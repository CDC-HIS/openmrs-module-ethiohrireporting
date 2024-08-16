package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSAutoCalcDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TX_PVLSAutoCalcDatasetDefinition.class })
public class TX_PVLSAutoCalDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private static final int _VALID_MONTHS_OF_VIRAL_LOAD_TEST = 12;
	
	@Autowired
	private VlQuery vlQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Date start, end;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TX_PVLSAutoCalcDatasetDefinition txDatasetDefinition = (TX_PVLSAutoCalcDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (!txDatasetDefinition.getHeader()) {
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(txDatasetDefinition.getEndDate());
			calendar.add(Calendar.MONTH, -_VALID_MONTHS_OF_VIRAL_LOAD_TEST);
			start = calendar.getTime();
			end = txDatasetDefinition.getEndDate();
			
			List<Integer> baseEncounters = encounterQuery.getEncounters(
			    Collections.singletonList(FollowUpConceptQuestions.DATE_VIRAL_TEST_RESULT_RECEIVED), start,
			    txDatasetDefinition.getEndDate());
			
			vlQuery.loadInitialCohort(start, end, baseEncounters);
			Cohort cohort;
			if (txDatasetDefinition.getIncludeUnSuppressed()) {
				cohort = vlQuery.cohort;
			} else {
				vlQuery.setSupperessedCohort(vlQuery.getViralLoadSuppressed(Arrays.asList(
				    ConceptAnswer.HIV_VIRAL_LOAD_SUPPRESSED, ConceptAnswer.HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA)));
				
				cohort = vlQuery.supperessedCohort;
			}
			
			DataSetRow setRow = new DataSetRow();
			setRow.addColumnValue(new DataSetColumn(txDatasetDefinition.getType(), txDatasetDefinition.getType(),
			        String.class), cohort.getMemberIds().size());
			dataSet.addRow(setRow);
		}
		return dataSet;
	}
	
}
