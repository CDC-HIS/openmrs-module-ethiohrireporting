package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATIM_REPORT;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_cx_ca.CxCaTxAutoCalculateDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_cx_ca.CxCaTxByAgeandTreatmentTypeandScreeningVisitTypeDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_cx_ca.CxCaTxPostTreatmentFollowupDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_cx_ca.CxCaTxRescreenDataSetDefinition;
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
public class DatimCxCaTxReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "7529cxca-e57c-47d3-9dc3-57c4ad9e28bf";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-Cx_CA_Tx";
	}
	
	@Override
	public String getDescription() {
		return "Calculate the total number of HIV positive women who are currently on ART and received treatment for cervical cancer in the reporting period";
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
		
		CxCaTxAutoCalculateDataSetDefinition aDefinition = new CxCaTxAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition
		        .setDescription("Total Number of female clients Currently on ART and received treatment forCervical Cancer during the reporting period");
		reportDefinition.addDataSetDefinition("Auto-Calculate",
		    EthiOhriUtil.map(aDefinition));
		
		CxCaTxByAgeandTreatmentTypeandScreeningVisitTypeDataSetDefinition fDefinition = new CxCaTxByAgeandTreatmentTypeandScreeningVisitTypeDataSetDefinition();
		fDefinition.addParameters(getParameters());
		fDefinition.setDescription("Disaggregated by Age/Treatment Type/Screening Visit Type");
		fDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition(
		    "Required Disaggregated by Age/Treatment Type/Screening Visit Type, First time screened for cervical cancer",
		    EthiOhriUtil.map(fDefinition));
		
		CxCaTxRescreenDataSetDefinition rDefinition = new CxCaTxRescreenDataSetDefinition();
		rDefinition.addParameters(getParameters());
		rDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		rDefinition.setDescription("Rescreened after previous negative or suspected cancer");
		reportDefinition.addDataSetDefinition("Conditional Rescreened after previous negative or suspected cancer",
		    EthiOhriUtil.map(rDefinition));
		
		CxCaTxPostTreatmentFollowupDataSetDefinition pDefinition = new CxCaTxPostTreatmentFollowupDataSetDefinition();
		pDefinition.addParameters(getParameters());
		pDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		pDefinition.setDescription("Post treatment follow-up");
		reportDefinition.addDataSetDefinition("Conditional Post treatment follow-up",
		    EthiOhriUtil.map(pDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("bfe1cxca-383a-472b-8eac-17fc5bef95a1`", reportDefinition);
		
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
