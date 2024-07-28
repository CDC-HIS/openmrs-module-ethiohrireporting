package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev.TbPrevDominatorDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

@Component
public class DatimTbPrevDenominatorReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "ee4aa4e7-3cbe-4f5e-881e-ec7699461f88";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_PREVENTION + "-TB_PREV (Denominator)";
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
		
		TbPrevDominatorDatasetDefinition headerDefinition = new TbPrevDominatorDatasetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: TB_PREV");
		reportDefinition.addDataSetDefinition("DSD: TB_PREV (Denominator)", EthiOhriUtil.map(headerDefinition));
		
		TbPrevDominatorDatasetDefinition aDefinition = new TbPrevDominatorDatasetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setAggregateType(false);
		aDefinition.setDescription("Auto-Calculate");
		reportDefinition
		        .addDataSetDefinition(
		            "Auto-Calculate: Number of ART patients who were initiated on any course of TPT during the previous reporting period",
		            EthiOhriUtil.map(aDefinition));
		
		TbPrevDominatorDatasetDefinition aggDatasetDefinition = new TbPrevDominatorDatasetDefinition();
		aggDatasetDefinition.setAggregateType(true);
		aggDatasetDefinition.addParameters(getParameters());
		aggDatasetDefinition.setDescription("DSD: TB_PREV Aggregation by age and sex");
		reportDefinition.addDataSetDefinition("Required: Disaggregated By ART Start by Age/Sex",
		    EthiOhriUtil.map(aggDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("50e85ddc-018b-4ea0-9412-248d566b587f", reportDefinition);
		
		return Arrays.asList(design);
		
	}
	
	@Override
	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
		return null;
	}
	
	@Override
	public String getVersion() {
		return "1.0.0-SNAPSHOT";
		
	}
	
}
