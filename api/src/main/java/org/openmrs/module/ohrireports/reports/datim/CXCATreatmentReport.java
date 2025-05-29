package org.openmrs.module.ohrireports.reports.datim;

import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_treatment.CxCaTreatmentAutoCalculateDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_treatment.CxCaTreatmentDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;

//@Component
public class CXCATreatmentReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "16686723-3994-4ae8-bee5-7870fa647cfb";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_TREATMENT + "- CXCA_TX";
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
		
		CxCaTreatmentAutoCalculateDatasetDefinition headerDefinition = new CxCaTreatmentAutoCalculateDatasetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: CXCA_TX");
		reportDefinition.addDataSetDefinition("DSD: CXCA_TX", EthiOhriUtil.map(headerDefinition));
		
		CxCaTreatmentAutoCalculateDatasetDefinition cxcaTreatmentDataSet = new CxCaTreatmentAutoCalculateDatasetDefinition();
		cxcaTreatmentDataSet.addParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "Number of women with a positive VIA screening test who are HIV-positive and on ART eligible for cryotherapy, "
		                    + "thermocoagulation or LEEP. Numerator will auto-calculate from the Age/Treatment Type/Screening Visit Type.",
		            EthiOhriUtil.map(cxcaTreatmentDataSet));
		
		CxCaTreatmentDatasetDefinition firstTimeScreeningCxCaTreatmentDatasetDefinition = new CxCaTreatmentDatasetDefinition();
		firstTimeScreeningCxCaTreatmentDatasetDefinition.setScreeningType(ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE);
		firstTimeScreeningCxCaTreatmentDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("First time screened for cervical cancer",
		    EthiOhriUtil.map(firstTimeScreeningCxCaTreatmentDatasetDefinition));
		
		CxCaTreatmentDatasetDefinition reScreenedDatasetDefinition = new CxCaTreatmentDatasetDefinition();
		reScreenedDatasetDefinition.setScreeningType(ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN);
		reScreenedDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("Rescreened after previous negative cancer",
		    EthiOhriUtil.map(reScreenedDatasetDefinition));
		
		CxCaTreatmentDatasetDefinition postTreatmentDatasetDefinition = new CxCaTreatmentDatasetDefinition();
		postTreatmentDatasetDefinition.setScreeningType(ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
		postTreatmentDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition("Post-treatment followup", EthiOhriUtil.map(postTreatmentDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("130e0d82-dc9d-4687-969c-87879ca77919", reportDefinition);
		
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
