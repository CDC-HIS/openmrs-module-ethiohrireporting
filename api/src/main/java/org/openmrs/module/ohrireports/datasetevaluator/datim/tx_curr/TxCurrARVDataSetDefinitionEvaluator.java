package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_curr;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_curr.TxCurrARVDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Handler(supports = { TxCurrARVDataSetDefinition.class })
public class TxCurrARVDataSetDefinitionEvaluator implements DataSetEvaluator {


	private TxCurrARVDataSetDefinition hdsd;

    @Autowired
	private EncounterQuery encounterQuery;

	// private int total = 0;
	List<Obs> obses = new ArrayList<>();
	List<Person> personList = new ArrayList<>();
    HashMap<Integer,Object> patientWithDispenseDay = new HashMap<>();
	private int total = 0;

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {
		total = 0;
		hdsd = (TxCurrARVDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

        PatientQueryService patientQueryService = Context.getService(PatientQueryService.class);

		List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(null,hdsd.getEndDate());
		Cohort baseCohort  = patientQueryService.getActiveOnArtCohort("",null,hdsd.getEndDate(),null,encounters);
		personList = patientQueryService.getPersons(baseCohort);
         patientWithDispenseDay = patientQueryService.getObsValue(baseCohort, FollowUpConceptQuestions.ARV_DISPENSED_IN_DAYS, PatientQueryImpDao.ObsValueType.CONCEPT_UUID,encounters);


		DataSetRow femaleDateSet = new DataSetRow();
		femaleDateSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "", Integer.class), "Female");

		femaleDateSet.addColumnValue(
				new DataSetColumn("3munknownAge", "<3 months of ARVs (not MMD)  Unknown Age", Integer.class),
				getUnknownAgeByGender(Arrays.asList(ConceptAnswer.ARV_30_Day, ConceptAnswer.ARV_60_Day),"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("3m<15", "<3 months of ARVs (not MMD) <15", Integer.class),
				getBelow15(Arrays.asList(ConceptAnswer.ARV_30_Day, ConceptAnswer.ARV_60_Day),"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("3m15+", "<3 months of ARVs (not MMD) 15+", Integer.class),
				getAbove15(Arrays.asList(ConceptAnswer.ARV_30_Day, ConceptAnswer.ARV_60_Day),"F"));

		femaleDateSet.addColumnValue(new DataSetColumn("5munknownAge", "3-5 months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(Arrays.asList(ConceptAnswer.ARV_90_Day, ConceptAnswer.ARV_120_Day, ConceptAnswer.ARV_150_Day),"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("5m<15", "3-5 months of ARVs <15", Integer.class),
				getBelow15(Arrays.asList(ConceptAnswer.ARV_90_Day, ConceptAnswer.ARV_120_Day, ConceptAnswer.ARV_150_Day),"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("5m15+", "3-5 months of ARVs 15+", Integer.class),
				getAbove15(Arrays.asList(ConceptAnswer.ARV_90_Day, ConceptAnswer.ARV_120_Day, ConceptAnswer.ARV_150_Day),"F"));

		femaleDateSet.addColumnValue(
				new DataSetColumn("6munknownAge", "6 or more months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(Arrays.asList(ConceptAnswer.ARV_180_Day),"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("6m<15", "6 or more months of ARVs <15", Integer.class),
				getBelow15(Arrays.asList(ConceptAnswer.ARV_180_Day),"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("6m15+", "6 or more months of ARVs 15+", Integer.class),
				getAbove15(Arrays.asList(ConceptAnswer.ARV_180_Day),"F"));

		set.addRow(femaleDateSet);

		DataSetRow maleDataSet = new DataSetRow();
		maleDataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "", Integer.class), "Male");

		maleDataSet.addColumnValue(
				new DataSetColumn("3munknownAge", "<3 months of ARVs (not MMD)  Unknown Age", Integer.class),
				getUnknownAgeByGender(Arrays.asList(ConceptAnswer.ARV_30_Day, ConceptAnswer.ARV_60_Day),"M"));
		maleDataSet.addColumnValue(new DataSetColumn("3m<15", "<3 months of ARVs (not MMD) <15", Integer.class),
				getBelow15(Arrays.asList(ConceptAnswer.ARV_30_Day, ConceptAnswer.ARV_60_Day),"M"));
		maleDataSet.addColumnValue(new DataSetColumn("3m15+", "<3 months of ARVs (not MMD) 15+", Integer.class),
				getAbove15(Arrays.asList(ConceptAnswer.ARV_30_Day, ConceptAnswer.ARV_60_Day),"M"));

		maleDataSet.addColumnValue(new DataSetColumn("5munknownAge", "3-5 months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(Arrays.asList(ConceptAnswer.ARV_90_Day, ConceptAnswer.ARV_120_Day, ConceptAnswer.ARV_150_Day),"M"));
		maleDataSet.addColumnValue(new DataSetColumn("5m<15", "3-5 months of ARVs <15", Integer.class),
				getBelow15(Arrays.asList(ConceptAnswer.ARV_90_Day, ConceptAnswer.ARV_120_Day, ConceptAnswer.ARV_150_Day),"M"));
		maleDataSet.addColumnValue(new DataSetColumn("5m15+", "3-5 months of ARVs 15+", Integer.class),
				getAbove15(Arrays.asList(ConceptAnswer.ARV_90_Day, ConceptAnswer.ARV_120_Day, ConceptAnswer.ARV_150_Day),"M"));

		maleDataSet.addColumnValue(
				new DataSetColumn("6munknownAge", "6 or more months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(Arrays.asList(ConceptAnswer.ARV_180_Day),"M"));
		maleDataSet.addColumnValue(new DataSetColumn("6m<15", "6 or more months of ARVs <15", Integer.class),
				getBelow15(Arrays.asList(ConceptAnswer.ARV_180_Day),"M"));
		maleDataSet.addColumnValue(new DataSetColumn("6m15+", "6 or more months of ARVs 15+", Integer.class),
				getAbove15(Arrays.asList(ConceptAnswer.ARV_180_Day),"M"));

		set.addRow(maleDataSet);

		DataSetRow tSetRow = new DataSetRow();
		tSetRow.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "", Integer.class), "Sub-Total");
		tSetRow.addColumnValue(new DataSetColumn("3munknownAge", "", Integer.class), total);

		set.addRow(tSetRow);

		return set;
	}

	private int getUnknownAgeByGender(List<String> dispenseDayInConceptUUId, String gender) {
		int count = 0;
		int _age =0;
		String dispenseValue;
		List<Integer> personIds = new ArrayList<>();
		for (Person person : personList) {
			if(!gender.equals(person.getGender()))
				continue;

			_age = person.getAge(hdsd.getEndDate());

			if (_age == 0) {
				dispenseValue = (String) patientWithDispenseDay.get(person.getPersonId());
				if (dispenseDayInConceptUUId.contains(dispenseValue)) {
					personIds.add(person.getPersonId());
					count++;

				}

			}
		}
		incrementTotalCount(count);
		clearCountedPerson(personIds);

		return count;
	}

	private int getAbove15(List<String> dispenseDayInConceptUUId,String gender) {
		int count = 0;
		int _age = 0;
		String dispenseValue ;
		List<Integer> personIntegers = new ArrayList<>();
		for (Person person : personList) {
			if (!gender.equals(person.getGender()) || personIntegers.contains(person.getPersonId()))
				continue;
			_age = person.getAge(hdsd.getEndDate());
			if (_age >= 15 ) {
				dispenseValue =  (String) patientWithDispenseDay.get(person.getPersonId());
				if (dispenseDayInConceptUUId.contains(dispenseValue)) {
					personIntegers.add(person.getPersonId());
					count++;
				}

			}
		}
		incrementTotalCount(count);
		clearCountedPerson(personIntegers);
		return count;
	}
	private int getBelow15(List<String> dispenseDayInConceptUUId,String gender) {
		int count = 0;
		int _age = 0;
		String dispenseValue ;
		List<Integer> personIntegers = new ArrayList<>();
		for (Person person : personList) {
			if (!gender.equals(person.getGender()) || personIntegers.contains(person.getPersonId()))
				continue;
			_age = person.getAge(hdsd.getEndDate());

			if (_age < 15 ) {
				dispenseValue =  (String) patientWithDispenseDay.get(person.getPersonId());
				if (dispenseDayInConceptUUId.contains(dispenseValue)) {
					personIntegers.add(person.getPersonId());
					count++;

				}

			}
		}
		incrementTotalCount(count);
		clearCountedPerson(personIntegers);
		return count;
	}
	private void clearCountedPerson(List<Integer> personIds) {
		for (int pId : personIds) {
			obses.removeIf(p -> p.getPersonId().equals(pId));
		}
	}

	private void incrementTotalCount(int count) {
		if (count > 0)
			total = total + count;
	}


}
