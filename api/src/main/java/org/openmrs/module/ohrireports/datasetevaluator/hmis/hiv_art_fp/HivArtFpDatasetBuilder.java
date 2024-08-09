package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fp;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

import java.util.*;

import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public class HivArtFpDatasetBuilder {
	
	private PatientQueryService patientQuery;
	
	private List<Person> personList;
	
	private SimpleDataSet dataSet;
	
	private String description;
	
	private String baseName;
	
	private final HashMap<Integer, Object> followUpDateMap;
	
	public HivArtFpDatasetBuilder(List<Person> personList, SimpleDataSet dataSet, String description, String baseName,
	    HashMap<Integer, Object> followUpDateMap) {
		this.personList = personList;
		this.dataSet = dataSet;
		this.description = description;
		this.baseName = baseName;
		this.followUpDateMap = followUpDateMap;
	}
	
	private String column_3_name = "Number";
	
	public void buildDataset() {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName);
		row.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), description);
		row.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), getCount(15, 49));
		row.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), getCount(15, 49));
		dataSet.addRow(row);
		buildRowByAge();
	}
	
	private void buildRowByAge() {
		int initialAgeRange = 10, maxAgeRange = 10;
		int maxAge = 49;
		int i = 1;
		while (initialAgeRange < 30) {
			
			maxAgeRange += 4;
			
			DataSetRow row = new DataSetRow();
			row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName + "." + i);
			row.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), initialAgeRange + " - "
			        + maxAgeRange + " years");
			row.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
			    getCount(initialAgeRange, maxAgeRange));
			maxAgeRange++;
			initialAgeRange = maxAgeRange;
			dataSet.addRow(row);
			i++;
		}
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName + "." + ++i);
		row.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), initialAgeRange + " - " + maxAge
		        + " years");
		row.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), getCount(initialAgeRange, maxAge));
		
		dataSet.addRow(row);
	}
	
	private Integer getCount(int initialAge, int maxAge) {
		List<Person> _personList = new ArrayList<>();
		for (Person person : personList) {
			//Age calculation is as of the report end date
			int age =person.getAge();
			Object date = followUpDateMap.get(person.getPersonId());
			if(Objects.nonNull(date)){
					age = person.getAge((Date) date);
			}
			if (person.getGender().equals("F") && age>= initialAge && age <= maxAge) {
				_personList.add(person);
			}
		}

		//personList.removeAll(_personList);

		return _personList.size();
	}
}
