package org.openmrs.module.ohrireports.reports.linelist;

import org.openmrs.module.ohrireports.datasetdefinition.linelist.VLReceivedDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.VLSentDataSetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.REPORT_VERSION;
import static org.openmrs.module.ohrireports.constants.ReportType.LINE_LIST_REPORT;

//@Component
public class VlSentReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "b858371b-925c-48a1-b390-8440628d5583";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- VL Sent";
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
		
		VLSentDataSetDefinition vlDatasetDefinition = new VLSentDataSetDefinition();
		vlDatasetDefinition.addParameters(getParameters());
		
		reportDefinition.addDataSetDefinition("Viral Load Sent Report", EthiOhriUtil.map(vlDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		
		ReportDesign design = ReportManagerUtil.createExcelDesign("57f1826d-18dc-4132-af5e-fc2afac2340b", reportDefinition);
		
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
