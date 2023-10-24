package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_art.PMTCTARTAutoCalculateDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_art.PMTCTARTDataSetDefinition;
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
public class PMTCTARTReport implements ReportManager {
	
	private EncounterType followUpEncounter;
	
	@Override
	public String getUuid() {
		return "59182764-c3a4-41bf-84ad-855d0804f89b";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-PMTCT_ART";
	}
	
	@Override
	public String getDescription() {
		return "Number of HIV-positive pregnant women who received ART to reduce the risk of mother-to-child-transmission";
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
		followUpEncounter = Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE);
		
		PMTCTARTAutoCalculateDataSetDefinition tbADataSet = new PMTCTARTAutoCalculateDataSetDefinition();
		tbADataSet.addParameters(getParameters());
		tbADataSet.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition(
		    "During pregnancy.Numerator will auto-calculate from the Maternal Regimen Type Desegregates",
		    EthiOhriUtil.map(tbADataSet));
		
		PMTCTARTDataSetDefinition alreadyOnARTSetDefinition = new PMTCTARTDataSetDefinition();
		alreadyOnARTSetDefinition.addParameters(getParameters());
		alreadyOnARTSetDefinition.setEncounterType(followUpEncounter);
		
		reportDefinition
		        .addDataSetDefinition("Disaggregated by Regiment Type:", EthiOhriUtil.map(alreadyOnARTSetDefinition));
		
		PMTCTARTDataSetDefinition newlyEnrolledSetDefinition = new PMTCTARTDataSetDefinition();
		newlyEnrolledSetDefinition.addParameters(getParameters());
		newlyEnrolledSetDefinition.setEncounterType(followUpEncounter);
		
		reportDefinition.addDataSetDefinition("Disaggregated by Regiment Type:",
		    EthiOhriUtil.map(newlyEnrolledSetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("39f4d2f0-f0c5-4090-82ce-1b627fc936fa", reportDefinition);
		
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
