package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.eid;

import org.openmrs.module.ohrireports.api.dao.PMTCTEncounter;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.PMTCTConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@Scope("prototype")
public class HMISEIDEvaluator {
	
	@Autowired
	private EIDQuery eidQuery;
	
	public void buildDataset(Date start, Date end, SimpleDataSet dataset) {
		RowBuilder rowBuilder = new RowBuilder();
		eidQuery.generateReportForHMIS(start, end, PMTCTConceptQuestions.PMTCT_SAMPLE_COLLECTION_DATE);
		
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.",
		    "Percentage of  HIV exposed infants who received a virologic HIV test (sample collected) within 12 month ",
		    eidQuery.getPatientEncounterHashMap().size()));
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1",
		    "Number of HIV exposed infants who received a virologic HIV test (sample collected) 0- 2 months of birth",
		    getByAge(0, 2)));
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.2",
		    "Number of HIV exposed infants who received a virologic HIV test (sample collected) 2-12 months of birth",
		    getByAge(2, 12)));
		
		int positiveBelowTwoAge = getPositive(0, 2);
		int negativeBelowTwoAge = getNegative(0, 2);
		int positiveAboveTwoAge = getPositive(2, 12);
		int negativeAboveTwoAge = getNegative(2, 12);
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1",
		    "Total Number of infants within 12 month received virological test result", ""));
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.1",
		    "Number of HIV exposed infants who received an HIV test 0-2 months of birth", positiveBelowTwoAge
		            + negativeBelowTwoAge));
		
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.1.1", "Positive", positiveBelowTwoAge));
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.1.2", "Negative", negativeBelowTwoAge));
		
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.2",
		    "Number of HIV exposed infants who received an HIV test 2-12 months of birth", positiveAboveTwoAge
		            + negativeAboveTwoAge));
		
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.2.1", "Positive", positiveAboveTwoAge));
		dataset.addRow(rowBuilder.buildDatasetColumn("MTCT_HEI_EID.1.2.2", "Negative", negativeAboveTwoAge));
		
	}
	
	private int getNegative(int minAge, int maxAge) {
		int count = 0;
		for (Map.Entry<Integer, PMTCTPatient> patientEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			PMTCTPatient patient = patientEntry.getValue();
			for (PMTCTEncounter encounter : patient.getEncounterList()) {
				if (encounter.getTestType().equals(PMTCTConceptQuestions.PMTCT_INITIAL_TEST)
				        && encounter.getDnaPcrResult().equals(ConceptAnswer.NEGATIVE)
				        && (encounter.getAge() >= minAge && encounter.getAge() <= maxAge)) {
					count++;
				}
			}
		}
		return count;
	}
	
	private int getByAge(int minAge, int maxAge) {
		int count = 0;
		for (Map.Entry<Integer, PMTCTPatient> patientEntry : eidQuery.getPatientEncounterHashMap().entrySet()) {
			PMTCTPatient patient = patientEntry.getValue();
			for (PMTCTEncounter encounter : patient.getEncounterList()) {
				if ((encounter.getAge() >= minAge && encounter.getAge() <= maxAge)) {
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
				if (encounter.getTestType().equals(PMTCTConceptQuestions.PMTCT_INITIAL_TEST)
				        && encounter.getDnaPcrResult().equals(ConceptAnswer.POSITIVE)
				        && (encounter.getAge() >= minAge && encounter.getAge() <= maxAge)) {
					count++;
				}
			}
		}
		return count;
	}
}
