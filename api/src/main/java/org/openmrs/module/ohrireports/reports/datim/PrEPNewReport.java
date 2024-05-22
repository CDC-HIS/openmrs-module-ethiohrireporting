package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.AutoCalculatePrepNewDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.DisaggregatedByPopulationTypDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_new.PrEPNewDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class PrEPNewReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "24fbeafe-f8c7-4f28-a3bd-d31ed89d21b7";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT_PREVENTION + "-PrEP_NEW";
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
		
		EncounterType followUpEncounter = Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		
		AutoCalculatePrepNewDataSetDefinition headerDefinition = new AutoCalculatePrepNewDataSetDefinition();
		headerDefinition.setParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: PrEP_NEW");
		reportDefinition.addDataSetDefinition("DSD: PrEP_NEW", EthiOhriUtil.map(headerDefinition));
		
		AutoCalculatePrepNewDataSetDefinition aDataSetDefinition = new AutoCalculatePrepNewDataSetDefinition();
		aDataSetDefinition.setParameters(getParameters());
		aDataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Auto-Calculate", EthiOhriUtil.map(aDataSetDefinition));
		
		PrEPNewDatasetDefinition dataSetDefinition = new PrEPNewDatasetDefinition();
		dataSetDefinition.setParameters(getParameters());
		dataSetDefinition.setEncounterType(Context.getEncounterService()
		        .getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Disaggregated by Age / Sex", EthiOhriUtil.map(dataSetDefinition));
		
		DisaggregatedByPopulationTypDatasetDefinition dDataSetDefinition = new DisaggregatedByPopulationTypDatasetDefinition();
		dDataSetDefinition.setParameters(getParameters());
		dDataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by key population type", EthiOhriUtil.map(dDataSetDefinition));
		
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
