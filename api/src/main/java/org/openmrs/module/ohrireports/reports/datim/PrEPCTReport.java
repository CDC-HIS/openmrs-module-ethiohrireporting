package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_ct.*;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;

@Component
public class PrEPCTReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "17ec12f4-7475-4deb-9b4a-ee8df5c667a3";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_PREVENTION + "-PrEP_CT";
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
		
		AutoCalculatePrEPCTDatasetDefinition headerDefinition = new AutoCalculatePrEPCTDatasetDefinition();
		headerDefinition.setParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: PrEP_CT");
		reportDefinition.addDataSetDefinition("DSD: PrEP_CT", EthiOhriUtil.map(headerDefinition));
		
		AutoCalculatePrEPCTDatasetDefinition aDataSetDefinition = new AutoCalculatePrEPCTDatasetDefinition();
		aDataSetDefinition.setParameters(getParameters());
		aDataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition(
		    "Auto-Calculate: Number of individuals, excluding those newly enrolled, that return for a follow-up visit or\n"
		            + "reinitiation visit to receive pre-exposure prophylaxis (PrEP) to prevent HIV during the reporting\n"
		            + "period. Numerator will autocalculate from sum of Age/Sex Disaggreg",
		    EthiOhriUtil.map(aDataSetDefinition));
		
		PrEPCTDatasetDefinition dataSetDefinition = new PrEPCTDatasetDefinition();
		dataSetDefinition.setParameters(getParameters());
		dataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by Age / Sex", EthiOhriUtil.map(dataSetDefinition));
		
		PrEPCTTestResultDatasetDefinition prEPCTTestDataset = new PrEPCTTestResultDatasetDefinition();
		prEPCTTestDataset.addParameters(getParameters());
		prEPCTTestDataset.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by test result", EthiOhriUtil.map(prEPCTTestDataset));
		
		PrEPCTByPopulationTypeDatasetDefinition prEPCTDataset = new PrEPCTByPopulationTypeDatasetDefinition();
		prEPCTDataset.addParameters(getParameters());
		prEPCTDataset.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by key population type", EthiOhriUtil.map(prEPCTDataset));
		
		PrEPCTPregnantBreastfeedingDatasetDefinition prEPCPFDataset = new PrEPCTPregnantBreastfeedingDatasetDefinition();
		prEPCPFDataset.addParameters(getParameters());
		prEPCPFDataset.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by Pregnant/Breastfeeding", EthiOhriUtil.map(prEPCPFDataset));
		
		PrepCTDisaggregatedByPrepDistributionDatasetDefinition prepDistributionDatasetDefinition = new PrepCTDisaggregatedByPrepDistributionDatasetDefinition();
		prepDistributionDatasetDefinition.addParameters(getParameters());
		prepDistributionDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by PrEP Distribution",
		    EthiOhriUtil.map(prepDistributionDatasetDefinition));
		
		PrepCTDisaggregatedByPrepTypeDatasetDefinition prepTypeDatasetDefinition = new PrepCTDisaggregatedByPrepTypeDatasetDefinition();
		prepTypeDatasetDefinition.addParameters(getParameters());
		prepTypeDatasetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Disaggregated by PrEP Type", EthiOhriUtil.map(prepTypeDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("fa0b3ea1-cf63-4dd0-9a69-d9d9af804590", reportDefinition);
		
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
