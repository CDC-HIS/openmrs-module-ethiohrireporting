package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CXCA_FIRST_TIME_SCREENING;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CXCA_TYPE_OF_SCREENING_POST_TREATMENT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CXCA_TYPE_OF_SCREENING_RESCREEN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATIM_REPORT;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCAAutoCalculateDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCADatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CXCASCRNReport implements ReportManager {
	
	private EncounterType followUpEncounter;
	
	private Concept cxcaFirstTimeScreeningConcept, cxcaRescreeningAfterNegativeResultOneYear,
	        cxcaPostScreeningAfterTreatmentConcept;
	
	@Autowired
	private ConceptService conceptService;
	
	@Override
	public String getUuid() {
		return "63130038-c829-478a-9925-5ad21e024c4e";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "- CXCA_SCRN";
	}
	
	@Override
	public String getDescription() {
		return "Number of HIV-positive women on ART screened for cervical cancer";
	}
	
	@Override
	public List<Parameter> getParameters() {
		return EthiOhriUtil.getDateRangeParameters();
	}
	
	private void loadConcepts() {
		cxcaFirstTimeScreeningConcept = conceptService.getConceptByUuid(CXCA_FIRST_TIME_SCREENING);
		cxcaPostScreeningAfterTreatmentConcept = conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
		cxcaRescreeningAfterNegativeResultOneYear = conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING_RESCREEN);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		followUpEncounter = Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		loadConcepts();
		CXCAAutoCalculateDatasetDefinition tbADataSet = new CXCAAutoCalculateDatasetDefinition();
		tbADataSet.addParameters(getParameters());
		tbADataSet.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Number of HIV-positive women on ART screened for cervical cancer.",
		    EthiOhriUtil.map(tbADataSet));
		
		CXCADatasetDefinition firstTimeScreeningCxcaDatasetDefinition = new CXCADatasetDefinition();
		firstTimeScreeningCxcaDatasetDefinition.setScreeningType(cxcaFirstTimeScreeningConcept);
		firstTimeScreeningCxcaDatasetDefinition.addParameters(getParameters());
		firstTimeScreeningCxcaDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("First time screened for cervical cancer",
		    EthiOhriUtil.map(firstTimeScreeningCxcaDatasetDefinition));
		
		CXCADatasetDefinition rescreenDatasetDefinition = new CXCADatasetDefinition();
		rescreenDatasetDefinition.setScreeningType(cxcaRescreeningAfterNegativeResultOneYear);
		rescreenDatasetDefinition.addParameters(getParameters());
		rescreenDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Rescreen after pervious negative or suspected cancer",
		    EthiOhriUtil.map(rescreenDatasetDefinition));
		
		CXCADatasetDefinition postTreatmentDatasetDefinition = new CXCADatasetDefinition();
		postTreatmentDatasetDefinition.setScreeningType(cxcaPostScreeningAfterTreatmentConcept);
		postTreatmentDatasetDefinition.addParameters(getParameters());
		postTreatmentDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Post-treatment follow-up ",
		    EthiOhriUtil.map(postTreatmentDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("96394090-31cb-4c4f-8234-9f10007de51f", reportDefinition);
		
		return Arrays.asList(design);
		
	}
	
	
	@Override
	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
		return null;
	}
	
	@Override
	public String getVersion() {
		return REPORT_VERSION;
	}
	
}
