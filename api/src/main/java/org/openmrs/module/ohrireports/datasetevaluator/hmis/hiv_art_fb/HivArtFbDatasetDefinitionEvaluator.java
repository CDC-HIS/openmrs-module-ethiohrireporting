package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fb;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_art_fb.HivArtFbDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Handler(supports = { HivArtFbDatasetDefinition.class })
public class HivArtFbDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private HivArtFbQuery fbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HivArtFbDatasetDefinition _DatasetDefinition = (HivArtFbDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(_DatasetDefinition, evalContext);
		fbQuery.setDate(_DatasetDefinition.getStartDate(), _DatasetDefinition.getEndDate(),
		    encounterQuery.getAliveFollowUpEncounters(_DatasetDefinition.getStartDate(), _DatasetDefinition.getEndDate()));
		List<Person> persons = fbQuery.getPersons(new Cohort(fbQuery.GetPatientsOnFamilyPlanning()));
		
		HivArtFbDatasetBuilder datasetBuilder = new HivArtFbDatasetBuilder(persons, dataSet,
		        _DatasetDefinition.getDescription(), "HIV_ART_FP");
		datasetBuilder.buildDataset();
		return dataSet;
	}
}
