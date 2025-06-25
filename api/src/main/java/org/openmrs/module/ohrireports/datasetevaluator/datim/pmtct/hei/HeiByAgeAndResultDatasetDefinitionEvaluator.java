package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.dao.PMTCTEncounter;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.HeiAgeAndResultDatasetDefinition;
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

import java.util.Map;

@Handler(supports = { HeiAgeAndResultDatasetDefinition.class })
public class HeiByAgeAndResultDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EIDQuery eidQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HeiAgeAndResultDatasetDefinition dsd = (HeiAgeAndResultDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(dsd.getStartDate(), dsd.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		String COLUMN_ONE = "Result - Type";
		String COLUMN_TWO = "Age 0 <=  2";
		String COLUMN_THREE = "age 2 <= 12";
		
		DataSetRow positiveRow = new DataSetRow();
		positiveRow.addColumnValue(new DataSetColumn(COLUMN_ONE, COLUMN_ONE, String.class), "Positive");
		positiveRow.addColumnValue(new DataSetColumn(COLUMN_TWO, COLUMN_TWO, Integer.class),
		    getCount(0, 2, ConceptAnswer.POSITIVE));
		positiveRow.addColumnValue(new DataSetColumn(COLUMN_THREE, COLUMN_THREE, Integer.class),
		    getCount(2, 12, ConceptAnswer.POSITIVE));
		dataSet.addRow(positiveRow);
		
		DataSetRow negativeRow = new DataSetRow();
		negativeRow.addColumnValue(new DataSetColumn(COLUMN_ONE, COLUMN_ONE, String.class), "Negative");
		negativeRow.addColumnValue(new DataSetColumn(COLUMN_TWO, COLUMN_TWO, Integer.class),
		    getCount(0, 2, ConceptAnswer.NEGATIVE));
		negativeRow.addColumnValue(new DataSetColumn(COLUMN_THREE, COLUMN_THREE, Integer.class),
		    getCount(2, 12, ConceptAnswer.NEGATIVE));
		dataSet.addRow(negativeRow);
		
		return dataSet;
	}
	
	private int getCount(int minAge, int maxAge, String conceptUUID) {
		int count = 0;
		for (Map.Entry<Integer, PMTCTPatient> patientEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			PMTCTPatient patient = patientEntry.getValue();
			for (PMTCTEncounter encounter : patient.getEncounterList()) {
				if (encounter.getTestType().equals(conceptUUID)
				        && encounter.getDnaPcrResult().equals(ConceptAnswer.POSITIVE)
				        && (encounter.getAge() >= minAge && encounter.getAge() <= maxAge)) {
					count++;
				}
			}
		}
		return count;
	}
}
