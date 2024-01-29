package org.openmrs.module.ohrireports.datasetevaluator.linelist.pmtct;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.dao.PMTCTEncounter;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.EidDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { EidDatasetDefinition.class })
public class EidDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EidLineListQuery eidQuery;
	List<PMTCTPatientRapidAntiBody> pmtctPatientRapidAntiBodies = new ArrayList<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		EidDatasetDefinition eidDatasetDefinition = (EidDatasetDefinition) dataSetDefinition;
		
		DataSetRow row = new DataSetRow();
		
		row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "Total");
		if(Objects.equals(eidDatasetDefinition.getReportType(), "Test Indication")){
			eidQuery.generateReport(eidDatasetDefinition.getStartDate(), eidDatasetDefinition.getEndDate());
			
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), eidQuery.getPMTCTPatient().size());
			dataSet.addRow(row);
			buildColumn(dataSet);
			
			
		}else {
			pmtctPatientRapidAntiBodies = eidQuery.getRapidAntiBodyWithPatient(eidDatasetDefinition.getStartDate(),eidDatasetDefinition.getEndDate());
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class),pmtctPatientRapidAntiBodies.size());
			dataSet.addRow(row);
			buildColumnForRapidAntiBody(dataSet);
			
		}
		
		
		return dataSet;
	}
	
	private void buildColumnForRapidAntiBody(SimpleDataSet dataSet) {
		DataSetRow row;
		HashMap<Integer,Object> heiCodeHashMap = eidQuery.getResult(PMTCT_HEI_CODE,pmtctPatientRapidAntiBodies.stream().map(PMTCTPatientRapidAntiBody::getPersonId).collect(Collectors.toList()));
		heiCodeHashMap.forEach((k, p) -> {
			pmtctPatientRapidAntiBodies.forEach(d -> {
				if (d.getPersonId() == k) {
					d.setHeiCode((String) p);
				}
			});
		});
		for (PMTCTPatientRapidAntiBody pmtctPatientRapidAntiBody:pmtctPatientRapidAntiBodies){
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("Name", "Name", String.class), pmtctPatientRapidAntiBody.getFullName());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), pmtctPatientRapidAntiBody.getGender());
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), pmtctPatientRapidAntiBody.getAge());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), pmtctPatientRapidAntiBody.getMrn());
			row.addColumnValue(new DataSetColumn("HEI Code", "HEI Code", String.class), pmtctPatientRapidAntiBody.getHeiCode());
			row.addColumnValue(new DataSetColumn("followUpDate", "Follow Up Date", Date.class),	pmtctPatientRapidAntiBody.getFollowUpDate());
			row.addColumnValue(new DataSetColumn("RapidAntibodyTestResult", "Rapid Antibody Test Result", String.class),	pmtctPatientRapidAntiBody.getFollowUpDate());
			
			dataSet.addRow(row);
		}
		
	}
	
	private void buildColumn(SimpleDataSet dataSet) {
		DataSetRow row;
		for (Map.Entry<Integer, PMTCTPatient> patientEntry : eidQuery.getPMTCTPatient().entrySet()) {
			
			PMTCTPatient pmtctPatient = patientEntry.getValue();
			for (PMTCTEncounter pmtctEncounter : pmtctPatient.getEncounterList()) {
				row = new DataSetRow();
				row.addColumnValue(new DataSetColumn("Name", "Name", String.class), pmtctPatient.getFullName());
				row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), pmtctPatient.getGender());
				row.addColumnValue(new DataSetColumn("Age", "Age", String.class), pmtctEncounter.getAge());
				row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), pmtctPatient.getMrn());
				row.addColumnValue(new DataSetColumn("HEI Code", "HEI Code", String.class), pmtctPatient.getHeiCode());
				row.addColumnValue(new DataSetColumn("Infant on BF?", "Infant on BF?", String.class), "--");
				row.addColumnValue(new DataSetColumn("ARV Prophylaxis", "ARV Prophylaxis", String.class),
				    pmtctEncounter.getArvProphylaxis());
				row.addColumnValue(new DataSetColumn("Maternal ART Status", "Maternal ART Status", String.class),
				    pmtctEncounter.getMaternalArtStatus());
				row.addColumnValue(new DataSetColumn("Test Indication", "Test Indication", String.class),
				    pmtctEncounter.getTestIndication());
				row.addColumnValue(new DataSetColumn("Specimen Type", "Specimen Type", String.class),
				    pmtctEncounter.getSpecimenType());
				row.addColumnValue(new DataSetColumn("Date of Sample Collection (E.C)", "Date of Sample Collection (E.C)",
				        String.class), pmtctEncounter.getDateOfSampleCollection());
				row.addColumnValue(new DataSetColumn("DNA PCR Result", "DNA PCR Result", String.class),
				    pmtctEncounter.getDnaPcrResult());
				row.addColumnValue(new DataSetColumn("Date of Result Received by H.F (E.C)",
				        "Date of Result Received by H.F (E.C)", String.class), pmtctEncounter.getDateOfResultByHf());
				row.addColumnValue(new DataSetColumn("Date of DBS referral to regional lab (E.C)",
				        "Date of DBS referral to regional lab (E.C)", String.class), pmtctEncounter
				        .getDateOfDbsReferralRegionalLab());
				row.addColumnValue(new DataSetColumn("Name of Testing Lab.", "Name of Testing Lab.", String.class),
				    pmtctEncounter.getNameOfTestingLab());
				row.addColumnValue(new DataSetColumn("Date of Sample received by Lab.", "Date of Sample received by Lab.",
				        String.class), pmtctEncounter.getDateOfSampleReceived());
				row.addColumnValue(new DataSetColumn("Sample Quality", "Sample Quality", String.class),
				    pmtctEncounter.getSampleQuality());
				row.addColumnValue(new DataSetColumn("Reason for Sample rejection/test not done",
				        "Reason for Sample rejection/test not done", String.class), pmtctEncounter
				        .getReasonForSampleRejection());
				row.addColumnValue(new DataSetColumn("Date test performed by Lab.", "Date test performed by Lab.",
				        String.class), pmtctEncounter.getDateTestPerformedByLab());
				row.addColumnValue(new DataSetColumn("Date test performed by Lab.", "Date test performed by Lab.",
				        String.class), pmtctEncounter.getPlatformUsed());
				dataSet.addRow(row);
				
			}
			
		}
	}
	

}
