package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCAAutoCalculateDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCADatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;

//@Component
public class CXCASCRNReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "63130038-c829-478a-9925-5ad21e024c4e";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_TESTING + "- CXCA_SCRN";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		return EthiOhriUtil.getDateRangeParameters();
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		CXCAAutoCalculateDatasetDefinition headerDefinition = new CXCAAutoCalculateDatasetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: CXCA_SCRN");
		reportDefinition.addDataSetDefinition("DSD: CXCA_SCRN", EthiOhriUtil.map(headerDefinition));
		
		CXCAAutoCalculateDatasetDefinition tbADataSet = new CXCAAutoCalculateDatasetDefinition();
		tbADataSet.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("Number of HIV-positive women on ART screened for cervical cancer. Numerator "
		        + "will auto-calculate from the Age/Result/Screening Visit Type", EthiOhriUtil.map(tbADataSet));
		
		CXCADatasetDefinition firstTimeScreeningCxcaDatasetDefinition = new CXCADatasetDefinition();
		firstTimeScreeningCxcaDatasetDefinition.setScreeningType(ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE);
		firstTimeScreeningCxcaDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("First time screened for cervical cancer",
		    EthiOhriUtil.map(firstTimeScreeningCxcaDatasetDefinition));
		
		CXCADatasetDefinition reScreenDatasetDefinition = new CXCADatasetDefinition();
		reScreenDatasetDefinition.setScreeningType(ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN);
		reScreenDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("Re-screen after previous negative or suspected cancer",
		    EthiOhriUtil.map(reScreenDatasetDefinition));
		
		CXCADatasetDefinition postTreatmentDatasetDefinition = new CXCADatasetDefinition();
		postTreatmentDatasetDefinition.setScreeningType(ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
		postTreatmentDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("Post-treatment follow-up ", EthiOhriUtil.map(postTreatmentDatasetDefinition));
		
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
