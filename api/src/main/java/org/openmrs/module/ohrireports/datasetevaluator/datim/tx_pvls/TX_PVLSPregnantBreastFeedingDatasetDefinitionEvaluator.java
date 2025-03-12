package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_pvls;

import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
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
		Cohort cohort;
		if(txDatasetDefinition.getIncludeUnSuppressed()){
			cohort = vlQuery.cohort;
		}else{
			cohort = vlQuery.suppressedCohort;
		}

		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		Cohort pregnantCohort = vlQuery.getPregnantCohort(cohort);
		int breastFeedingCohort =
                (int) vlQuery.getByResult(
                                FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD, cohort, vlQuery.getVlTakenEncounters())
                        .entrySet().stream().
                        filter(integerObjectEntry -> {
                            return Objects.nonNull(integerObjectEntry.getValue()) &&
									integerObjectEntry.getValue().toString().equalsIgnoreCase("yes");
                        }).count();

		
		DataSetRow typeRow = new DataSetRow();
		typeRow.addColumnValue(new DataSetColumn("type", "", String.class), "Viral load count");
		typeRow.addColumnValue(new DataSetColumn("pregnant", "Pregnant", Integer.class), pregnantCohort.size());
		typeRow.addColumnValue(new DataSetColumn("breastFeeding", "Breast  Feeding", Integer.class),
		    breastFeedingCohort);
		dataSet.addRow(typeRow);
		
		return dataSet;
	}
}
