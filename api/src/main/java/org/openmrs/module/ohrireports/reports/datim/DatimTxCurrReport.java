package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATIM_REPORT;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrARVDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrAutoCalculateDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrCoarseByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrFineByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrKeyPopulationTypeDataSetDefinition;
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
public class DatimTxCurrReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "d94af9d0-d35b-446a-9e62-4c8b31b9c0fe";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-TX_CURR";
	}
	
	@Override
	public String getDescription() {
		return "Aggregate report of DATIM TX_CURR enrolling  patients";
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
		
		TxCurrAutoCalculateDataSetDefinition aDefinition = new TxCurrAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition.setDescription("Number of adults and children currently enrolling on antiretroviral therapy (ART)");
		reportDefinition.addDataSetDefinition("Auto-Calculate",
		    EthiOhriUtil.map(aDefinition));
		
		TxCurrFineByAgeAndSexDataSetDefinition fDefinition = new TxCurrFineByAgeAndSexDataSetDefinition();
		fDefinition.addParameters(getParameters());
		fDefinition.setDescription("Disaggregated by Age/Sex (Fine disaggregate)");
		fDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Required Disaggregated by Age/Sex (Fine disaggregate)",
		    EthiOhriUtil.map(fDefinition));
		
		TxCurrCoarseByAgeAndSexDataSetDefinition cDefinition = new TxCurrCoarseByAgeAndSexDataSetDefinition();
		cDefinition.addParameters(getParameters());
		cDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		cDefinition.setDescription("Disaggregated by Age/Sex (Coarse disaggregated)");
		reportDefinition.addDataSetDefinition("Conditional Disaggregated by Age/Sex (Coarse disaggregated)",
		    EthiOhriUtil.map(cDefinition));
		
		TxCurrARVDataSetDefinition arvDefinition = new TxCurrARVDataSetDefinition();
		arvDefinition.addParameters(getParameters());
		arvDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		arvDefinition.setDescription("Disaggregated by ARV Dispensing Quantity by Coarse Age/Sex ");
		
		reportDefinition.addDataSetDefinition("Required Disaggregated by ARV Dispensing Quantity by Coarse Age/Sex",
		    EthiOhriUtil.map(arvDefinition));
		
		TxCurrKeyPopulationTypeDataSetDefinition keyPopulationTypeDefinition = new TxCurrKeyPopulationTypeDataSetDefinition();
		keyPopulationTypeDefinition.addParameters(getParameters());
		keyPopulationTypeDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		keyPopulationTypeDefinition.setDescription("Disaggregated by key population type");
		
		reportDefinition.addDataSetDefinition("Required Disaggregated by key population type",
		    EthiOhriUtil.map(keyPopulationTypeDefinition));
		
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
