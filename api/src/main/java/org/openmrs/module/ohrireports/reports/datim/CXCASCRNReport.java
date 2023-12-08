package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCAAutoCalculateDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCADatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class CXCASCRNReport implements ReportManager {
	
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
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		CXCAAutoCalculateDatasetDefinition tbADataSet = new CXCAAutoCalculateDatasetDefinition();
		tbADataSet.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("Number of HIV-positive women on ART screened for cervical cancer.",
		    EthiOhriUtil.map(tbADataSet));
		
		CXCADatasetDefinition firstTimeScreeningCxcaDatasetDefinition = new CXCADatasetDefinition();
		firstTimeScreeningCxcaDatasetDefinition.setScreeningType(CXCA_FIRST_TIME_SCREENING_TYPE);
		firstTimeScreeningCxcaDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("First time screened for cervical cancer",
		    EthiOhriUtil.map(firstTimeScreeningCxcaDatasetDefinition));
		
		CXCADatasetDefinition rescreenDatasetDefinition = new CXCADatasetDefinition();
		rescreenDatasetDefinition.setScreeningType(CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
		rescreenDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("Rescreen after pervious negative or suspected cancer",
		    EthiOhriUtil.map(rescreenDatasetDefinition));
		
		CXCADatasetDefinition postTreatmentDatasetDefinition = new CXCADatasetDefinition();
		postTreatmentDatasetDefinition.setScreeningType(CXCA_TYPE_OF_SCREENING_RESCREEN);
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
