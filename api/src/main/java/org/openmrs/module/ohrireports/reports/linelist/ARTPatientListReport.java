package org.openmrs.module.ohrireports.reports.linelist;

import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.ARTPatientListDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.CervicalCancerDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINE_LIST_REPORT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;

@Component
public class ARTPatientListReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "4710fb50-1fcb-4de2-95ba-bf0ce492a4e8";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "- ART Patient List";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		Calendar calendar = Calendar.getInstance();
		Date nowDate = calendar.getTime();
		Parameter endDate = new Parameter("endDate", "Enrolled Before:", Date.class);
		endDate.setRequired(false);
		endDate.setDefaultValue(nowDate);
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
		
		ARTPatientListDatasetDefinition artPatientListDatasetDefinition = new ARTPatientListDatasetDefinition();
		artPatientListDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("ART Patient List",
		    EthiOhriUtil.map(artPatientListDatasetDefinition, "endDate=${endDateGC}"));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("2cf7c5f2-1e96-4149-983c-eec47f01bd71", reportDefinition);
		
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
