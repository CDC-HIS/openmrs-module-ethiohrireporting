package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_numerator.TxTbNumeratorARTByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_numerator.TxTbNumeratorAutoCalculateDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

//@Component
public class DatimTxTbNumeratorReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "88ace55a-db97-4c3f-960d-83685fa070d3";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_TREATMENT + "-TX_TB(Numerator)";
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
		
		TxTbNumeratorAutoCalculateDataSetDefinition headerDefinition = new TxTbNumeratorAutoCalculateDataSetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: TX_TB (Numerator)");
		reportDefinition.addDataSetDefinition("DSD: TX_TB (Numerator)", EthiOhriUtil.map(headerDefinition));
		
		TxTbNumeratorAutoCalculateDataSetDefinition aDefinition = new TxTbNumeratorAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition
		        .setDescription("Number of adults and children currently enrolling ART and has documented Active on TB treatment");
		reportDefinition.addDataSetDefinition(
		    "Auto-Calculate : Number of ART patients who were started on TB treatment during the reporting period. Numerator"
		            + " will auto-calculate from Already/New ART by Age/Sex", EthiOhriUtil.map(aDefinition));
		
		TxTbNumeratorARTByAgeAndSexDataSetDefinition cDefinition = new TxTbNumeratorARTByAgeAndSexDataSetDefinition();
		cDefinition.addParameters(getParameters());
		cDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		cDefinition.setDescription("Disaggregated by Current/New on ART by Age/Sex");
		reportDefinition.addDataSetDefinition("Required : Disaggregated by Current/New on ART by Age/Sex",
		    EthiOhriUtil.map(cDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("996a9b84-7d4a-4d13-9f7d-edeee916835c", reportDefinition);
		
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
