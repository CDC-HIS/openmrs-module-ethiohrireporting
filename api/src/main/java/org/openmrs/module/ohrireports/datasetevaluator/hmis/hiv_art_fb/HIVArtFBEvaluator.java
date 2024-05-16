package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fb;

import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_art_fb.HivArtFbDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")
public class HIVArtFBEvaluator {
	
	@Autowired
	private HivArtFbQuery fbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public void buildDataset(Date start, Date end, String description, SimpleDataSet dataSet) {
		fbQuery.generateReport(start, end);
		List<Person> persons = fbQuery.getPersons(fbQuery.getCohort());
		HivArtFbDatasetBuilder datasetBuilder = new HivArtFbDatasetBuilder(persons, dataSet, description, "HIV_ART_FP");
		datasetBuilder.buildDataset();
		
	}
}
