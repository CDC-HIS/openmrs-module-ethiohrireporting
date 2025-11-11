package org.openmrs.module.ohrireports.reports.datim;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.*;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ReportType.DATIM_REPORT_TESTING;

//@Component
public class PMTCTHEIDATIMReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "52e58724-a4ed-4b0d-ad29-e03297ae242b";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT_TESTING + "-PMTCT_HEI";
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
		
		HeiTotalDatasetDefinition headerDefinition = new HeiTotalDatasetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: PMTCT_HEI");
		reportDefinition.addDataSetDefinition("DSD: PMTCT_HEI", EthiOhriUtil.map(headerDefinition));
		
		HeiTotalDatasetDefinition heiTotalDatasetDefinition = new HeiTotalDatasetDefinition();
		heiTotalDatasetDefinition
		        .setDescription("Number of HIV-infected infants identified in the reporting period, whose diagnostic sample was collected by 12 months of age. Numerator will auto-calculate from the HIV-infected infant age and result returned disaggregation.");
		heiTotalDatasetDefinition.setParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "Number of HIV-infected infants identified in the reporting period, whose diagnostic sample was collected by 12 months of age. Numerator will auto-calculate from the HIV-infected infant age and result returned disaggregation.",
		            EthiOhriUtil.map(heiTotalDatasetDefinition));
		
		HeiAgeAndResultDatasetDefinition ageAndTestDisAggregationDatasetDefinition = new HeiAgeAndResultDatasetDefinition();
		ageAndTestDisAggregationDatasetDefinition
		        .setDescription("Disaggregated by infant age at virologic sample collection and result returned.");
		ageAndTestDisAggregationDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition(
		    "Disaggregated by infant age at virologic sample collection and result returned.",
		    EthiOhriUtil.map(ageAndTestDisAggregationDatasetDefinition));
		
		HeiPositiveLinkedDatasetDefinition positiveLinkedDatasetDefinition = new HeiPositiveLinkedDatasetDefinition();
		positiveLinkedDatasetDefinition
		        .setDescription("Disaggregated by result returned, Positive, confirmed initiated ART by age at virologic sample collection");
		positiveLinkedDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition(
		    "Disaggregated by result returned, Positive, confirmed initiated ART by age at virologic sample collection ",
		    EthiOhriUtil.map(positiveLinkedDatasetDefinition));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("fe4ca4ed-bacf-4f78-bf15-d56d74ef9585", reportDefinition);
		return Collections.singletonList(design);
		
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
