package org.openmrs.module.ohrireports.reports.hmis;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_fb.HivArtFbDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_fb.HivArtFbMetDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_intr.HivArtIntrDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_re_arv.HivArtReArvDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_ret.HIVARTRETDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_linkage_new_ct.HIVLinkageNewCtDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_p_r_ep_cat.HivPrEpCatagoriesDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_p_rep.HivPrepDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_plhiv.HivPlHivDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_plhiv.HivPvlHivType;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_pvls.HivPvlsDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_pvls.HivPvlsType;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.pr_ep_curr.HivPrEpCurrDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.tx_curr.HmisTXCurrDataSetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.tx_dsd.HmisTXDsdDataSetDefinition;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.tx_new.HIVTXNewDatasetDefinition;
import org.openmrs.api.context.Context;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

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
		HmisTXCurrDataSetDefinition aDefinition = new HmisTXCurrDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		aDefinition.setDescription("06 - HIV | Hospital, Health center, Clinic |	Monthly (Federal Ministry Of Health)");
		reportDefinition.addDataSetDefinition(
		    "HMIS:06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry		Of Health)",
		    map(aDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HIVTXNewDatasetDefinition txNewDataset = new HIVTXNewDatasetDefinition();
		txNewDataset.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("HMIS:Number of adults and children		with HIV infection newly started on ART",
		    map(txNewDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HIVARTRETDatasetDefinition hivArtRetDatasetDefinition = new HIVARTRETDatasetDefinition();
		hivArtRetDatasetDefinition.addParameters(getParameters());
		hivArtRetDatasetDefinition.setNetRetention(false);
		reportDefinition.addDataSetDefinition(
		    "HMIS:Number of adults and children who are still on treatment at 12 months		after\n" + //
		            "initiating ART", map(hivArtRetDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HIVARTRETDatasetDefinition hivArtRetDatasetDefinitionNet = new HIVARTRETDatasetDefinition();
		hivArtRetDatasetDefinitionNet.addParameters(getParameters());
		hivArtRetDatasetDefinitionNet.setNetRetention(true);
		reportDefinition
		        .addDataSetDefinition(
		            "HMIS:Number of persons on ART in the original cohort including those transferred in, minus those transferred out (net current cohort)",
		            map(hivArtRetDatasetDefinitionNet, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HIVLinkageNewCtDatasetDefinition linkageNewDataset = new HIVLinkageNewCtDatasetDefinition();
		linkageNewDataset.setParameters(getParameters());
		reportDefinition.addDataSetDefinition(
		    "HMIS:Linkage outcome of newly identified Hiv positive individuals in the reporting period",
		    map(linkageNewDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPvlsDatasetDefinition hivPvlsDataset = new HivPvlsDatasetDefinition();
		hivPvlsDataset.setParameters(getParameters());
		hivPvlsDataset.setType(HivPvlsType.TESTED);
		hivPvlsDataset.setPrefix(".1");
		hivPvlsDataset
		        .setDescription("Number of adult and pediatric ART patients for whom viral	load test result received in the reporting period (with in the past 12	months)");
		reportDefinition
		        .addDataSetDefinition(
		            "HMIS:Viral load Suppression (Percentage of ART clients with a suppressed	viral load among those with a viral load test at 12 month in the reporting	period)",
		            map(hivPvlsDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPvlsDatasetDefinition hivPvlsUnDataset = new HivPvlsDatasetDefinition();
		hivPvlsUnDataset.setParameters(getParameters());
		hivPvlsUnDataset.setType(HivPvlsType.SUPPRESSED);
		hivPvlsUnDataset.setPrefix("_UN");
		hivPvlsUnDataset
		        .setDescription("Total number of adult and pediatric ART patients with an		undetectable viral load(<50 copies/ml) in the reporting period (with in the		past 12 months)");
		reportDefinition
		        .addDataSetDefinition(
		            "HMIS:(UN) Viral load Suppression (Percentage of ART clients with a		suppressed viral load among those with a viral load test at 12 month in the		reporting period)",
		            map(hivPvlsUnDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPvlsDatasetDefinition hivPvlsLvDataset = new HivPvlsDatasetDefinition();
		hivPvlsLvDataset.setParameters(getParameters());
		hivPvlsLvDataset.setType(HivPvlsType.LOW_LEVEL_LIVERMIA);
		hivPvlsLvDataset.setPrefix("_LV");
		hivPvlsLvDataset
		        .setDescription("Total number of adult and paediatric ART patients with low		level viremia (50 -1000 copies/ml) in the reporting period (with in the past		12 months)");
		reportDefinition
		        .addDataSetDefinition(
		            "HMIS: (LV) Viral load Suppression (Percentage of ART clients with a		suppressed viral load among those with a viral load test at 12 month in the		reporting period)",
		            map(hivPvlsLvDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HmisTXDsdDataSetDefinition txDSDDataset = new HmisTXDsdDataSetDefinition();
		txDSDDataset.setParameters(getParameters());
		txDSDDataset.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(HTS_FOLLOW_UP_ENCOUNTER_TYPE));
		reportDefinition.addDataSetDefinition(
		    "HMIS:Proportion of PLHIV currently on differentiated service Delivery model	(DSD) in the reporting period",
		    map(txDSDDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivArtIntrDatasetDefinition hivArtIntrDataset = new HivArtIntrDatasetDefinition();
		hivArtIntrDataset.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("HMIS: HIV_ART_INTR Number of ART Clients that interrupted Treatment",
		    map(hivArtIntrDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivArtReArvDatasetDefinition hivArtReArvDataset = new HivArtReArvDatasetDefinition();
		hivArtReArvDataset.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("HMIS: Number of ART clients restarted ARV treatment in the reporting period",
		    map(hivArtReArvDataset, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPrepDatasetDefinition hivPrepDatasetDefinition = new HivPrepDatasetDefinition();
		hivPrepDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition("HMIS:Number of individuals receiving Pre-Exposure Prophylaxis",
		    map(hivPrepDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPrEpCurrDatasetDefinition hivPrepCurrDatasetDefinition = new HivPrEpCurrDatasetDefinition();
		hivPrepCurrDatasetDefinition.setParameters(getParameters());
		reportDefinition.addDataSetDefinition(
		    "HMIS:PrEP Curr (Number of individuals that received oral PrEP during the reporting period)",
		    map(hivPrepCurrDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPrEpCatagoriesDatasetDefinition hivPrEpCatagoriesDatasetDefinition = new HivPrEpCatagoriesDatasetDefinition();
		hivPrEpCatagoriesDatasetDefinition.setParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "HMIS:Number of persons provided with post-exposure prophylaxis (PEP) for risk of HIV infection by exposure type",
		            map(hivPrEpCatagoriesDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPlHivDatasetDefinition hivPlTSPDatasetDefinition = new HivPlHivDatasetDefinition();
		hivPlTSPDatasetDefinition.setHivPvlHivType(HivPvlHivType.PLHIV_TSP);
		hivPlTSPDatasetDefinition.setParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "Proportion of clinically undernourished People Living with HIV (PLHIV) who received therapeutic or supplementary food",
		            map(hivPlTSPDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPlHivDatasetDefinition hivPlNUTDatasetDefinition = new HivPlHivDatasetDefinition();
		hivPlNUTDatasetDefinition.setHivPvlHivType(HivPvlHivType.PLHIV_NUT);
		hivPlNUTDatasetDefinition.setParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "Number of PLHIV that were nutritionally assessed and found to be clinically undernourished (disaggregated by Age, Sex and Pregnancy)",
		            map(hivPlNUTDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivPlHivDatasetDefinition hivPlSUPDatasetDefinition = new HivPlHivDatasetDefinition();
		hivPlSUPDatasetDefinition.setHivPvlHivType(HivPvlHivType.PLHIV_SUP);
		hivPlSUPDatasetDefinition.setParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "Clinically undernourished PLHIV who received therapeutic or supplementary food (disaggregated by age, sex and pregnancy status)",
		            map(hivPlSUPDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));

		HivArtFbDatasetDefinition hivArtFpDatasetDefinition = new HivArtFbDatasetDefinition();
		hivArtFpDatasetDefinition.setParameters(getParameters());
		reportDefinition
		        .addDataSetDefinition(
		            "FP:Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by age",
		            map(hivArtFpDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
		HivArtFbMetDatasetDefinition hivArtFpMeDatasetDefinition = new HivArtFbMetDatasetDefinition();
		hivArtFpMeDatasetDefinition.setParameters(getParameters());
		hivArtFpDatasetDefinition
		        .setDescription("Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by age");
		reportDefinition
		        .addDataSetDefinition(
		            "FP-Method:Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by age",
		            map(hivArtFpMeDatasetDefinition, "startDate=${startDateGC},endDate=${endDateGC}"));
		
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
