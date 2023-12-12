package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_plhiv;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_plhiv.HivPlHivDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivPlHivDatasetDefinition.class })
public class HivPlHivDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private HivPlvHivQuery hivPlvHivQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HivPlHivDatasetDefinition _datasetDefinition = (HivPlHivDatasetDefinition) dataSetDefinition;
		hivPlvHivQuery.setDates(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate(),
		    encounterQuery.getAliveFollowUpEncounters(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate()));
		
		SimpleDataSet dataSet = new SimpleDataSet(_datasetDefinition, evalContext);
		switch (_datasetDefinition.getHivPvlHivType()) {
			case PLHIV_TSP:
				List<Person> assessedPatients = hivPlvHivQuery.getPersons(new Cohort(hivPlvHivQuery.getAssessedPatients()));
				HivPlHivDatasetBuilder tspDatasetBuilder = new HivPlHivDatasetBuilder(assessedPatients, dataSet,
				        "Number of PLHIV who were assessed/screened for malnutrition", "HIV_PLHIV_TSP.1");
				tspDatasetBuilder.getPlHivDataset();
				break;
			case PLHIV_NUT:
				List<Person> moderatePatients = hivPlvHivQuery.getPersons(new Cohort(hivPlvHivQuery
				        .getPatientModerateMalNutrition()));
				HivPlHivDatasetBuilder moderateDatasetBuilder = new HivPlHivDatasetBuilder(moderatePatients, dataSet,
				        "Total MAM", "HIV_PLHIV_NUT_MAM");
				moderateDatasetBuilder.getPlHivDataset();
				
				List<Person> severePatients = hivPlvHivQuery.getPersons(new Cohort(hivPlvHivQuery
				        .getPatientSevereMalNutrition()));
				HivPlHivDatasetBuilder severeDatasetBuilder = new HivPlHivDatasetBuilder(severePatients, dataSet,
				        "Total SAM", "HIV_PLHIV_NUT_SAM");
				severeDatasetBuilder.getPlHivDataset();
				break;
			case PLHIV_SUP:
				
				List<Person> supPregnantList = hivPlvHivQuery.getPersons(new Cohort(hivPlvHivQuery.getPregnant()));
				List<Person> supPatients = hivPlvHivQuery
				        .getPersons(new Cohort(hivPlvHivQuery.getPatientMATookSupplement()));
				HivPlHivDatasetBuilder supDatasetBuilder = new HivPlHivDatasetBuilder(supPatients, supPregnantList, dataSet,
				        "Total MAM who received therapeutic or supplementary food", "HIV_PLHIV_SUP.1");
				supDatasetBuilder.getPlHivPregnantDataset();
				
				List<Person> sevSupPatients = hivPlvHivQuery.getPersons(new Cohort(hivPlvHivQuery
				        .getPatientSVTookSupplement()));
				HivPlHivDatasetBuilder sevSupDatasetBuilder = new HivPlHivDatasetBuilder(sevSupPatients, supPregnantList,
				        dataSet, "Total SAM who received therapeutic or supplementary food", "HIV_PLHIV_SUP.2");
				sevSupDatasetBuilder.getPlHivPregnantDataset();
				break;
			default:
				break;
		}
		
		return dataSet;
	}
	
}
