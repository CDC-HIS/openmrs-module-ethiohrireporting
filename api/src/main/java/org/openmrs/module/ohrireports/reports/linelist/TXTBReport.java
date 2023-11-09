package org.openmrs.module.ohrireports.reports.linelist;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TXTBDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

@Component
public class TXTBReport implements ReportManager {
	
	public static String numerator = "TB Treatment";
	
	public static String denominator = "TB Screening";
	
	@Override
	public String getUuid() {
		return "0b3f8468-29af-47b6-92d3-f9d3cc4d4405";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "-TX_TB";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		
		Parameter types = new Parameter("type", "TB Treatment Status", String.class);
		types.addToWidgetConfiguration("codedOptions", numerator + "," + denominator);
		
		types.setRequired(false);
		Parameter startDate = new Parameter("startDate", "Start Date", Date.class);
		startDate.setRequired(false);
		Parameter startDateGC = new Parameter("startDateGC", " ", Date.class);
		startDateGC.setRequired(false);
		Parameter endDate = new Parameter("endDate", "End Date", Date.class);
		endDate.setRequired(false);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(startDate, startDateGC, endDate, endDateGC, types);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		TXTBDataSetDefinition txTBdataSetDefinition = new TXTBDataSetDefinition();
		txTBdataSetDefinition.addParameters(getParameters());
		
		reportDefinition.addDataSetDefinition("TB-Treatment", EthiOhriUtil.map(txTBdataSetDefinition, "type=${type}"));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		//
		ReportDesign design = ReportManagerUtil.createExcelDesign("41f952ea-e777-44c4-8f54-303517dd6622", reportDefinition);
		
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
