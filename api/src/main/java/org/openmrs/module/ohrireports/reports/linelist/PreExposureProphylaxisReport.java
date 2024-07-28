package org.openmrs.module.ohrireports.reports.linelist;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.PreExposureProphylaxisDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ReportType.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.REPORT_VERSION;

@Component
public class PreExposureProphylaxisReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "8331586b-49db-4702-9cf4-e7f3957160b0";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- Pre Exposure Prophylaxis";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter startDate = new Parameter("startDate", "Start Date", Date.class);
		startDate.setRequired(false);
		Parameter startDateGC = new Parameter("startDateGC", " ", Date.class);
		startDateGC.setRequired(false);
		Parameter endDate = new Parameter("endDate", "End Date", Date.class);
		endDate.setRequired(false);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(startDate, startDateGC, endDate, endDateGC);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		PreExposureProphylaxisDataSetDefinition preExposureProphylaxisDataSetDefinition = new PreExposureProphylaxisDataSetDefinition();
		preExposureProphylaxisDataSetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("Pre-Exposure Prophylaxis",
		    EthiOhriUtil.map(preExposureProphylaxisDataSetDefinition));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("3971aeda-df81-4eb2-9037-03885d6316a3", reportDefinition);
		
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
