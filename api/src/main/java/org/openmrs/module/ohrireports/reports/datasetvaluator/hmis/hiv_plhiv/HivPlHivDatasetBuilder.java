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
	
	public HivPlHivDatasetBuilder(List<Person> personList, List<Person> pregnantPersonList, SimpleDataSet dataSet,
	    String description, String baseName) {
		this.personList = personList;
		this.dataSet = dataSet;
		this.description = description;
		this.baseName = baseName;
		this.pregnantPersonList = pregnantPersonList;
		patientQuery = Context.getService(PatientQuery.class);
	}
	
	public DataSet getPlHivDataset() {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName);
		row.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), description);
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
	
	public DataSet getPlHivPregnantDataset() {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName);
		row.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), description);
		dataSet.addRow(row);
		
		dataSet.addRow(getPregnantDatasetRow("<", "M"));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow("<", "F"));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow("<", "F"));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow(">=", "M"));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow(">=", "F"));
		rowCounter++;
		dataSet.addRow(getPregnantDatasetRow(">=", "F"));
		rowCounter++;
		return dataSet;
	}
	
	private DataSetRow getDatasetRow(String compriseSign, String gender) {
		DataSetRow dataSetRow = new DataSetRow();
		
		dataSetRow
		        .addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName + "." + rowCounter);
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    getActivitiesName(compriseSign, gender));
		dataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    getCount(compriseSign, gender, false));
		
		return dataSetRow;
	}
	
	private DataSetRow getPregnantDatasetRow(String compriseSign, String gender) {
		DataSetRow dataSetRow = new DataSetRow();
		
		dataSetRow
		        .addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName + "." + rowCounter);
		dataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    getActivitiesName(compriseSign, gender, true));
		dataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    getCount(compriseSign, gender, true));
		return dataSetRow;
	}
	
	private String getActivitiesName(String compriseSign, String gender, Boolean isPregnant) {
		StringBuilder name = new StringBuilder(compriseSign);
		name.append(" 15 years,");
		
		if (gender.equals("M")) {
			name.append("Male");
			return name.toString();
		}
		
		name.append(" Female");
		if (isPregnant) {
			name.append("- pregnant");
			
		} else {
			name.append("- non-pregnant");
		}
		
		return name.toString();
	}
	
	private String getActivitiesName(String compriseSign, String gender) {
		StringBuilder activitiesName = new StringBuilder(compriseSign);
		activitiesName.append(" 15 years, ");
		activitiesName.append(gender == "M" ? "Male" : "Female");
		return activitiesName.toString();
	}
	
	private int getCount(String compriseSign, String gender, Boolean aggregatePregnant) {
		List<Integer> patientIdList = new ArrayList<>();
		List<Person> persons = aggregatePregnant ? pregnantPersonList : personList;
		for (Person person : persons) {
			if (compriseSign.equals(">=") && person.getAge() >= 15 && person.getGender().equals(gender)) {

				if (aggregatePregnant && gender.equals("F")) {

					if (isPersonPregnant(person))
						patientIdList.add(person.getPersonId());

				} else {

					patientIdList.add(person.getPersonId());

				}
			} else if (compriseSign.equals("<") && person.getAge() < 15 && person.getGender().equals(gender)) {

				if (aggregatePregnant && gender.equals("F")) {

					if (isPersonPregnant(person))
						patientIdList.add(person.getPersonId());

				} else {

					patientIdList.add(person.getPersonId());

				}
			}
		}
		clearCountedPerson(patientIdList);
		return patientIdList.size();
	}
	
	private boolean isPersonPregnant(Person person) {
		return pregnantPersonList.removeIf(p -> p.getPersonId().equals(person.getPersonId()));

	}
	
	private void clearCountedPerson(List<Integer> patientIdList) {
		for (Integer id : patientIdList) {
			personList.removeIf(p -> p.getPersonId().equals(id));
		}
	}
}
