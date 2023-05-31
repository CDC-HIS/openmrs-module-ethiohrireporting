package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATIM_REPORT;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_ml.*;
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
public class DatimTxMlReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "d94atxml-d35b-446a-9e62-4c8b31b9c0fe";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-Tx_Ml";
	}
	
	@Override
	public String getDescription() {
		return "Aggregate report of DATIM TX_ML";
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
		
		TxMlAutoCalculateDataSetDefinition aDefinition = new TxMlAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition
		        .setDescription("Number of ART patients (who were on ART at the beginning of the quarterly reporting period or initiated treatment during the reporting period) and then had no clinical contact since their last expected contact");
		reportDefinition
		        .addDataSetDefinition(
		            "Auto-Calculate - Number of ART patients (who were on ART at the beginning of the quarterly reporting period or initiated treatment during the reporting period) and then had no clinical contact since their last expected contact",
		            map(aDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TxMlDiedByAgeAndSexDataSetDefinition dDefinition = new TxMlDiedByAgeAndSexDataSetDefinition();
		dDefinition.addParameters(getParameters());
		dDefinition.setDescription("Disaggregated Outcome by Age/Sex");
		dDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Required - Disaggregated Outcome by Age/Sex",
		    map(dDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TxMlInterruptionlessthan3MonthsByAgeAndSexDataSetDefinition cDefinition = new TxMlInterruptionlessthan3MonthsByAgeAndSexDataSetDefinition();
		cDefinition.addParameters(getParameters());
		cDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		cDefinition.setDescription("Interruption in Treatment After being on Treatment for < 3 months");
		reportDefinition.addDataSetDefinition(
		    "Conditional - Interruption in Treatment After being on Treatment for < 3 months",
		    map(cDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition tDefinition = new TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition();
		tDefinition.addParameters(getParameters());
		tDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		tDefinition.setDescription("Interruption in Treatment After being on Treatment for 3-5 months");
		reportDefinition.addDataSetDefinition(
		    "Conditional - Interruption in Treatment After being on Treatment for 3-5 months",
		    map(tDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition sDefinition = new TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition();
		sDefinition.addParameters(getParameters());
		sDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		sDefinition.setDescription("Interruption in Treatment After being on Treatment for 6+ months");
		reportDefinition.addDataSetDefinition(
		    "Conditional - Interruption in Treatment After being on Treatment for 6+ months",
		    map(sDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TxMlTransferOutByAgeAndSexDataSetDefinition oDefinition = new TxMlTransferOutByAgeAndSexDataSetDefinition();
		oDefinition.addParameters(getParameters());
		oDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		oDefinition.setDescription("Transferred out");
		reportDefinition.addDataSetDefinition("Conditional - Transferred out",
		    map(oDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		TxMlRefusedByAgeAndSexDataSetDefinition rDefinition = new TxMlRefusedByAgeAndSexDataSetDefinition();
		rDefinition.addParameters(getParameters());
		rDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		rDefinition.setDescription("Refused(Stopped) Treatment");
		reportDefinition.addDataSetDefinition("Conditional - Refused(Stopped) Treatment",
		    map(rDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
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
		ReportDesign design = ReportManagerUtil.createExcelDesign("2283ctxml-c04a-4159-b19e-ded411b0d749", reportDefinition);
		
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
