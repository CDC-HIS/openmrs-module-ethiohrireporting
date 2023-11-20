package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_curr;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
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
import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ARV_DISPENSED_IN_DAYS;

@Handler(supports = { TxCurrARVDataSetDefinition.class })
public class TxCurrARVDataSetDefinitionEvaluator implements DataSetEvaluator {


	private TxCurrARVDataSetDefinition hdsd;

    @Autowired
	private EncounterQuery encounterQuery;

	// private int total = 0;
	List<Obs> obses = new ArrayList<>();
	List<Person> personList = new ArrayList<>();
    HashMap<Integer,Object> patientWithDispenseDay = new HashMap<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		hdsd = (TxCurrARVDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

        PatientQueryService patientQueryService = Context.getService(PatientQueryService.class);

		List<Integer> encounters = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		Cohort baseCohort  = patientQueryService.getActiveOnArtCohort("",null,hdsd.getEndDate(),null,encounters);
		personList = patientQueryService.getPersons(baseCohort);
         patientWithDispenseDay = patientQueryService.getObsValue(baseCohort,ARV_DISPENSED_IN_DAYS, PatientQueryImpDao.ObsValueType.NUMERIC_VALUE,encounters);

		DataSetRow femaleDateSet = new DataSetRow();
		femaleDateSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender", Integer.class), "Female");

		femaleDateSet.addColumnValue(
				new DataSetColumn("3munknownAge", "<3 months of ARVs (not MMD)  Unknown Age", Integer.class),
				getUnknownAgeByGender(0.0, 89.0,"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("3m<15", "<3 months of ARVs (not MMD) <15", Integer.class),
				getEnrolledByAgeAndGender(0.0, 89.0, 0, 14,"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("3m15+", "<3 months of ARVs (not MMD) 15+", Integer.class),
				getEnrolledByAgeAndGender(0.0, 89.0, 15, 200,"F"));

		femaleDateSet.addColumnValue(new DataSetColumn("5munknownAge", "3-5 months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(90.0, 179.0,"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("5m<15", "3-5 months of ARVs <15", Integer.class),
				getEnrolledByAgeAndGender(90.0, 179.0, 0, 14,"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("5m15+", "3-5 months of ARVs 15+", Integer.class),
				getEnrolledByAgeAndGender(90.0, 179.0, 15, 200,"F"));

		femaleDateSet.addColumnValue(
				new DataSetColumn("6munknownAge", "6 or more months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(180.0, 0.0,"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("6m<15", "6 or more months of ARVs <15", Integer.class),
				getEnrolledByAgeAndGender(180.0, 0.0, 0, 14,"F"));
		femaleDateSet.addColumnValue(new DataSetColumn("6m15+", "6 or more months of ARVs 15+", Integer.class),
				getEnrolledByAgeAndGender(180.0, 0.0, 15, 200,"F"));

		set.addRow(femaleDateSet);

		DataSetRow maleDataSet = new DataSetRow();
		maleDataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender", Integer.class), "Male");

		maleDataSet.addColumnValue(
				new DataSetColumn("3munknownAge", "<3 months of ARVs (not MMD)  Unknown Age", Integer.class),
				getUnknownAgeByGender(0.0, 89.0,"M"));
		maleDataSet.addColumnValue(new DataSetColumn("3m<15", "<3 months of ARVs (not MMD) <15", Integer.class),
				getEnrolledByAgeAndGender(0.0, 89.0, 0, 14,"M"));
		maleDataSet.addColumnValue(new DataSetColumn("3m15+", "<3 months of ARVs (not MMD) 15+", Integer.class),
				getEnrolledByAgeAndGender(0.0, 89.0, 15, 200,"M"));

		maleDataSet.addColumnValue(new DataSetColumn("5munknownAge", "3-5 months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(90.0, 179.0,"M"));
		maleDataSet.addColumnValue(new DataSetColumn("5m<15", "3-5 months of ARVs <15", Integer.class),
				getEnrolledByAgeAndGender(90.0, 179.0, 0, 14,"M"));
		maleDataSet.addColumnValue(new DataSetColumn("5m15+", "3-5 months of ARVs 15+", Integer.class),
				getEnrolledByAgeAndGender(90.0, 179.0, 15, 200,"M"));

		maleDataSet.addColumnValue(
				new DataSetColumn("6munknownAge", "6 or more months of ARVs Unknown Age", Integer.class),
				getUnknownAgeByGender(180.0, 0.0,"M"));
		maleDataSet.addColumnValue(new DataSetColumn("6m<15", "6 or more months of ARVs <15", Integer.class),
				getEnrolledByAgeAndGender(180.0, 0.0, 0, 14,"M"));
		maleDataSet.addColumnValue(new DataSetColumn("6m15+", "6 or more months of ARVs 15+", Integer.class),
				getEnrolledByAgeAndGender(180.0, 0.0, 15, 200,"M"));

		set.addRow(maleDataSet);
		return set;
	}

	private int getUnknownAgeByGender(double minDispenseDay, double maxDispenseDay,String gender) {
		int count = 0;
		int _age =0;
        double dispenseValue;
		for (Person person : personList) {
            if(!gender.equals(person.getGender()))
                continue;

            _age = person.getAge(hdsd.getEndDate());

			if (_age == 0) {
                dispenseValue = (double) patientWithDispenseDay.get(person.getPersonId());
				if (dispenseValue <= maxDispenseDay && dispenseValue >= minDispenseDay) {
					count++;

				}
				if (minDispenseDay == 0.0 && dispenseValue >= maxDispenseDay) {
					count++;
				}
				if (maxDispenseDay == 0.0 && dispenseValue >= minDispenseDay) {
					count++;
				}
			}
		}
		// total = total + count;
		return count;
	}

	private int getEnrolledByAgeAndGender(double minDispenseDay, double maxDispenseDay ,int minAge, int maxAge,String gender) {
		int count = 0;
		int _age = 0;
        double dispenseValue = 0.0d;
		List<Integer> persoIntegers = new ArrayList<>();
		for (Person person : personList) {
			_age = person.getAge(hdsd.getEndDate());
			if (!gender.equals(person.getGender())||persoIntegers.contains(person.getPersonId()))
				continue;
			if (_age >= minAge && _age <= maxAge) {
				if (minDispenseDay > 0 && dispenseValue <= maxDispenseDay && dispenseValue >= minDispenseDay) {
					count++;

				} else if (minDispenseDay == 0 && maxDispenseDay == 89.0 && dispenseValue <= maxDispenseDay) {
					count++;

				} else if (maxDispenseDay == 0 && minDispenseDay == 180.0 && dispenseValue >= minDispenseDay) {
					count++;

				}
				persoIntegers.add(person.getPersonId());
			}

		}
		// total = total + count;
		// clearCountedPerson(persoIntegers);
		return count;
	}

	private void clearCountedPerson(List<Integer> personIds) {
		for (int pId : personIds) {
			obses.removeIf(p -> p.getPersonId().equals(pId));
		}
	}



}
