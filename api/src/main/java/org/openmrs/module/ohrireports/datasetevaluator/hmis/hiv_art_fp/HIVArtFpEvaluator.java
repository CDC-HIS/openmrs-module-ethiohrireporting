package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fp;

import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")
public class HIVArtFpEvaluator {
	
	@Autowired
	private HivArtFpQuery fbQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public void buildDataset(Date start, Date end, String description, SimpleDataSet dataSet) {
		fbQuery.generateReport(start, end);
		List<Person> persons = fbQuery.getPersons(fbQuery.getCohort());
		HivArtFpDatasetBuilder datasetBuilder = new HivArtFpDatasetBuilder(persons, dataSet, description, "HIV_ART_FP", end);
		datasetBuilder.buildDataset();
	}
}
