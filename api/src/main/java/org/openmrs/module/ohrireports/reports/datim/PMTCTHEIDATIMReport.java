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

@Component
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
		headerDefinition.setDescription("DSD: HEI_TOTAL");
		reportDefinition.addDataSetDefinition("DSD: HEI_TOTAL", EthiOhriUtil.map(headerDefinition));
		
		HeiTotalDatasetDefinition heiTotalDatasetDefinition = new HeiTotalDatasetDefinition();
		heiTotalDatasetDefinition.setDescription("Sum Result");
		heiTotalDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("Sum results", EthiOhriUtil.map(heiTotalDatasetDefinition));
		
		HeiAgeAndResultDatasetDefinition ageAndTestDisAggregationDatasetDefinition = new HeiAgeAndResultDatasetDefinition();
		ageAndTestDisAggregationDatasetDefinition.setDescription("-");
		ageAndTestDisAggregationDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("PMTCT ", EthiOhriUtil.map(ageAndTestDisAggregationDatasetDefinition));
		
		HeiPositiveLinkedDatasetDefinition positiveLinkedDatasetDefinition = new HeiPositiveLinkedDatasetDefinition();
		positiveLinkedDatasetDefinition.setDescription("-");
		positiveLinkedDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("PMTCT- Positive,confirmed on ARt ",
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
