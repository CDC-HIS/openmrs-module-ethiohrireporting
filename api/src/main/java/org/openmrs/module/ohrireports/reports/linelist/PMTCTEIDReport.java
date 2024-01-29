package org.openmrs.module.ohrireports.reports.linelist;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.EidDatasetDefinition;
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

import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;

@Component
public class PMTCTEIDReport implements ReportManager {
	
	String[] param = { "Test Indication", "Rapid antibody result" };
	
	@Override
	public String getUuid() {
		return "e5a3d79f-3ea3-49d0-a53f-a450f4cb7cd1";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- PMTCT_EID";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter types = new Parameter("reportType", "Select report type", String.class);
		types.addToWidgetConfiguration("codedOptions", StringUtils.join(param, ","));
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
		
		EidDatasetDefinition dataSetDefinition = new EidDatasetDefinition();
		dataSetDefinition.addParameters(getParameters());
		//
		reportDefinition.addDataSetDefinition("PMTCT_EID", EthiOhriUtil.map(dataSetDefinition, "reportType=${reportType}"));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("26ffe2c2-dac7-4788-961b-f4d1fb5fdbb5", reportDefinition);
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
}
