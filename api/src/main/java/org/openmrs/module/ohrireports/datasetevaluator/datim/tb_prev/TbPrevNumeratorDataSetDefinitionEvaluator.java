package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_prev;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_COMPLETED_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_START_DATE;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev.TbPrevNumeratorDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TbPrevNumeratorDataSetDefinition.class })
public class TbPrevNumeratorDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TbPrevNumeratorDataSetDefinition hdsd;
	
	private int maleTotal = 0;
	
	private int femaleTotal = 0;
	
	@Autowired
	private TBQuery tbQuery;
	
	List<Integer> baseEncounters;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Date endDate = null;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		maleTotal = 0;
		femaleTotal = 0;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		hdsd = (TbPrevNumeratorDataSetDefinition) dataSetDefinition;
		if (Objects.isNull(endDate) || !endDate.equals(hdsd.getEndDate()))
			baseEncounters = encounterQuery.getEncounters(Arrays.asList(TPT_COMPLETED_DATE), hdsd.getStartDate(),
			    hdsd.getEndDate());
		
		endDate = hdsd.getEndDate();
		
		Cohort tptCohort = tbQuery.getTPTCohort(baseEncounters, TPT_COMPLETED_DATE, hdsd.getStartDate(), endDate);
		Cohort onArtCorCohort = new Cohort(tbQuery.getArtStartedCohort(baseEncounters, null, endDate, tptCohort));
		if (!hdsd.getAggregateType()) {
			// #region newly enrolled on Art with TPT completed
			Cohort cohortByArt = new Cohort(tbQuery.getArtStartedCohort(baseEncounters, hdsd.getStartDate(), endDate,
			    onArtCorCohort));
			buildDataRow(set, tbQuery.getPersons(cohortByArt), "Newly enrolled on ART");
			// #endregion
			
			// #region already enrolled on ART with TPT completed
			cohortByArt = new Cohort(tbQuery.getArtStartedCohort(baseEncounters, null, hdsd.getStartDate(), onArtCorCohort));
			buildDataRow(set, tbQuery.getPersons(cohortByArt), "Previously enrolled on ART");
			// #endregion
			
			// #region total counted row by gender
			buildTotalRow(set);
			// #endregion
		} else {
			DataSetRow dataSet = new DataSetRow();
			dataSet.addColumnValue(new DataSetColumn("TPTPREVEnrolled", "Numerator", Integer.class), onArtCorCohort.size());
			set.addRow(dataSet);
		}
		
		return set;
	}
	
	private void buildTotalRow(SimpleDataSet dataSet) {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn("", "", Integer.class), "Sub-Total");
		row.addColumnValue(new DataSetColumn("f<15", "feSub-Total", Integer.class), femaleTotal);
		row.addColumnValue(new DataSetColumn("m<15", "maleSub-Total", Integer.class), maleTotal);
		row.addColumnValue(new DataSetColumn("total", "Total", Integer.class), femaleTotal + maleTotal);
		dataSet.addRow(row);
	}
	
	private Date getPrevSixMonth() {
		Calendar subSixMonth = Calendar.getInstance();
		subSixMonth.setTime(hdsd.getStartDate());
		subSixMonth.add(Calendar.MONTH, -6);
		Date prevSixMonth = subSixMonth.getTime();
		return prevSixMonth;
	}
	
	private int getUnknownAgeByGender(List<Person> persons, String gender) {
		int count = 0;
		int age = 0;
		for (Person person : persons) {
			age = person.getAge(hdsd.getEndDate());
			if (person.getGender().equals(gender) && (age <= 0)) {
				count++;
			}
			
		}
		if (gender.equals("M")) {
			femaleTotal = femaleTotal + count;
		} else {
			maleTotal = maleTotal + count;
		}
		return count;
	}
	
	private int getEnrolledByAgeAndGender(int min, int max, List<Person> persons, String gender) {
		int count = 0;
		int age = 0;
		for (Person person : persons) {
			age = person.getAge(hdsd.getEndDate());
			if (person.getGender().equals(gender) && (age >= min && age <= max)) {
				count++;
			}
			
		}
		
		if (gender.equals("M")) {
			femaleTotal = femaleTotal + count;
		} else {
			maleTotal = maleTotal + count;
		}
		return count;
	}
	
	private void buildDataRow(SimpleDataSet set, List<Person> persons, String desc) {
		DataSetRow row = new DataSetRow();
		int _total = 0;
		
		int value = getUnknownAgeByGender(persons, "F");
		_total = value;
		row.addColumnValue(new DataSetColumn("", "", String.class), desc);
		row.addColumnValue(new DataSetColumn("femaleKnownAge", "Female Unknown Age", Integer.class), value);
		value = getEnrolledByAgeAndGender(0, 14, persons, "F");
		_total += value;
		row.addColumnValue(new DataSetColumn("f<15", "Female <15", Integer.class), value);
		
		value = getEnrolledByAgeAndGender(15, 150, persons, "F");
		_total += value;
		row.addColumnValue(new DataSetColumn("f+15", "female +15", Integer.class), value);
		
		value = getUnknownAgeByGender(persons, "M");
		_total += value;
		row.addColumnValue(new DataSetColumn("maleKnownAge", "Male Unknown Age", Integer.class), value);
		
		value = getEnrolledByAgeAndGender(0, 14, persons, "M");
		_total += value;
		row.addColumnValue(new DataSetColumn("m<15", "Male <15", Integer.class), value);
		
		value = getEnrolledByAgeAndGender(15, 150, persons, "M");
		_total += value;
		row.addColumnValue(new DataSetColumn("m+15", "Male +15", Integer.class), value);
		
		row.addColumnValue(new DataSetColumn("total", "Total", Integer.class), _total);
		
		set.addRow(row);
	}
	
}
