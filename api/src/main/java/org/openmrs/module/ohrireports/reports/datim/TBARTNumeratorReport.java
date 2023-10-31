package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_art.TBARTAutoCalculateDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_art.TBARTDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

@Component
public class TBARTNumeratorReport implements ReportManager {
	
	private EncounterType followUpEncounter;
	
	@Override
	public String getUuid() {
		return "af7c1fe6-d669-414e-b066-e9733f0de7a8";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-TB_ART (Numerator)";
	}
	
	@Override
	public String getDescription() {
		return "Proportion of HIV-positive new and relapsed TB cases on ART during TB treatment";
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
		
		TBARTAutoCalculateDataSetDefinition tbADataSet = new TBARTAutoCalculateDataSetDefinition();
		tbADataSet.addParameters(getParameters());
		tbADataSet.setEncounterType(followUpEncounter);
		reportDefinition
		        .addDataSetDefinition(
		            "Number of TB cases with documented HIV-positive status who start or continue ART during the reporting period. ",
		            EthiOhriUtil.map(tbADataSet));
		
		TBARTDataSetDefinition alreadyOnARTSetDefinition = new TBARTDataSetDefinition();
		alreadyOnARTSetDefinition.addParameters(getParameters());
		alreadyOnARTSetDefinition.setEncounterType(followUpEncounter);
		//alreadyOnARTSetDefinition.setNewlyEnrolled(false);
		reportDefinition.addDataSetDefinition("Disaggregated by Age/Sex/Result Already on ART",
		    EthiOhriUtil.map(alreadyOnARTSetDefinition));
		
		TBARTDataSetDefinition newlyEnrolledSetDefinition = new TBARTDataSetDefinition();
		newlyEnrolledSetDefinition.addParameters(getParameters());
		newlyEnrolledSetDefinition.setEncounterType(followUpEncounter);
		//newlyEnrolledSetDefinition.setNewlyEnrolled(true);
		reportDefinition.addDataSetDefinition("Disaggregated by Age/Sex/Result Newly on ARt",
		    EthiOhriUtil.map(newlyEnrolledSetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("81fa27e6-4685-49e4-9e37-ae11e679f4d5", reportDefinition);
		
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
