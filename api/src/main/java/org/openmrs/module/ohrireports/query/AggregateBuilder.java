package org.openmrs.module.ohrireports.query;

import java.util.Collection;

import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.dataset.DataSetRow;

public interface AggregateBuilder extends OpenmrsService {
	
	void setLowerBoundAge(int lowerBoundAge);
	
	void setUpperBoundAge(int upperBoundAge);
	
	void setAgeInterval(int ageInterval);
	
	void setPersonList(Collection<Person> person);
	
	void buildDataSetColumn(DataSetRow dataSet, String gender);
	
}
