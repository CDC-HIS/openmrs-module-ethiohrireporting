package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATE_VIRAL_TEST_RESULT_RECEIVED;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
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
	
	private PatientQueryService patientQueryService;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	private Date start, end = new Date();
	
	private TX_PVLSDatasetDefinition txDatasetDefinition;
	
	private String desc = "Disaggregated by Age / Sex / (Fine Disaggregated).";
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		txDatasetDefinition = (TX_PVLSDatasetDefinition) dataSetDefinition;
		patientQueryService = Context.getService(PatientQueryService.class);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(txDatasetDefinition.getEndDate());
		calendar.add(Calendar.MONTH, -_VALID_MONTHS_OF_VIRAL_LOAD_TEST);
		start = calendar.getTime();
		end = txDatasetDefinition.getEndDate();
		
		if (vlQuery.start != start || vlQuery.end != end || vlQuery.getVlTakenEncounters().isEmpty()) {
			List<Integer> laIntegers = patientQueryService.getBaseEncounters(DATE_VIRAL_TEST_RESULT_RECEIVED, start, end);
			
			vlQuery.loadInitialCohort(start, end, laIntegers);
		}
		
		Cohort _cohort = txDatasetDefinition.getIncludeUnSuppressed() ? vlQuery.cohort : vlQuery.getViralLoadSuppressed();
		
		// #region Routing test indication
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow routineDataSetRow = new DataSetRow();
		routineDataSetRow.addColumnValue(new DataSetColumn("label", "", String.class), desc);
		dataSet.addRow(routineDataSetRow);
		
		List<Person> persons = patientQueryService.getPersons(_cohort);
		_AggregateBuilder.setPersonList(persons);
		
		DataSetRow femaleRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(femaleRow, "F");
		dataSet.addRow(femaleRow);
		
		DataSetRow maleRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(maleRow, "M");
		dataSet.addRow(maleRow);
		// #endregion
		
		// // #region Target test indication
		// DataSetRow targetDataSetRow = new DataSetRow();
		// targetDataSetRow.addColumnValue(new DataSetColumn("label", "", String.class), targetDesc);
		// dataSet.addRow(targetDataSetRow);
		
		// persons = patientQueryService.getPersons(vlQuery.getTargetViralLoad(_cohort));
		// _AggregateBuilder.setPersonList(persons);
		
		// DataSetRow femaleTargetRow = new DataSetRow();
		// _AggregateBuilder.buildDataSetColumn(femaleTargetRow, "F");
		// dataSet.addRow(femaleTargetRow);
		
		// DataSetRow maleTargetRow = new DataSetRow();
		// _AggregateBuilder.buildDataSetColumn(maleTargetRow, "M");
		// dataSet.addRow(maleTargetRow);
		// // #endregion
		
		return dataSet;
	}
	
}
