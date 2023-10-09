package org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

/**
 * HmisPrepDatasetBuilder
 */
public class HmisPrepDatasetBuilder {
	
	private String baseName = "";
	
	private String column_3_name = "Number";
	
	private int initialAge = 15;
	
	private int min, max = 0;
	
	private int maxAge = 50;
	
	private int incremental = 1;
	
	private SimpleDataSet dataSet;
	
	private List<Person> personList;
	
	public HmisPrepDatasetBuilder(SimpleDataSet dataSet, List<Person> person, String name) {
		this.dataSet = dataSet;
		this.personList = person;
		this.baseName = name;
		buildPrepDataSet();
	}
	
	private SimpleDataSet buildPrepDataSet() {
		DataSetRow subHeaderDataSetRow = new DataSetRow();
		subHeaderDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName);
		subHeaderDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "By age and sex");
		subHeaderDataSetRow
		        .addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), personList.size());
		for (int i = initialAge; i <= maxAge; i++) {
			min = i;
			i = i + 4;
			max = i;
			dataSet.addRow(getRow(min, max, "M"));
			dataSet.addRow(getRow(min, max, "F"));
			
		}
		return dataSet;
	}
	
	private DataSetRow getRow(int min, int max, String gender) {
		
		DataSetRow dataSetRow = new DataSetRow();
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
		    baseName.concat(". " + incremental));
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    getDescription(min, max, gender));
		dataSetRow
		        .addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), getCount(min, max, gender));
		incremental++;
		return dataSetRow;
	}
	
	private int getCount(int min, int max, String gender) {
		List<Integer> count = new ArrayList<>();
		for (Person person : personList) {
			if (max == 50) {
				if (person.getAge() >= max && person.getGender().equals(gender)) {
					count.add(person.getPersonId());
				}
			} else {
				if (person.getAge() >= min && person.getAge() <= max && person.getGender().equals(gender))
					count.add(person.getPersonId());

			}
		}
		cleanList(count);
		return count.size();
	}
	
	private void cleanList(List<Integer> counted) {
		for (Integer id : counted) {
			personList.removeIf(p -> p.getPersonId().equals(id));
		}
	}
	
	private String getDescription(int min, int max, String gender) {
		StringBuilder description = new StringBuilder();
		if (max == 50) {
			description.append(">= 50 years, ");
			description.append(gender.equals("M") ? "Male" : "Female");
			
		} else {
			description.append(min);
			description.append(" - ");
			description.append(max);
			description.append(" years, ");
			description.append(gender.equals("M") ? "Male" : "Female");
		}
		
		return description.toString();
	}
	
}
