package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import org.openmrs.api.context.Context;
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
public class DatimTbPrevNumeratorReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "752tbpren-e57c-47d3-9dc3-57c4ad9e28bf";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-TB_PREV (Numerator)";
	}
	
	@Override
	public String getDescription() {
		return "Aggregate report of  TB_Prev_Numerator patients";
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
		
		TbPrevNumeratorAutoCalculateDataSetDefinition aDefinition = new TbPrevNumeratorAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition
		        .setDescription("Among those who started a course of TPT in the previous reporting period, the number that completed a full course of therapy (for continuous IPT programs, this includes the patients who have completed the first 6 months of isoniazid preventive therapy (IPT), or any other standard course of TPT such as 3 months of weekly isoniazid and rifapentine, or 3-HP)");
		reportDefinition
		        .addDataSetDefinition(
		            "Auto-Calculate : Among those who started a course of TPT in the previous reporting period, the number that completed a full course of therapy",
		            map(aDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TbPrevNumeratorARTByAgeAndSexDataSetDefinition cDefinition = new TbPrevNumeratorARTByAgeAndSexDataSetDefinition();
		cDefinition.addParameters(getParameters());
		cDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		cDefinition.setDescription("Disaggregated by ART by Age/Sex");
		reportDefinition.addDataSetDefinition("Required : Disaggregated by ART by Age/Sex",
		    map(cDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
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
		ReportDesign design = ReportManagerUtil.createExcelDesign("6db2a9c6-9b2d-4684-8ae3-b2b7114331fc", reportDefinition);
		
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
