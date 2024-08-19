package org.openmrs.module.ohrireports.reports.linelist;

import org.openmrs.module.ohrireports.datasetdefinition.linelist.TBPrevDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
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

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.REPORT_VERSION;
import static org.openmrs.module.ohrireports.constants.ReportType.LINE_LIST_REPORT;

@Component
public class TPTPrevSamiAnnualReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "5bb6b32b-b024-4c5d-b8d7-8c5a08cb914b";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- TPT Prev Sami";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter tptStatus = new Parameter("tptStatus", "TPT Status", String.class);
		tptStatus.addToWidgetConfiguration("codedOptions", "start,end");
			Parameter endDate = new Parameter("endDate", "End Date", Date.class);
		endDate.setRequired(false);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(endDate, endDateGC, tptStatus);
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
		
		ReportDesign design = ReportManagerUtil.createExcelDesign("ea38de54-f0c4-44f4-80f3-29850dad5d6a", reportDefinition);
		
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
