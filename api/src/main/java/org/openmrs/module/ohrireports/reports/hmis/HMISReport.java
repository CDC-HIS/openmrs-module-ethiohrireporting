package org.openmrs.module.ohrireports.reports.hmis;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HMIS_REPORT;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_ret.HIVARTRETDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.tx_new.HIVTXNewDatasetDefinition;
import org.openmrs.api.context.Context;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.HmisTXCurrDataSetDefinition;
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
public class HMISReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "4bf142d2-f92c-4a9e-9368-55801e9c025d";
	}
	
	@Override
	public String getName() {
		return HMIS_REPORT + "-HMIS/DHIS";
	}
	
	@Override
	public String getDescription() {
		return "06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry Of Health)";
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter startDate = new Parameter("startDate", "Start Date", Date.class);
		startDate.setRequired(true);
		Parameter startDateGC = new Parameter("startDateGC", " ", Date.class);
		startDateGC.setRequired(false);
		Parameter endDate = new Parameter("endDate", "End Date", Date.class);
		endDate.setRequired(true);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(startDate, startDateGC, endDate, endDateGC);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		ReportDefinition reportDefinition = new ReportDefinition();
		reportDefinition.setUuid(getUuid());
		reportDefinition.setName(getName());
		reportDefinition.setDescription(getDescription());
		reportDefinition.setParameters(getParameters());
		
		HIVTXNewDatasetDefinition txNewDataset = new HIVTXNewDatasetDefinition();
		txNewDataset.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("HMIS:Number of adults and children with HIV infection newly started on ART",
		    map(txNewDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
<<<<<<< HEAD
		HmisTXCurrDataSetDefinition aDefinition = new HmisTXCurrDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition.setDescription("06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry Of Health)");
		reportDefinition.addDataSetDefinition(
		    "06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry Of Health)",
		    map(aDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
=======
		HIVARTRETDatasetDefinition hivArtRetDatasetDefinition = new HIVARTRETDatasetDefinition();
		hivArtRetDatasetDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition(
		    "HMIS:Number of adults and children who are still on treatment at 12 months after\n" + //
		            "initiating ART", map(hivArtRetDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
>>>>>>> hmis_hiv_art
		
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
		ReportDesign design = ReportManagerUtil.createExcelDesign("d15829f9-ad58-4421-8d82-11dd80ffaeb2", reportDefinition);
		
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
