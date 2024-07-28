package org.openmrs.module.ohrireports.reports.linelist;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MLDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ReportType.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.REPORT_VERSION;

@Component
public class MLReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "b6cdc1b5-fb53-41c6-b027-2e492db8fb3e";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- TX_ML";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		return getDateRangeParameters();
		
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		MLDataSetDefinition mlDataSetDefinition = new MLDataSetDefinition();
		mlDataSetDefinition.addParameters(getParameters());
		
		reportDefinition.addDataSetDefinition("TX-ML Line list Report ", EthiOhriUtil.map(mlDataSetDefinition));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("646e6c28-2d1e-4e8b-b51a-fcb0ce16033b", reportDefinition);
		return Collections.singletonList(design);
	}
	
	@Override
	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
		return null;
	}
	
	@Override
	public String getVersion() {
		return REPORT_VERSION;
	}
	
	private List<Parameter> getDateRangeParameters() {
		return EthiOhriUtil.getDateRangeParameters();
	}
}
