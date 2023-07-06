package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_plhiv;

import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.*;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQuery;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public class HivPlHivDatasetBuilder {
	
	private PatientQuery patientQuery;
	
	private List<Person> personList;
	
	private List<Person> pregnantPersonList;
	
	private SimpleDataSet dataSet;
	
	private String description;
	
	private String baseName;
	
	private int rowCounter = 1;
	
	private String column_3_name = "Tir 15";
	
	public HivPlHivDatasetBuilder(List<Person> personList, SimpleDataSet dataSet, String description, String baseName) {
		this.personList = personList;
		this.dataSet = dataSet;
		this.description = description;
		this.baseName = baseName;
		patientQuery = Context.getService(PatientQuery.class);
	}
	
	public DataSet getPlHivDataset() {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), description);
		dataSet.addRow(row);
		dataSet.addRow(getDatasetRow("<", "M"));
		rowCounter++;
		dataSet.addRow(getDatasetRow("<", "F"));
		rowCounter++;
		dataSet.addRow(getDatasetRow(">=", "M"));
		rowCounter++;
		dataSet.addRow(getDatasetRow(">=", "F"));
		rowCounter++;
		return dataSet;
	}
	
	public DataSet getPlHivPregnantDataset(List<Person> pregnantPersons) {
		this.pregnantPersonList = pregnantPersons;
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), description);
		dataSet.addRow(row);
		dataSet.addRow(getPregnantDatasetRow("<", "M", false));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow("<", "F", true));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow("<", "F", false));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow(">=", "M", false));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow(">=", "F", true));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow(">=", "F", false));
		rowCounter++;
		return dataSet;
	}
	
	private DataSetRow getDatasetRow(String compriseSign, String gender) {
		DataSetRow dataSetRow = new DataSetRow();
		
		dataSetRow
		        .addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName + "." + rowCounter);
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), compriseSign
		        + " 15 years, " + gender == "M" ? "Male" : "Female");
		dataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    getCount(compriseSign, gender, false));
		return dataSetRow;
	}
	
	private DataSetRow getPregnantDatasetRow(String compriseSign, String gender, Boolean isPregnant) {
		DataSetRow dataSetRow = new DataSetRow();
		
		dataSetRow
		        .addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName + "." + rowCounter);
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    getName(compriseSign, gender, isPregnant));
		dataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    getCount(compriseSign, gender, true));
		return dataSetRow;
	}
	
	private String getName(String compriseSign, String gender, Boolean isPregnant) {
		StringBuilder name = new StringBuilder(compriseSign + " 15 years,");
		name.append(gender.equals("M") ? " Male" : " Female");
		if (isPregnant) {
			name.append("- pregnant");
			
		} else {
			name.append("- non-pregnant");
		}
		
		return name.toString();
	}
	
	private int getCount(String compriseSign, String gender, Boolean isPregnant) {
        List<Integer> patientIdList = new ArrayList<>();
        List<Person> persons = isPregnant ? pregnantPersonList : personList;
        for (Person person : persons) {
            if (compriseSign.equals(">=") && person.getAge() >= 15 && person.getGender().equals(gender)) {
                patientIdList.add(person.getPersonId());
            } else if (compriseSign.equals("<") && person.getAge() < 15 && person.getGender().equals(gender)) {
                patientIdList.add(person.getPersonId());

            }
        }
        clearCountedPerson(patientIdList, isPregnant);
        return patientIdList.size();
    }
	
	private void clearCountedPerson(List<Integer> patientIdList, Boolean isPregnant) {
        for (Integer id : patientIdList) {
            if (isPregnant) {
                pregnantPersonList.removeIf(p -> p.getPersonId().equals(id));

            } else {
                personList.removeIf(p -> p.getPersonId().equals(id));

            }
        }
    }
}
