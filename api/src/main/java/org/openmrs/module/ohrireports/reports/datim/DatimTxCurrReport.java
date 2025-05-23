package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrARVDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrAutoCalculateDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrCoarseByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrFineByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrKeyPopulationTypeDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

//@Component
public class DatimTxCurrReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "d94af9d0-d35b-446a-9e62-4c8b31b9c0fe";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_TREATMENT + "-TX_CURR";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter endDate = new Parameter("endDate", "Report Date", Date.class);
		endDate.setRequired(true);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(endDate, endDateGC);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		TxCurrAutoCalculateDataSetDefinition headerDefinition = new TxCurrAutoCalculateDataSetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: TX_CURR");
		reportDefinition.addDataSetDefinition("DSD: TX_CURR", EthiOhriUtil.mapEndDate(headerDefinition));
		
		TxCurrAutoCalculateDataSetDefinition aDefinition = new TxCurrAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition.setDescription("Number of adults and children currently enrolling on antiretroviral therapy (ART)");
		reportDefinition.addDataSetDefinition(
		    "Number of adults and children currently receiving antiretroviral therapy (ART). "
		            + "Numerator will auto-calculate from Age/Sex Disaggregates", EthiOhriUtil.mapEndDate(aDefinition));
		
		TxCurrFineByAgeAndSexDataSetDefinition fDefinition = new TxCurrFineByAgeAndSexDataSetDefinition();
		fDefinition.addParameters(getParameters());
		fDefinition.setDescription("Disaggregated by Age/Sex (Fine disaggregate)");
		fDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Required Disaggregated by Age/Sex (Fine disaggregate)",
		    EthiOhriUtil.mapEndDate(fDefinition));
		
		TxCurrCoarseByAgeAndSexDataSetDefinition cDefinition = new TxCurrCoarseByAgeAndSexDataSetDefinition();
		cDefinition.addParameters(getParameters());
		cDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		cDefinition.setDescription("Disaggregated by Age/Sex (Coarse disaggregated)");
		reportDefinition.addDataSetDefinition("Conditional Disaggregated by Age/Sex (Coarse disaggregated)",
		    EthiOhriUtil.mapEndDate(cDefinition));
		
		TxCurrARVDataSetDefinition arvDefinition = new TxCurrARVDataSetDefinition();
		arvDefinition.addParameters(getParameters());
		arvDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		arvDefinition.setDescription("Disaggregated by ARV Dispensing Quantity by Coarse Age/Sex ");
		
		reportDefinition.addDataSetDefinition("Required Disaggregated by ARV Dispensing Quantity by Coarse Age/Sex",
		    EthiOhriUtil.mapEndDate(arvDefinition));
		
		TxCurrKeyPopulationTypeDataSetDefinition keyPopulationTypeDefinition = new TxCurrKeyPopulationTypeDataSetDefinition();
		keyPopulationTypeDefinition.addParameters(getParameters());
		keyPopulationTypeDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		keyPopulationTypeDefinition.setDescription("Disaggregated by key population type");
		
		reportDefinition.addDataSetDefinition("Required Disaggregated by key population type",
		    EthiOhriUtil.mapEndDate(keyPopulationTypeDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("2283c1d0-c04a-4159-b19e-ded411b0d749", reportDefinition);
		
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
