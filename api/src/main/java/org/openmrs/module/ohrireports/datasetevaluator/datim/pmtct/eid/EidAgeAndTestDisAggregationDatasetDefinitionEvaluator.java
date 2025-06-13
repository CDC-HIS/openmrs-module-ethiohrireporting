package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct.eid;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.dao.PMTCTEncounter;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.constants.PMTCTConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.EidAgeAndTestDisAggregationDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Handler(supports = { EidAgeAndTestDisAggregationDatasetDefinition.class })
public class EidAgeAndTestDisAggregationDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EIDQuery eidQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		EidAgeAndTestDisAggregationDatasetDefinition _datasetDefinition = (EidAgeAndTestDisAggregationDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(_datasetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(_datasetDefinition.getStartDate(),
		    _datasetDefinition.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		DataSetRow firstTestRow = new DataSetRow();
		String COLUMN_ONE = "Test Type";
		firstTestRow.addColumnValue(new DataSetColumn(COLUMN_ONE, COLUMN_ONE, String.class), "First Test");
		String COLUMN_TWO = "0>=2 Months";
		firstTestRow.addColumnValue(new DataSetColumn(COLUMN_TWO, COLUMN_TWO, Integer.class),
		    getTotalForBelowTwo(Collections.singletonList(PMTCTConceptQuestions.PMTCT_INITIAL_TEST)));
		String COLUMN_THREE = "2>=12 Months";
		firstTestRow.addColumnValue(new DataSetColumn(COLUMN_THREE, COLUMN_THREE, Integer.class),
		    getTotalForAboveTwo(Collections.singletonList(PMTCTConceptQuestions.PMTCT_INITIAL_TEST)));
		dataSet.addRow(firstTestRow);
		
		DataSetRow secondTestRow = new DataSetRow();
		secondTestRow.addColumnValue(new DataSetColumn(COLUMN_ONE, COLUMN_ONE, String.class), "Second Test");
		secondTestRow.addColumnValue(new DataSetColumn(COLUMN_TWO, COLUMN_TWO, Integer.class), getTotalForBelowTwo(Arrays
		        .asList(PMTCTConceptQuestions.PMTCT_DIAGNOSTIC_REPEAT_TEST,
		            PMTCTConceptQuestions.PMTCT_NINE_MONTH_FOR_PREVIOUS_NEGATIVE_TEST)));
		secondTestRow.addColumnValue(new DataSetColumn(COLUMN_THREE, COLUMN_THREE, Integer.class),
		    getTotalForAboveTwo(Arrays.asList(PMTCTConceptQuestions.PMTCT_DIAGNOSTIC_REPEAT_TEST,
		        PMTCTConceptQuestions.PMTCT_NINE_MONTH_FOR_PREVIOUS_NEGATIVE_TEST)));
		dataSet.addRow(secondTestRow);
		
		return dataSet;
	}
	
	private int getTotalForBelowTwo(List<String> PMTCTTestType) {
		
		int count = 0;
		for (Map.Entry<Integer, PMTCTPatient> patientEncounterEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			for (PMTCTEncounter encounter : patientEncounterEntry.getValue().getEncounterList()) {
				if (PMTCTTestType.contains(encounter.getTestType()) && encounter.getAge() <= 2) {
					count++;
				}
			}
		}
		return count;
		
	}
	
	private int getTotalForAboveTwo(List<String> PMTCTTestType) {
		
		int count = 0;
		for (Map.Entry<Integer, PMTCTPatient> patientEncounterEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			for (PMTCTEncounter encounter : patientEncounterEntry.getValue().getEncounterList()) {
				if (PMTCTTestType.contains(encounter.getTestType()) && encounter.getAge() <= 12) {
					count++;
				}
			}
		}
		return count;
		
	}
}
