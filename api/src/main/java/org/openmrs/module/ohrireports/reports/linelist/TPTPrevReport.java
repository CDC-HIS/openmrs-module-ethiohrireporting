package org.openmrs.module.ohrireports.reports.linelist;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TBPrevDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

@Component
public class TPTPrevReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "f65697f6-7d48-4a10-b3b4-9d0e24317da7";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- TPT";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter tptStatus = new Parameter("tptStatus", "TPT Status", String.class);
		tptStatus.addToWidgetConfiguration("codedOptions", "start,end");
		
		Parameter startDate = new Parameter("startDate", "Start Date", Date.class);
		startDate.setRequired(false);
		Parameter startDateGC = new Parameter("startDateGC", " ", Date.class);
		startDateGC.setRequired(false);
		Parameter endDate = new Parameter("endDate", "End Date", Date.class);
		endDate.setRequired(false);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(startDate, startDateGC, endDate, endDateGC, tptStatus);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		TBPrevDatasetDefinition tbPrevDataSetDefinition = new TBPrevDatasetDefinition();
		tbPrevDataSetDefinition.addParameters(getParameters());
		
		reportDefinition.addDataSetDefinition("TPT Line List Report",
		    EthiOhriUtil.map(tbPrevDataSetDefinition, "tptStatus=${tptStatus}"));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		
		ReportDesign design = ReportManagerUtil.createExcelDesign("66364db5-5935-4608-8ecb-b6fc33467ead", reportDefinition);
		
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
