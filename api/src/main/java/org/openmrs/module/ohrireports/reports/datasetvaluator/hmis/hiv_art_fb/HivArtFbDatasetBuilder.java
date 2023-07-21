package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_art_fb;

import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.*;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.query.PatientQuery;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public class HivArtFbDatasetBuilder {
	
	private PatientQuery patientQuery;
	
	private List<Person> personList;
	
	private SimpleDataSet dataSet;
	
	private String description;
	
	private String baseName;
	
	public HivArtFbDatasetBuilder(List<Person> personList, SimpleDataSet dataSet, String description, String baseName) {
		this.personList = personList;
		this.dataSet = dataSet;
		this.description = description;
		this.baseName = baseName;
	}
	
	private String column_3_name = "Number";
	
	public void buildDataset() {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName);
		row.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), description);
		dataSet.addRow(row);
		buildRowByAge();
	}
	
	private void buildRowByAge() {
		int initialAgeRange = 10, maxAgeRange = 10;
		int maxAge = 49;
		for (int i = 1; initialAgeRange <= maxAge; i++) {
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
		}
	}
	
	private Integer getCount(int initialAge, int maxAge) {
        List<Person> _personList = new ArrayList<>();
        for (Person person : personList) {
            if (person.getGender().equals("F") && person.getAge() >= initialAge && person.getAge() <= maxAge) {
                _personList.add(person);
            }
        }

        personList.removeAll(_personList);

        return _personList.size();
    }
}
