package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.eid;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.dao.PMTCTEncounter;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISEIDDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { HMISEIDDatasetDefinition.class })
public class HMISEIDDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EIDQuery eidQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HMISEIDDatasetDefinition hmiseidDatasetDefinition = (HMISEIDDatasetDefinition) dataSetDefinition;
		eidQuery.generateReport(hmiseidDatasetDefinition.getStartDate(), hmiseidDatasetDefinition.getEndDate(),
		    PMTCT_DATE_OF_SAMPLE_RECEIVED_BY_LAB);
		
		SimpleDataSet data = new SimpleDataSet(hmiseidDatasetDefinition, evalContext);
		RowBuilder rowBuilder = new RowBuilder();
		int positiveBelowTwoAge = getPositive(0, 2);
		int negativeBelowTwoAge = getNegative(0, 2);
		int positiveAboveTwoAge = getPositive(2, 12);
		int negativeAboveTwoAge = getNegative(2, 12);
		
		data.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.1",
		    "Number of HIV exposed infants who received an HIV test 0- 2 months of birth", ""));
		data.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.1.1", "Positive", positiveBelowTwoAge));
		data.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.1.2", "Negative", negativeBelowTwoAge));
		data.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.2",
		    "Total Number of infants within 12 month received virological test result", ""));
		data.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.2.1", "Positive", positiveAboveTwoAge));
		data.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.2.2", "Negative", negativeAboveTwoAge));
		
		return data;
	}
	
	private int getNegative(int minAge, int maxAge) {
		int count = 0;
		for (Map.Entry<Integer, PMTCTPatient> patientEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			PMTCTPatient patient = patientEntry.getValue();
			for (PMTCTEncounter encounter : patient.getEncounterList()) {
				if (encounter.getTestType().equals(PMTCT_INITIAL_TEST) && encounter.getDnaPcrResult().equals(NEGATIVE)
				        && (encounter.getAge() >= minAge && encounter.getAge() <= maxAge)) {
					count++;
				}
			}
		}
		return count;
	}
	
	private int getPositive(int minAge, int maxAge) {
		int count = 0;
		for (Map.Entry<Integer, PMTCTPatient> patientEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			PMTCTPatient patient = patientEntry.getValue();
			for (PMTCTEncounter encounter : patient.getEncounterList()) {
				if (encounter.getTestType().equals(PMTCT_INITIAL_TEST) && encounter.getDnaPcrResult().equals(POSITIVE)
				        && (encounter.getAge() >= minAge && encounter.getAge() <= maxAge)) {
					count++;
				}
			}
		}
		return count;
	}
}
