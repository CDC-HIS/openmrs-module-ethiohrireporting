package org.openmrs.module.ohrireports.reports.linelist;

import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MissedAppointmentDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MonthlyVisitDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiopianDate;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;

@Component
public class MissedAppointmentReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "ab8d3e0e-45b6-448f-af0d-863e9e3b06a4";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- Missed Appointment Tracing";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		
		Date nowDate = new Date();
		Parameter endDate = new Parameter("endDate", "Reporting Date", Date.class);
		endDate.setDefaultValue(nowDate);
		endDate.setRequired(false);
		Properties properties = new Properties();
		
		endDate.setWidgetConfiguration(properties);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		endDate.setDefaultValue(EthiOhriUtil.getEthiopianDate(nowDate));
		
		return Arrays.asList(endDate, endDateGC);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		MissedAppointmentDatasetDefinition datasetDefinition = new MissedAppointmentDatasetDefinition();
		datasetDefinition.addParameters(getParameters());
		
		reportDefinition.addDataSetDefinition("Missed Appointment Tracing ", EthiOhriUtil.mapEndDate(datasetDefinition));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("c5d86c28-da68-42fa-9801-f75d612f6b8f", reportDefinition);
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
