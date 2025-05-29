package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.*;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;

//@Component
public class PrEPNewReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "24fbeafe-f8c7-4f28-a3bd-d31ed89d21b7";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_PREVENTION + "-PrEP_NEW";
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
		reportDefinition.addParameters(getParameters());
		
		EncounterType followUpEncounter = Context.getEncounterService().getEncounterTypeByUuid(
		    org.openmrs.module.ohrireports.constants.EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		
		AutoCalculatePrepNewDataSetDefinition headerDefinition = new AutoCalculatePrepNewDataSetDefinition();
		headerDefinition.setParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: PrEP_NEW");
		reportDefinition.addDataSetDefinition("DSD: PrEP_NEW", EthiOhriUtil.map(headerDefinition));
		
		AutoCalculatePrepNewDataSetDefinition aDataSetDefinition = new AutoCalculatePrepNewDataSetDefinition();
		aDataSetDefinition.setParameters(getParameters());
		aDataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition(
		    "Auto-Calculate: Number of individuals who have been newly enrolled on oral antiretroviral pre-exposure prophylaxis "
		            + "(PrEP) in the reporting period to prevent HIV infection.", EthiOhriUtil.map(aDataSetDefinition));
		
		PrEPNewDatasetDefinition dataSetDefinition = new PrEPNewDatasetDefinition();
		dataSetDefinition.setParameters(getParameters());
		dataSetDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    org.openmrs.module.ohrireports.constants.EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Disaggregated by Age / Sex", EthiOhriUtil.map(dataSetDefinition));
		
		DisaggregatedByPopulationTypDatasetDefinition dDataSetDefinition = new DisaggregatedByPopulationTypDatasetDefinition();
		dDataSetDefinition.setParameters(getParameters());
		dDataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by key population type", EthiOhriUtil.map(dDataSetDefinition));
		
		DisaggregatedByPregnantDatasetDefinition pregDatasetDefinition = new DisaggregatedByPregnantDatasetDefinition();
		pregDatasetDefinition.setParameters(getParameters());
		pregDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by Pregnant/Breastfeeding.",
		    EthiOhriUtil.map(pregDatasetDefinition));
		
		DisaggregatedByPrepDistributionDatasetDefinition distributionDatasetDefinition = new DisaggregatedByPrepDistributionDatasetDefinition();
		distributionDatasetDefinition.setParameters(getParameters());
		distributionDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by PrEP Distribution.",
		    EthiOhriUtil.map(distributionDatasetDefinition));
		
		DisaggregatedByPrepTypeDatasetDefinition prepTypeDatasetDefinition = new DisaggregatedByPrepTypeDatasetDefinition();
		prepTypeDatasetDefinition.setParameters(getParameters());
		prepTypeDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by PrEP Type.", EthiOhriUtil.map(prepTypeDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("ba0df56a-2902-4c7f-a32f-e7f552431105", reportDefinition);
		
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
