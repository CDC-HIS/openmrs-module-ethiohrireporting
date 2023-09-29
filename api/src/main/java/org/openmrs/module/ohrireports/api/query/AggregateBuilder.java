package org.openmrs.module.ohrireports.api.query;

import java.util.Collection;

import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public interface AggregateBuilder extends OpenmrsService {
	
	void setLowerBoundAge(int lowerBoundAge);
	
	void setUpperBoundAge(int upperBoundAge);
	
	void setAgeInterval(int ageInterval);
	
	void setPersonList(Collection<Person> person);
	
	//Build data set using lower age to upper age by incrementing age interval to it reach's upper bound age disaggregation by gender
	void buildDataSetColumn(DataSetRow dataSet, String gender);
	
	//Build data set using middle age and gender, the middle age is use for dissociation the lower and upper age limit
	void buildDataSetColumn(SimpleDataSet dataSet, String gender, int middleAge);
	
}
