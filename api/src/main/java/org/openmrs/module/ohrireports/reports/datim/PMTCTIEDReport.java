package org.openmrs.module.ohrireports.reports.datim;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.EidAgeAndTestDisAggregationDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.EidNumeratorDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PMTCTIEDReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "ccca77d6-521c-43f6-811d-3d268877fe70";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_TESTING + "-PMTCT_EID";
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
		
		EidNumeratorDatasetDefinition headerDefinition = new EidNumeratorDatasetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: PMTCT_EID");
		reportDefinition.addDataSetDefinition("DSD: PMTCT_EID", EthiOhriUtil.map(headerDefinition));
		
		EidNumeratorDatasetDefinition numeratorDatasetDefinition = new EidNumeratorDatasetDefinition();
		numeratorDatasetDefinition
		        .setDescription("Number of HIV-exposed infants who had a virologic HIV test (sample collected) by 12 months of birth during the reporting period. Numerator will auto-calculate from infant age at first test.");
		numeratorDatasetDefinition.setParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "Number of HIV-exposed infants who had a virologic HIV test (sample collected) by 12 months of birth during the reporting period. Numerator will auto-calculate from infant age at first test.",
		            EthiOhriUtil.map(numeratorDatasetDefinition));
		
		EidAgeAndTestDisAggregationDatasetDefinition ageAndTestDisAggregationDatasetDefinition = new EidAgeAndTestDisAggregationDatasetDefinition();
		ageAndTestDisAggregationDatasetDefinition.setDescription("Disaggregated by infant age at sample collection.");
		ageAndTestDisAggregationDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("Disaggregated by infant age at sample collection.",
		    EthiOhriUtil.map(ageAndTestDisAggregationDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("9d4dabdb-f300-4b34-a126-aa0e77baabd5", reportDefinition);
		
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
