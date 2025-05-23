package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.List;
import static org.openmrs.module.ohrireports.constants.EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.*;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

//@Component
public class DatimTxTbDenominatorReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "3172dd1a-ca9b-4146-9053-b48b3428dd21";
	}
	
	@Override
	public String getName() {
		return ReportType.DATIM_REPORT_TREATMENT + "-TX_TB (Denominator)";
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
		
		TxTbDenominatorAutoCalculateDataSetDefinition headerDefinition = new TxTbDenominatorAutoCalculateDataSetDefinition();
		headerDefinition.addParameters(getParameters());
		headerDefinition.setHeader(true);
		headerDefinition.setDescription("DSD: TX_TB_Denominator");
		reportDefinition.addDataSetDefinition("DSD: TX_TB_Denominator", EthiOhriUtil.map(headerDefinition));
		
		TxTbDenominatorAutoCalculateDataSetDefinition aDefinition = new TxTbDenominatorAutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition
		        .setDescription("Number of ART patients who were screened for TB at least once during the reporting period. Denominator will auto-calculate from Start on ART by Screen Result by Age/Sex");
		reportDefinition
		        .addDataSetDefinition(
		            "Auto-Calculate : Number of ART patients who were screened for TB at least once during the reporting period. Denominator will auto-calculate from Start on ART by Screen Result by Age/Sex",
		            EthiOhriUtil.map(aDefinition));
		
		TxTbDenominatorARTByAgeAndSexDataSetDefinition cDefinition = new TxTbDenominatorARTByAgeAndSexDataSetDefinition();
		
		cDefinition.addParameters(getParameters());
		cDefinition.setDescription("Disaggregated by Start of ART Screen Result by Age/Sex");
		reportDefinition.addDataSetDefinition("Required : Disaggregated by Start of ART Screen Result by Age/Sex",
		    EthiOhriUtil.map(cDefinition));
		
		TxTbDenominatorSpecimenTypeDataSetDefinition typeDefinition = new TxTbDenominatorSpecimenTypeDataSetDefinition();
		
		typeDefinition.addParameters(getParameters());
		typeDefinition.setDescription("Disaggregated by Screen Type");
		reportDefinition.addDataSetDefinition("Required : Disaggregated by Screen Type", EthiOhriUtil.map(typeDefinition));
		
		TxTbDenominatorSpecimenSentDataSetDefinition sDefinition = new TxTbDenominatorSpecimenSentDataSetDefinition();
		sDefinition.addParameters(getParameters());
		sDefinition.setDescription("Disaggregated by Specimen Sent");
		reportDefinition.addDataSetDefinition("Required : Disaggregated by Specimen Sent", EthiOhriUtil.map(sDefinition));
		
		TxTbDenominatorDiagnosticTestDataSetDefinition tDefinition = new TxTbDenominatorDiagnosticTestDataSetDefinition();
		tDefinition.addParameters(getParameters());
		tDefinition.setDescription("Disaggregated by Specimen Sent and Diagnostic Test");
		reportDefinition.addDataSetDefinition("Required : [Disagg by Specimen Sent] Diagnostic Test",
		    EthiOhriUtil.map(tDefinition));
		
		TxTbDenominatorPositiveResultReturnedDataSetDefinition pDefinition = new TxTbDenominatorPositiveResultReturnedDataSetDefinition();
		pDefinition.addParameters(getParameters());
		pDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		pDefinition.setDescription("Disaggregated by Positive Result Returned");
		reportDefinition.addDataSetDefinition("Required: Disaggregated by Positive Result Returned",
		    EthiOhriUtil.map(pDefinition));
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("006145f4-a8bb-4876-ad6d-f2a020778534", reportDefinition);
		
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
