package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev_denominator.TbPrevDominatorDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev_numerator.TbPrevNumeratorARTByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev_numerator.TbPrevNumeratorAutoCalculateDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
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
		return DATIM_REPORT + "-TB_PREV (Denominator)";
	}
	
	@Override
	public String getDescription() {
		return "DSD: TB_PREV (Denominator)";
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
		
		TbPrevDominatorDatasetDefinition aDefinition = new TbPrevDominatorDatasetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setAggregateType(false);
		aDefinition.setDescription("DSD: TB_PREV (Denominator)");
		reportDefinition.addDataSetDefinition("DSD: TB_PREV (Denominator)",
		    map(aDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TbPrevDominatorDatasetDefinition aggDatasetDefinition = new TbPrevDominatorDatasetDefinition();
		aggDatasetDefinition.setAggregateType(true);
		aggDatasetDefinition.addParameters(getParameters());
		aggDatasetDefinition.setDescription("DSD: TB_PREV Aggregation by age and sex");
		reportDefinition.addDataSetDefinition("Required:Disaggregated by age and sex",
		    map(aggDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		return reportDefinition;
	}
	
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		if (mappings == null) {
			mappings = ""; // probably not necessary, just to be safe
		}
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
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
