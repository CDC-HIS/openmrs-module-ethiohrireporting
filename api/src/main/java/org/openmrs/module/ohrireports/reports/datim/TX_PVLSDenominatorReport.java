package org.openmrs.module.ohrireports.reports.datim;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATIM_REPORT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSAutoCalcDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSDisaggregationByPopulationDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSPregnantBreastfeedingDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TX_PVLSDenominatorReport implements ReportManager {
	
	@Autowired
	private ConceptService conceptService;
	
	@Override
	public String getUuid() {
		
		return "fcc0dcdb-31e5-44e4-9be6-4c83a54f7e3c";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT.concat("-TX_PVLS(Denominator)");
	}
	
	@Override
	public String getDescription() {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder
		        .append("Number of adults and pediatric ART patients with a viral load result documented in the medical records ");
		stringBuilder.append("and/or supporting laboratory results within the past 12 months. ");
		stringBuilder.append("Denominator will auto-calculate from the sum of the Age/Sex/Indication disaggregated");
		
		return stringBuilder.toString();
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter endDate = new Parameter("endDate", "Report Date", Date.class);
		endDate.setRequired(false);
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
		reportDefinition.addParameters(getParameters());
		
		EncounterType followUpEncounter = Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		
		TX_PVLSAutoCalcDatasetDefinition autoCalDataSetDefinition = new TX_PVLSAutoCalcDatasetDefinition();
		autoCalDataSetDefinition.setParameters(getParameters());
		autoCalDataSetDefinition.setIncludeUnSuppressed(true);
		autoCalDataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Auto-Calculate", map(autoCalDataSetDefinition, "endDate=${endDateGC}"));
		
		TX_PVLSDatasetDefinition dataSetDefinition = new TX_PVLSDatasetDefinition();
		dataSetDefinition.setParameters(getParameters());
		dataSetDefinition.setIncludeUnSuppressed(true);
		dataSetDefinition.setEncounterType(Context.getEncounterService()
		        .getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition(" Disaggregated by Age / Sex / (Fine Disaggregated).",
		    map(dataSetDefinition, "endDate=${endDateGC}"));
		
		TX_PVLSPregnantBreastfeedingDatasetDefinition pregnantAndBFDataSetDefinition = new TX_PVLSPregnantBreastfeedingDatasetDefinition();
		pregnantAndBFDataSetDefinition.setParameters(getParameters());
		pregnantAndBFDataSetDefinition.setIncludeUnSuppressed(true);
		pregnantAndBFDataSetDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Disaggregated by Preg/BF indication.",
		    map(pregnantAndBFDataSetDefinition, "endDate=${endDateGC}"));
		
		TX_PVLSDisaggregationByPopulationDatasetDefinition disaggregationByPopDataSetDefinition = new TX_PVLSDisaggregationByPopulationDatasetDefinition();
		disaggregationByPopDataSetDefinition.setParameters(getParameters());
		disaggregationByPopDataSetDefinition.setIncludeUnSuppressed(true);
		disaggregationByPopDataSetDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Disaggregated by key population type",
		    map(disaggregationByPopDataSetDefinition, "endDate=${endDateGC}"));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("43f1c1e7-1e0d-480b-a1ae-616805525a58", reportDefinition);
		
		return Arrays.asList(design);
		
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
	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
		return null;
	}
	
	@Override
	public String getVersion() {
		return REPORT_VERSION;
	}
	
}
