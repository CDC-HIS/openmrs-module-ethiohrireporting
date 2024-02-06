package org.openmrs.module.ohrireports.datasetevaluator.linelist.monthlyVisit;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.MonthlyVisitDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.openmrs.module.ohrireports.helper.EthiopianDate;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { MonthlyVisitDatasetDefinition.class })
public class MonthlyVisitDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	MonthlyVisitQuery monthlyVisitQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		MonthlyVisitDatasetDefinition dsd = (MonthlyVisitDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dsd, evalContext);
		monthlyVisitQuery.generateReport(dsd.getStartDate(), dsd.getEndDate());
		
		HashMap<Integer, Object> mrnIdentifierHashMap = monthlyVisitQuery.getIdentifier(monthlyVisitQuery.getBaseCohort(),
		    MRN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> uaIdentifierHashMap = monthlyVisitQuery.getIdentifier(monthlyVisitQuery.getBaseCohort(),
		    UAN_PATIENT_IDENTIFIERS);
		HashMap<Integer, Object> artStartDictionary = monthlyVisitQuery.getArtStartDate(monthlyVisitQuery.getBaseCohort(),
		    null, dsd.getEndDate());
		HashMap<Integer, Object> regimentDictionary = monthlyVisitQuery.getRegiment(monthlyVisitQuery.getEncounter(),
		    monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    FOLLOW_UP_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> followUpStatus = monthlyVisitQuery.getFollowUpStatus(monthlyVisitQuery.getEncounter(),
		    monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> weight = monthlyVisitQuery.getByResult(WEIGHT, monthlyVisitQuery.getBaseCohort(),
		    monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> dose = monthlyVisitQuery.getByResult(TPT_DOSE_DAY_TYPE_INH, monthlyVisitQuery.getBaseCohort(),
		    monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> nextVisitDate = monthlyVisitQuery.getObsValueDate(monthlyVisitQuery.getEncounter(),
		    NEXT_VISIT_DATE, monthlyVisitQuery.getBaseCohort());
		HashMap<Integer, Object> tbScreening = monthlyVisitQuery.getByResult(TB_SCREENED, monthlyVisitQuery.getBaseCohort(),
		    monthlyVisitQuery.getEncounter());
		HashMap<Integer, Object> tbScreeningResult = monthlyVisitQuery.getByResult(TB_SCREENED_RESULT,
		    monthlyVisitQuery.getBaseCohort(), monthlyVisitQuery.getEncounter());
		
		DataSetRow row = new DataSetRow();
		List<Person> personList = monthlyVisitQuery.getPersons(monthlyVisitQuery.getBaseCohort());
		for (Person person : personList) {
			row = new DataSetRow();
			row.addColumnValue(new DataSetColumn("FullName", "FullName", String.class), person.getNames());
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uaIdentifierHashMap.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), person.getAge());
			row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
			row.addColumnValue(new DataSetColumn("Weight", "Weight", String.class), weight.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("Mobile", "Mobile", String.class), person.getActiveAttributes());
			row.addColumnValue(new DataSetColumn("ARTStartDate", "ARTStartDate", String.class),
			    artStartDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("followUpDate", "followUpDate", String.class),
			    followUpDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ARVRegiment", "ARVRegiment", String.class),
			    regimentDictionary.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("ARVDoseDays", "ARVDoseDays", String.class), dose.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("followUpStatus", "followUpStatus", String.class),
			    followUpStatus.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("nextVisitDate", "nextVisitDate", String.class),
			    nextVisitDate.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("artStartDateGC", "artStartDateGC", String.class),
			    monthlyVisitQuery.getEthiopianDate((Date) artStartDictionary.get(person.getPersonId())));
			row.addColumnValue(new DataSetColumn("TBScreening", "TBScreening", String.class),
			    tbScreening.get(person.getPersonId()));
			row.addColumnValue(new DataSetColumn("TBScreeningResult", "TB Screening Result", String.class),
			    tbScreeningResult.get(person.getPersonId()));
			dataSet.addRow(row);
		}
		
		return dataSet;
	}
}
