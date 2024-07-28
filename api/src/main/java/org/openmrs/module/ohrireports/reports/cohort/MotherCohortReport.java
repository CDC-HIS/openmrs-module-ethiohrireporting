package org.openmrs.module.ohrireports.reports.cohort;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.pmtct_cohort.MotherDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;

@Component
public class MotherCohortReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "5cea5cd3-aa72-43f7-a2a9-54e94ebcc069";
	}
	
	@Override
	public String getName() {
		return ReportType.COHORT + "- Mother Cohort Report ";
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
		
		MotherDatasetDefinition motherDatasetDefinition = new MotherDatasetDefinition();
		motherDatasetDefinition.addParameters(getParameters());
		
		reportDefinition.addDataSetDefinition("MotherCohort ", EthiOhriUtil.map(motherDatasetDefinition));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("3c037ae7-b959-46d0-91c8-4d702388b6e1", reportDefinition);
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
