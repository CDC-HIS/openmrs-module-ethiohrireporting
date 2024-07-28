package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
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
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;

@Component
public class TX_PVLSReport implements ReportManager {
	
	@Override
	public String getUuid() {
		
		return "ae16163a-51c8-4941-839e-58027874e4d8";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_VIRAL_SUPPRESSION.concat("-TX_PVLS(Numerator)");
	}
	
	@Override
	public String getDescription() {
		return "";
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
		
		EncounterType followUpEncounter = Context.getEncounterService().getEncounterTypeByUuid(
		    org.openmrs.module.ohrireports.constants.EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		
		TX_PVLSAutoCalcDatasetDefinition headerDefinition = new TX_PVLSAutoCalcDatasetDefinition();
		headerDefinition.setParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("TX_PVLS");
		reportDefinition.addDataSetDefinition("DSD: TX_PVLS (Numerator)", EthiOhriUtil.mapEndDate(headerDefinition));
		
		TX_PVLSAutoCalcDatasetDefinition autoCalDataSetDefinition = new TX_PVLSAutoCalcDatasetDefinition();
		autoCalDataSetDefinition.setParameters(getParameters());
		autoCalDataSetDefinition.setIncludeUnSuppressed(false);
		autoCalDataSetDefinition.setEncounterType(followUpEncounter);
		reportDefinition
		        .addDataSetDefinition(
		            "Auto-Calculate Adult and Pediatric on ART with suppressed viral load"
		                    + " results (<1,000 copies/ml) documented in the medical records and/or supporting laboratory results within the "
		                    + "past 12 months. Numerator will auto-calculate from the sum of the Age/Sex disaggregates",
		            map(autoCalDataSetDefinition, "endDate=${endDateGC}"));
		
		TX_PVLSDatasetDefinition DataSetDefinition = new TX_PVLSDatasetDefinition();
		DataSetDefinition.setParameters(getParameters());
		DataSetDefinition.setIncludeUnSuppressed(false);
		DataSetDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    org.openmrs.module.ohrireports.constants.EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition(
		    "Required: Disaggregated by Age / Sex / (Fine Disaggregated). Must complete finer"
		            + " disaggregates unless permitted by program.", map(DataSetDefinition, "endDate=${endDateGC}"));
		
		TX_PVLSPregnantBreastfeedingDatasetDefinition pregnantAndBFDataSetDefinition = new TX_PVLSPregnantBreastfeedingDatasetDefinition();
		pregnantAndBFDataSetDefinition.setParameters(getParameters());
		pregnantAndBFDataSetDefinition.setIncludeUnSuppressed(false);
		pregnantAndBFDataSetDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    org.openmrs.module.ohrireports.constants.EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Required: Disaggregated by Pregnant/Breastfeeding.",
		    map(pregnantAndBFDataSetDefinition, "endDate=${endDateGC}"));
		
		TX_PVLSDisaggregationByPopulationDatasetDefinition disaggregationByPopDataSetDefinition = new TX_PVLSDisaggregationByPopulationDatasetDefinition();
		disaggregationByPopDataSetDefinition.setParameters(getParameters());
		disaggregationByPopDataSetDefinition.setIncludeUnSuppressed(false);
		disaggregationByPopDataSetDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(
		    org.openmrs.module.ohrireports.constants.EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition("Disaggregated by key population type",
		    map(disaggregationByPopDataSetDefinition, "endDate=${endDateGC}"));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("e7b8081e-5806-4a81-80f0-95650366e2b3", reportDefinition);
		
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
