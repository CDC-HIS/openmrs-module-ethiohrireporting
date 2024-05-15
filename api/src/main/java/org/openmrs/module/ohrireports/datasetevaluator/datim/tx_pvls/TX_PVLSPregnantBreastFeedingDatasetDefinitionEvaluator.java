package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATE_VIRAL_TEST_RESULT_RECEIVED;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSPregnantBreastfeedingDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TX_PVLSPregnantBreastfeedingDatasetDefinition.class })
public class TX_PVLSPregnantBreastFeedingDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private static final int _VALID_MONTHS_OF_VIRAL_LOAD_TEST = 12;
	
	@Autowired
	private VlQuery vlQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Date start, end = new Date();
	
	private TX_PVLSPregnantBreastfeedingDatasetDefinition txDatasetDefinition;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		txDatasetDefinition = (TX_PVLSPregnantBreastfeedingDatasetDefinition) dataSetDefinition;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(txDatasetDefinition.getEndDate());
		calendar.add(Calendar.MONTH, -_VALID_MONTHS_OF_VIRAL_LOAD_TEST);
		start = calendar.getTime();
		end = txDatasetDefinition.getEndDate();
		
		if (vlQuery.start != start || vlQuery.end != end || vlQuery.getVlTakenEncounters().isEmpty()) {
			List<Integer> laIntegers = encounterQuery.getEncounters(Arrays.asList(DATE_VIRAL_TEST_RESULT_RECEIVED), start,
			    end);
			vlQuery.loadInitialCohort(start, end, laIntegers);
		}
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		Cohort pregnantCohort = vlQuery.getPregnantCohort(vlQuery.cohort);
		Cohort breatFeedingCohort = vlQuery.getBreastFeedingCohort(vlQuery.cohort);
		
		DataSetRow typeRow = new DataSetRow();
		typeRow.addColumnValue(new DataSetColumn("type", "", String.class), "Viral load count");
		typeRow.addColumnValue(new DataSetColumn("pregnant", "Pregnant", Integer.class), pregnantCohort.size());
		typeRow.addColumnValue(new DataSetColumn("breastFeeding", "Breast  Feeding Cohort", Integer.class),
		    breatFeedingCohort.size());
		dataSet.addRow(typeRow);
		
		return dataSet;
	}
	
}
