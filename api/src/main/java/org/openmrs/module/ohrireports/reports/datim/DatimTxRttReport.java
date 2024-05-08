package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATIM_REPORT;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.FineByAgeAndSexAndCD4DataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_rtt.*;
import org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new.CD4Status;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

@Component
public class DatimTxRttReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "d94txrtt-d35b-446a-9e62-4c8b31b9c0fe";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-TX_RTT";
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
		
		TxRttAutoCalculateDataSetDefinition headerDefinition = new TxRttAutoCalculateDataSetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: TX_RTT");
		reportDefinition.addDataSetDefinition("DSD: TX_RTT", EthiOhriUtil.map(headerDefinition));
		
		TxRttAutoCalculateDataSetDefinition aDefinition = new TxRttAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition
		        .setDescription("Number of ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period.");
		reportDefinition
		        .addDataSetDefinition(
		            "Number of ART patients who experienced IIT during any previous reporting period, who successfully restarted ARVs within the reporting period and remained on treatment until the end of the reporting period.",
		            EthiOhriUtil.map(aDefinition));
		
		TxRttByAgeAndSexDataSetDefinition txRTTCD4HeaderDefinition = new TxRttByAgeAndSexDataSetDefinition();
		txRTTCD4HeaderDefinition.addParameters(getParameters());
		txRTTCD4HeaderDefinition.setHeader(true);
		txRTTCD4HeaderDefinition.setDescription("Disaggregated by Age/Sex And CD4");
		reportDefinition.addDataSetDefinition("Required Disaggregated by Age/Sex And CD4",
		    EthiOhriUtil.map(txRTTCD4HeaderDefinition));
		
		TxRttByAgeAndSexDataSetDefinition txRTTCD4L_200Definition = new TxRttByAgeAndSexDataSetDefinition();
		txRTTCD4L_200Definition.addParameters(getParameters());
		txRTTCD4L_200Definition.setCountCD4GreaterThan200(CD4Status.CD4LessThan200);
		txRTTCD4L_200Definition.setDescription("< 200 CD4");
		txRTTCD4L_200Definition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("< 200 CD4", EthiOhriUtil.map(txRTTCD4L_200Definition));
		
		TxRttByAgeAndSexDataSetDefinition txRTTCD4G_200Definition = new TxRttByAgeAndSexDataSetDefinition();
		txRTTCD4G_200Definition.addParameters(getParameters());
		txRTTCD4G_200Definition.setCountCD4GreaterThan200(CD4Status.CD4GreaterThan200);
		txRTTCD4G_200Definition.setDescription(">= 200 CD4");
		txRTTCD4G_200Definition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition(">= 200 CD4", EthiOhriUtil.map(txRTTCD4G_200Definition));
		
		TxRttByAgeAndSexDataSetDefinition txRTTCDUnknownDefinition = new TxRttByAgeAndSexDataSetDefinition();
		txRTTCDUnknownDefinition.addParameters(getParameters());
		txRTTCDUnknownDefinition.setDescription("Unknown CD4");
		txRTTCDUnknownDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Unknown CD4", EthiOhriUtil.map(txRTTCDUnknownDefinition));
		
		TxRttIITDataSetDefinition tDefinition = new TxRttIITDataSetDefinition();
		tDefinition.addParameters(getParameters());
		tDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		tDefinition.setDescription("Disaggregated by IIT");
		reportDefinition.addDataSetDefinition("Conditional Required - Disaggregated by IIT", EthiOhriUtil.map(tDefinition));
		
		TxRttKeyPopulationTypeDataSetDefinition oDefinition = new TxRttKeyPopulationTypeDataSetDefinition();
		oDefinition.addParameters(getParameters());
		oDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		oDefinition.setDescription("Disaggregated by key population type");
		reportDefinition
		        .addDataSetDefinition("Required Disaggregated by key population type", EthiOhriUtil.map(oDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("2283txrtt-c04a-4159-b19e-ded411b0d749", reportDefinition);
		
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
