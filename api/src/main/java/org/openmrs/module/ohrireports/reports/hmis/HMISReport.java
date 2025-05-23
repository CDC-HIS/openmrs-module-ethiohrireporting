package org.openmrs.module.ohrireports.reports.hmis;

import java.util.Collections;
import java.util.List;

import static org.openmrs.module.ohrireports.constants.ETHIOHRIReportsConstants.*;

import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.constants.ReportType;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.HMISDatasetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

//@Component
public class HMISReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "4bf142d2-f92c-4a9e-9368-55801e9c025d";
	}
	
	@Override
	public String getName() {
		return ReportType.HMIS_REPORT + "-DHIS 2";
	}
	
	@Override
	public String getDescription() {
		return "06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry Of Health)";
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
		
		HMISDatasetDefinition aDefinition = new HMISDatasetDefinition();
		aDefinition.addParameters(getParameters());
		reportDefinition.addDataSetDefinition(
		    "06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry Of Health)",
		    EthiOhriUtil.map(aDefinition));
		
		//		HmisTXCurrDataSetDefinition aDefinition = new HmisTXCurrDataSetDefinition();
		//		aDefinition.addParameters(getParameters());
		//		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		aDefinition.setDescription("06 - HIV | Hospital, Health center, Clinic |	Monthly (Federal Ministry Of Health)");
		//		reportDefinition.addDataSetDefinition(
		//		    "HMIS:06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry		Of Health)",
		//		    EthiOhriUtil.map(aDefinition));
		//
		//		HIVTXNewDatasetDefinition txNewDataset = new HIVTXNewDatasetDefinition();
		//		txNewDataset.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("HMIS:Number of adults and children		with HIV infection newly started on ART",
		//		    EthiOhriUtil.map(txNewDataset));
		//
		//		HIVARTRETDatasetDefinition hivArtRetDatasetDefinition = new HIVARTRETDatasetDefinition();
		//		hivArtRetDatasetDefinition.addParameters(getParameters());
		//		hivArtRetDatasetDefinition.setNetRetention(false);
		//		reportDefinition.addDataSetDefinition(
		//		    "HMIS:Number of adults and children who are still on treatment at 12 months	after initiating ART",
		//		    EthiOhriUtil.map(hivArtRetDatasetDefinition));
		//
		//		HIVARTRETDatasetDefinition hivArtRetDatasetDefinitionNet = new HIVARTRETDatasetDefinition();
		//		hivArtRetDatasetDefinitionNet.addParameters(getParameters());
		//		hivArtRetDatasetDefinitionNet.setNetRetention(true);
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "HMIS:Number of persons on ART in the original cohort including those transferred in, minus those transferred out (net current cohort)",
		//		            EthiOhriUtil.map(hivArtRetDatasetDefinitionNet));
		//
		//		HIVLinkageNewCtDatasetDefinition linkageNewDataset = new HIVLinkageNewCtDatasetDefinition();
		//		linkageNewDataset.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition(
		//		    "HMIS:Linkage outcome of newly identified Hiv positive individuals in the reporting period",
		//		    EthiOhriUtil.map(linkageNewDataset));
		//
		//		HivPvlsDatasetDefinition hivPvlsDataset = new HivPvlsDatasetDefinition();
		//		hivPvlsDataset.setParameters(getParameters());
		//		hivPvlsDataset.setType(HivPvlsType.TESTED);
		//		hivPvlsDataset.setPrefix(".1");
		//		hivPvlsDataset
		//		        .setDescription("Number of adult and pediatric ART patients for whom viral	load test result received in the reporting period (with in the past 12	months)");
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "HMIS:Viral load Suppression (Percentage of ART clients with a suppressed	viral load among those with a viral load test at 12 month in the reporting	period)",
		//		            EthiOhriUtil.map(hivPvlsDataset));
		//
		//		HivPvlsDatasetDefinition hivPvlsUnDataset = new HivPvlsDatasetDefinition();
		//		hivPvlsUnDataset.setParameters(getParameters());
		//		hivPvlsUnDataset.setType(HivPvlsType.SUPPRESSED);
		//		hivPvlsUnDataset.setPrefix("_UN");
		//		hivPvlsUnDataset
		//		        .setDescription("Total number of adult and pediatric ART patients with an undetectable viral load(<50 copies/ml) in the reporting period (with in the		past 12 months)");
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "HMIS:(UN) Viral load Suppression (Percentage of ART clients with a	uppressed viral load among those with a viral load test at 12 month in the		reporting period)",
		//		            EthiOhriUtil.map(hivPvlsUnDataset));
		//
		//		HivPvlsDatasetDefinition hivPvlsLvDataset = new HivPvlsDatasetDefinition();
		//		hivPvlsLvDataset.setParameters(getParameters());
		//		hivPvlsLvDataset.setType(HivPvlsType.LOW_LEVEL_LIVERMIA);
		//		hivPvlsLvDataset.setPrefix("_LV");
		//		hivPvlsLvDataset
		//		        .setDescription("Total number of adult and paediatric ART patients with low	level viremia (50 -1000 copies/ml) in the reporting period (with in the past		12 months)");
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "HMIS: (LV) Viral load Suppression (Percentage of ART clients with a		suppressed viral load among those with a viral load test at 12 month in the		reporting period)",
		//		            EthiOhriUtil.map(hivPvlsLvDataset));
		//
		//		HmisTXDsdDataSetDefinition txDSDDataset = new HmisTXDsdDataSetDefinition();
		//		txDSDDataset.setParameters(getParameters());
		//		txDSDDataset.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		reportDefinition.addDataSetDefinition(
		//		    "HMIS:Proportion of PLHIV currently on differentiated service Delivery model	(DSD) in the reporting period",
		//		    EthiOhriUtil.map(txDSDDataset));
		//
		//		HivArtIntrDatasetDefinition hivArtIntrDataset = new HivArtIntrDatasetDefinition();
		//		hivArtIntrDataset.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("HMIS: HIV_ART_INTR Number of ART Clients that interrupted Treatment",
		//		    EthiOhriUtil.map(hivArtIntrDataset));
		//
		//		HivArtReArvDatasetDefinition hivArtReArvDataset = new HivArtReArvDatasetDefinition();
		//		hivArtReArvDataset.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("HMIS: Number of ART clients restarted ARV treatment in the reporting period",
		//		    EthiOhriUtil.map(hivArtReArvDataset));
		//
		//		HivPrepDatasetDefinition hivPrepDatasetDefinition = new HivPrepDatasetDefinition();
		//		hivPrepDatasetDefinition.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("HMIS:Number of individuals receiving Pre-Exposure Prophylaxis",
		//		    EthiOhriUtil.map(hivPrepDatasetDefinition));
		//
		//		HivPrEpCurrDatasetDefinition hivPrepCurrDatasetDefinition = new HivPrEpCurrDatasetDefinition();
		//		hivPrepCurrDatasetDefinition.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition(
		//		    "HMIS:PrEP Curr (Number of individuals that received oral PrEP during the reporting period)",
		//		    EthiOhriUtil.map(hivPrepCurrDatasetDefinition));
		//
		//		HivPEPCategoryDatasetDefinition hivPrEpCatagoriesDatasetDefinition = new HivPEPCategoryDatasetDefinition();
		//		hivPrEpCatagoriesDatasetDefinition.setParameters(getParameters());
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "HMIS:Number of persons provided with post-exposure prophylaxis (PEP) for risk of HIV infection by exposure type",
		//		            EthiOhriUtil.map(hivPrEpCatagoriesDatasetDefinition));
		//
		//		HivPlHivDatasetDefinition hivPlTSPDatasetDefinition = new HivPlHivDatasetDefinition();
		//		hivPlTSPDatasetDefinition.setParameters(getParameters());
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "Proportion of clinically undernourished People Living with HIV (PLHIV) who received therapeutic or supplementary food",
		//		            EthiOhriUtil.map(hivPlTSPDatasetDefinition));
		//
		//		HivArtFbDatasetDefinition hivArtFpDatasetDefinition = new HivArtFbDatasetDefinition();
		//		hivArtFpDatasetDefinition.setParameters(getParameters());
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "FP:Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by age",
		//		            EthiOhriUtil.map(hivArtFpDatasetDefinition));
		//
		//		HivArtFbMetDatasetDefinition hivArtFpMeDatasetDefinition = new HivArtFbMetDatasetDefinition();
		//		hivArtFpMeDatasetDefinition.setParameters(getParameters());
		//		hivArtFpDatasetDefinition
		//		        .setDescription("Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by age");
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "FP-Method:Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by age",
		//		            EthiOhriUtil.map(hivArtFpMeDatasetDefinition));
		//
		//		HmisTbScrnDataSetDefinition tbscrnDataset = new HmisTbScrnDataSetDefinition();
		//		tbscrnDataset.setParameters(getParameters());
		//		tbscrnDataset.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		reportDefinition.addDataSetDefinition(
		//		    "HMIS:Proportion of patients enrolled in HIV care who were screened for TB (FD)",
		//		    EthiOhriUtil.map(tbscrnDataset));
		//
		//		HmisArtTptDataSetDefinition artTpt = new HmisArtTptDataSetDefinition();
		//		artTpt.setParameters(getParameters());
		//		artTpt.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "Number of ART patients who started on a standard course of TB Preventive Treatment (TPT) in the reporting period",
		//		            EthiOhriUtil.map(artTpt));
		//
		//		HmisArtTptCrOneDataSetDefinition artTptCr1 = new HmisArtTptCrOneDataSetDefinition();
		//		artTptCr1.setParameters(getParameters());
		//		artTptCr1.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		reportDefinition.addDataSetDefinition(
		//		    "Number of ART patients who were initiated on any course of TPT 12 months before the reporting period",
		//		    EthiOhriUtil.map(artTptCr1));
		//
		//		HmisArtTptCrTwoDataSetDefinition artTptCr2 = new HmisArtTptCrTwoDataSetDefinition();
		//		artTptCr2.setParameters(getParameters());
		//		artTptCr2.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		reportDefinition
		//		        .addDataSetDefinition(
		//		            "Number of ART patients who started TPT 12 months prior to the reporting period that completed a full course of therapy",
		//		            EthiOhriUtil.map(artTptCr2));
		//
		//		HmisCxCaScrnDataSetDefinition cxcaScrn = new HmisCxCaScrnDataSetDefinition();
		//		cxcaScrn.setParameters(getParameters());
		//		cxcaScrn.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		reportDefinition.addDataSetDefinition("Cervical Cancer screening by type of test", EthiOhriUtil.map(cxcaScrn));
		//
		//		HmisCxCaRxDataSetDefinition cxcaRx = new HmisCxCaRxDataSetDefinition();
		//		cxcaRx.setParameters(getParameters());
		//		cxcaRx.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		//		reportDefinition.addDataSetDefinition("Treatment of precancerous cervical lesion", EthiOhriUtil.map(cxcaRx));
		//
		//		TbLbLfLamDataSetDefinition tblblflam = new TbLbLfLamDataSetDefinition();
		//		tblblflam.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition(
		//		    "Total Number of tests performed using Lateral Flow Urine Lipoarabinomannan (LF-LAM) assay",
		//		    EthiOhriUtil.map(tblblflam));
		//
		//		HMISARTDatasetDefinition hmisartDatasetDefinition = new HMISARTDatasetDefinition();
		//		hmisartDatasetDefinition.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("PMTCT", EthiOhriUtil.map(hmisartDatasetDefinition));
		//		HMISEIDDatasetDefinition hmiseidDatasetDefinition = new HMISEIDDatasetDefinition();
		//		hmiseidDatasetDefinition.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("PMTCT - EID", EthiOhriUtil.map(hmiseidDatasetDefinition));
		//
		//		HMISHEICOTRDatasetDefinition hmisheicotrDatasetDefinition = new HMISHEICOTRDatasetDefinition();
		//		hmisheicotrDatasetDefinition.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("MTCT_HEI_COTR", EthiOhriUtil.map(hmisheicotrDatasetDefinition));
		//
		//		HMISHEIARVDatasetDefinition hmisheiarvDatasetDefinition = new HMISHEIARVDatasetDefinition();
		//		hmisheiarvDatasetDefinition.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("RMH_PMTCT_IARV", EthiOhriUtil.map(hmisheiarvDatasetDefinition));
		//
		//		HMISHEIABTSTDatasetDefinition hmisheiabtstDatasetDefinition = new HMISHEIABTSTDatasetDefinition();
		//		hmisheiabtstDatasetDefinition.setParameters(getParameters());
		//		reportDefinition.addDataSetDefinition("MTCT_HEI_ABTST", EthiOhriUtil.map(hmisheiabtstDatasetDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("d15829f9-ad58-4421-8d82-11dd80ffaeb2", reportDefinition);
		
		return Collections.singletonList(design);
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
