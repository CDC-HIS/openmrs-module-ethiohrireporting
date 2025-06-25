package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.dao.PMTCTEncounter;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.constants.PMTCTConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.HeiPositiveLinkedDatasetDefinition;
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
import java.util.List;
import java.util.Map;

@Handler(supports = { HeiPositiveLinkedDatasetDefinition.class })
public class HeiPositiveLinkedDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EIDQuery eidQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HeiPositiveLinkedDatasetDefinition dsd = (HeiPositiveLinkedDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(dsd.getStartDate(), dsd.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		String COLUMN_ONE = "Result - Type";
		String COLUMN_TWO = "Age 0 <=  2";
		String COLUMN_THREE = "Age 2 <= 12";
		
		DataSetRow positiveRow = new DataSetRow();
		positiveRow.addColumnValue(new DataSetColumn(COLUMN_ONE, COLUMN_ONE, String.class), "Positive");
		positiveRow.addColumnValue(new DataSetColumn(COLUMN_TWO, COLUMN_TWO, Integer.class), getCount(0, 2));
		positiveRow.addColumnValue(new DataSetColumn(COLUMN_THREE, COLUMN_THREE, Integer.class), getCount(2, 12));
		dataSet.addRow(positiveRow);
		
		return dataSet;
	}
	
	private int getCount(int minAge, int maxAge) {
		int count = 0;
		List<String> conceptUUIDS = Arrays.asList(PMTCTConceptQuestions.PMTCT_POSITIVE_WITH_OUTSIDE_FACILITY,
		    PMTCTConceptQuestions.PMTCT_POSITIVE_WITHIN_FACILITY);
		for (Map.Entry<Integer, PMTCTPatient> patientEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			PMTCTPatient patient = patientEntry.getValue();
			for (PMTCTEncounter encounter : patient.getEncounterList()) {
				if (conceptUUIDS.contains(encounter.getDnaPcrResult())
				        && (encounter.getAge() >= minAge && encounter.getAge() <= maxAge)) {
					count++;
				}
			}
		}
		return count;
	}
}
