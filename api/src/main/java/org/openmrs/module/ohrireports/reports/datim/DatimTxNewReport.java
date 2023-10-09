package org.openmrs.module.ohrireports.reports.datim;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HTS_FOLLOW_UP_ENCOUNTER_TYPE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATIM_REPORT;

import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.AutoCalculateDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.BreastFeedingStatusDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.CoarseByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.FineByAgeAndSexDataSetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.PopulationTypeDataSetDefinition;
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
public class DatimTxNewReport implements ReportManager {
	
	private EncounterType followUpEncounter;
	
	@Override
	public String getUuid() {
		return "9f9f13aa-65cb-44c2-a2e8-1ff058f8c959";
	}
	
	@Override
	public String getName() {
		return DATIM_REPORT + "-TX_NEW";
	}
	
	@Override
	public String getDescription() {
		return "Aggregate report of DATIM TXnew lists a newly enrolled  patients";
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
		
		AutoCalculateDataSetDefinition aDefinition = new AutoCalculateDataSetDefinition();
		aDefinition.addParameters(getParameters());
		aDefinition.setEncounterType(followUpEncounter);
		aDefinition.setDescription("Number of adults and children newly enrolled on antiretroviral therapy (ART)");
		reportDefinition.addDataSetDefinition("Auto-Calculate",
		    EthiOhriUtil.map(aDefinition));
		
		FineByAgeAndSexDataSetDefinition fDefinition = new FineByAgeAndSexDataSetDefinition();
		fDefinition.addParameters(getParameters());
		fDefinition.setDescription("Disaggregated by Age/Sex (Fine disaggregate)");
		fDefinition.setEncounterType(followUpEncounter);
		reportDefinition.addDataSetDefinition("Required", EthiOhriUtil.map(fDefinition));
		
		CoarseByAgeAndSexDataSetDefinition cDefinition = new CoarseByAgeAndSexDataSetDefinition();
		cDefinition.addParameters(getParameters());
		cDefinition.setEncounterType(followUpEncounter);
		cDefinition.setDescription("Disaggregated by Age/Sex (Coarse disaggregated)");
		reportDefinition.addDataSetDefinition("Conditional",
		    EthiOhriUtil.map(cDefinition));
		
		BreastFeedingStatusDataSetDefinition bDefinition = new BreastFeedingStatusDataSetDefinition();
		bDefinition.addParameters(getParameters());
		bDefinition.setEncounterType(followUpEncounter);
		bDefinition.setDescription("Disaggregated by Breastfeeding Status at ART Initiation");
		reportDefinition.addDataSetDefinition("Breast-Feeding-ART-Status",
		    EthiOhriUtil.map(bDefinition));
		
		PopulationTypeDataSetDefinition pDefinition = new PopulationTypeDataSetDefinition();
		pDefinition.addParameters(getParameters());
		pDefinition.setEncounterType(followUpEncounter);
		pDefinition.setDescription("Disaggregated by Key population-type");
		reportDefinition.addDataSetDefinition("Breast-Feeding-Status",
		    EthiOhriUtil.map(pDefinition));
		
		return reportDefinition;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("c29ab966-7727-4e66-95e9-d1aeba22caf1", reportDefinition);
		
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
