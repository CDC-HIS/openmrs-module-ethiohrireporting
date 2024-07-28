package org.openmrs.module.ohrireports.reports.linelist;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.DSDDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ReportType.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.REPORT_VERSION;

@Component
public class DSDReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "35c6f69d-4813-4310-a859-2f0acb5de998";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- DSD ";
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
		
		DSDDataSetDefinition dsdDataSetDefinition = new DSDDataSetDefinition();
		dsdDataSetDefinition.addParameters(getParameters());
		
		reportDefinition.addDataSetDefinition("DSD ", EthiOhriUtil.map(dsdDataSetDefinition));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("838da59b-1f05-4f88-8658-5ee9aa3f426c", reportDefinition);
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
