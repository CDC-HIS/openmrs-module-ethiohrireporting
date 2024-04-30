package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd;

import org.openmrs.Person;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.ColumnBuilder;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RowBuilder extends ColumnBuilder {
	
	private  List<Person> personList = new ArrayList<>();
	
	private  Date endDateReport= Calendar.getInstance().getTime();
	
	private List<DataSetRow> rowList;
	
	public int getTotalCount() {
		return totalCount;
	}
	
	private int totalCount;
	
	public List<DataSetRow> getRowList() {
		return rowList;
	}
	public RowBuilder(){}
	public RowBuilder(List<Person> personList, Date endDateReport) {
		this.personList = personList;
		this.endDateReport = endDateReport;
		this.totalCount = 0;
		rowList = new ArrayList<>();
	}
	
	public void buildDataSetRow(String identifier, int minAge, int maxAge) {
		if (minAge == 1) {
			buildRowForAllAgeRange(identifier, minAge, maxAge);
		} else {
			buildRowForAdultOnly(identifier, minAge, maxAge);
		}
	}
	
	public void buildDataSetRow(String identifier, int minAge, int maxAge, int stopIntervalAge) {
		if (minAge == 1) {
			buildRowForAllAgeRange(identifier,stopIntervalAge, minAge, maxAge);
		} else {
			buildRowForAdultOnly(identifier, stopIntervalAge,minAge, maxAge);
		}
	}
	
	private void buildRowForAllAgeRange(String identifier, int stopIntervalAge, int minAge, int maxAge) {
		int _minAge = minAge;
		int _maxAge = 4;
		int counter = 1;
		rowList.add(buildColumn(identifier + "." + counter, "< 1 year, Male ", countBelowOneYear("M")));
		rowList.add(buildColumn(identifier + "." + counter, "< 1 year, Female ", countBelowOneYear("F")));
		
		do {
			
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Male ",
					countByAgeRange(_minAge, _maxAge, "M")));
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Female ",
					countByAgeRange(_minAge, _maxAge, "F")));
			
			_minAge = _maxAge + 1;
			_maxAge = _minAge + 4;
			counter++;
			
			if(stopIntervalAge == _minAge){
				_maxAge = maxAge;
			}
			if(stopIntervalAge < _minAge){
				_maxAge = maxAge+1;
			}
		} while (_maxAge <= maxAge);
		
		rowList.add(buildColumn(identifier + "." + counter, ">= "+_maxAge+" years, Male ", countAboveAndFifteenYear("M")));
		rowList.add(buildColumn(identifier + "." + counter, ">= "+_maxAge+" years, Female ", countAboveAndFifteenYear("F")));
		
	}
	
	private void buildRowForAdultOnly(String identifier, int stopIntervalAge, int minAge, int maxAge) {
		int _minAge = minAge;
		int _maxAge = 19;
		int counter = 1;
		do {
			
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Male ",
					countByAgeRange(_minAge, _maxAge, "M")));
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Female ",
					countByAgeRange(_minAge, _maxAge, "F")));
			
			_minAge = _maxAge + 1;
			_maxAge = _minAge + 4;
			counter++;
			if(stopIntervalAge == _minAge){
				_maxAge = maxAge;
			}
			if(stopIntervalAge< _minAge){
				_maxAge = maxAge+1;
			}
		} while (_maxAge <= maxAge);
		
		rowList.add(buildColumn(identifier + "." + counter, ">= "+_maxAge+" years, Male ", countAboveAndFifteenYear("M")));
		rowList.add(buildColumn(identifier + "." + counter, ">= "+_maxAge+" years, Female ", countAboveAndFifteenYear("F")));
		
	}
	
	public DataSetRow buildDatasetColumn(String identifier, String description, String value) {
		return super.buildColumn(identifier, description, value);
	}
	public DataSetRow buildDatasetColumn(String identifier, String description, int value) {
		return super.buildColumn(identifier, description, value);
	}
	public void updateDataset(SimpleDataSet dataSet) {
		for (DataSetRow row : rowList) {
			dataSet.addRow(row);
		}
	}
	
	private void buildRowForAllAgeRange(String identifier, int minAge, int maxAge) {
		int _minAge = 1;
		int _maxAge = 4;
		int counter = 1;
		rowList.add(buildColumn(identifier + "." + counter, "< 1 year, Male ", countBelowOneYear("M")));
		rowList.add(buildColumn(identifier + "." + counter, "< 1 year, Female ", countBelowOneYear("F")));
		
		do {
			
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Male ",
			    countByAgeRange(_minAge, _maxAge, "M")));
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Female ",
			    countByAgeRange(_minAge, _maxAge, "F")));
			
			_minAge = _maxAge + 1;
			_maxAge = _minAge + 4;
			counter++;
			
		} while (_maxAge < maxAge);
		
		rowList.add(buildColumn(identifier + "." + counter, ">= 50 years, Male ", countAboveAndFifteenYear("M")));
		rowList.add(buildColumn(identifier + "." + counter, ">= 50 years, Female ", countAboveAndFifteenYear("F")));
		
	}
	
	private void buildRowForAdultOnly(String identifier, int minAge, int maxAge) {
		int _minAge = 15;
		int _maxAge = 19;
		int counter = 1;
		do {
			
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Male ",
			    countByAgeRange(_minAge, _maxAge, "M")));
			rowList.add(buildColumn(identifier + "." + counter, _minAge + " - " + _maxAge + " year, Female ",
			    countByAgeRange(_minAge, _maxAge, "F")));
			
			_minAge = _maxAge + 1;
			_maxAge = _minAge + 4;
			counter++;
			
		} while (_maxAge < maxAge);
		
		rowList.add(buildColumn(identifier + "." + counter, ">= 50 years, Male ", countAboveAndFifteenYear("M")));
		rowList.add(buildColumn(identifier + "." + counter, ">= 50 years, Female ", countAboveAndFifteenYear("F")));
		
	}
	
	private int count(int minAge, int maxAge, String gender) {
		if (maxAge == 1) {
			return countBelowOneYear(gender);
		} else if (minAge == 50) {
			return countAboveAndFifteenYear(gender);
		} else {
			return countByAgeRange(minAge, maxAge, gender);
		}
		
	}
	
	private int countByAgeRange(int minAge, int maxAge,String gender ){
		List<Integer> personIdList = new ArrayList<>();
		int age = 0;
		for (Person person:personList){
			age = person.getAge(endDateReport);
			
			if(person.getGender().equals(gender) &&
					   age>=minAge && age<=maxAge){
				personIdList.add(person.getPersonId());
			}
		}
		totalCount+=personIdList.size();
		return personIdList.size();
	}
	
	private int countBelowOneYear(String gender ){
		List<Integer> personIdList = new ArrayList<>();
		int age = 0;
		for (Person person:personList) {
			age = person.getAge(endDateReport);
			if (person.getGender().equals(gender) && age < 1) {
				personIdList.add(person.getPersonId());
			}
		}
		totalCount+=personIdList.size();
		return personIdList.size();
	}
	
	private int countAboveAndFifteenYear(String gender ){
		List<Integer> personIdList = new ArrayList<>();
		int age = 0;
		for (Person person:personList) {
			age = person.getAge(endDateReport);
			if (person.getGender().equals(gender) && age >= 50) {
				personIdList.add(person.getPersonId());
			}
		}
		totalCount+=personIdList.size();
		return personIdList.size();
	}
	
	private void clearCountedPerson(List<Integer> perosnIdList){
		for (Integer personId:perosnIdList){
			personList.removeIf(p->p.getPersonId().equals(personId));
		}
	}
}
