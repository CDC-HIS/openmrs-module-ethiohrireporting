package org.openmrs.module.ohrireports.api.query;

import java.util.List;
import java.util.Date;

import org.openmrs.Person;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public interface AggregateBuilder extends OpenmrsService {
	
	void setLowerBoundAge(int lowerBoundAge);
	
	public void setCalculateAgeFrom(Date calculateAgeFrom);
	
	void setUpperBoundAge(int upperBoundAge);
	
	void setAgeInterval(int ageInterval);
	
	void setPersonList(List<Person> person);
	
	// Build data set using lower age to upper age by incrementing age interval to
	// it reach's upper bound age disaggregation by gender
	void buildDataSetColumn(DataSetRow dataSet, String gender);
	
	// Build data set using middle age and gender, the middle age is use for
	// dissociation the lower and upper age limit
	void buildDataSetColumn(DataSetRow row, String gender, int middleAge);
	
}
