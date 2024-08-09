package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED;
import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.FOLLOW_UP_DATE;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TX_PVLSDatasetDefinition.class })
public class TX_PVLSDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private static final int _VALID_MONTHS_OF_VIRAL_LOAD_TEST = 12;
	
	@Autowired
	private VlQuery vlQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	private Date end = new Date();
	
	private TX_PVLSDatasetDefinition txDatasetDefinition;
	
	private String desc = "Disaggregated by Age / Sex / (Fine Disaggregated).";
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_AggregateBuilder.clearTotal();
		txDatasetDefinition = (TX_PVLSDatasetDefinition) dataSetDefinition;
		PatientQueryService patientQueryService = Context.getService(PatientQueryService.class);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(txDatasetDefinition.getEndDate());
		calendar.add(Calendar.MONTH, -_VALID_MONTHS_OF_VIRAL_LOAD_TEST);
		Date start = calendar.getTime();
		end = txDatasetDefinition.getEndDate();
		
		if (vlQuery.start != start || vlQuery.end != end || vlQuery.getVlTakenEncounters().isEmpty()) {
			List<Integer> laIntegers = encounterQuery.getEncounters(Arrays.asList(DATE_VIRAL_TEST_RESULT_RECEIVED), start,
			    end);
			
			vlQuery.loadInitialCohort(start, end, laIntegers);
		}
		
		Cohort _cohort = txDatasetDefinition.getIncludeUnSuppressed() ? vlQuery.cohort : vlQuery
		        .getViralLoadSuppressed(Arrays.asList(ConceptAnswer.HIV_VIRAL_LOAD_SUPPRESSED,
		            ConceptAnswer.HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA));
		HashMap<Integer, Object> followUpHashSet = vlQuery.getObsValueDate(vlQuery.getVlTakenEncounters(), FOLLOW_UP_DATE,
		    _cohort);
		
		// #region Routing test indication
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow routineDataSetRow = new DataSetRow();
		routineDataSetRow.addColumnValue(new DataSetColumn("label", "", String.class), desc);
		dataSet.addRow(routineDataSetRow);
		
		List<Person> persons = patientQueryService.getPersons(_cohort);
		_AggregateBuilder.setFollowUpDate(followUpHashSet);
		
		_AggregateBuilder.setPersonList(persons);
		_AggregateBuilder.setLowerBoundAge(0);
		_AggregateBuilder.setUpperBoundAge(65);
		DataSetRow femaleRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumnWithFollowUpDate(femaleRow, "F");
		dataSet.addRow(femaleRow);
		
		DataSetRow maleRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumnWithFollowUpDate(maleRow, "M");
		dataSet.addRow(maleRow);
		
		DataSetRow total = new DataSetRow();
		_AggregateBuilder.buildDataSetColumnWithFollowUpDate(total, "T");
		dataSet.addRow(total);
		
		return dataSet;
	}
	
}
