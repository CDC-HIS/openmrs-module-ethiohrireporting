package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.cxca_rx;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.cxca_rx.HmisCxCaRxDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.*;
@Handler(supports = { HmisCxCaRxDataSetDefinition.class })
public class HmisCxCaRxDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	private String baseName = "HIV_CXCA_RX. ";
	private String COLUMN_3_NAME = "Number";
	private HmisCxCaRxDataSetDefinition hdsd;
		
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	List<Obs> obses = new ArrayList<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (HmisCxCaRxDataSetDefinition) dataSetDefinition;
		context = evalContext;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);	
		data.addRow(buildColumn("","Treatment of precancerous cervical lesion",0));
		obses = getByScreenType(CXCA_TREATMENT_TYPE_CRYOTHERAPY);
		data.addRow(buildColumn("1", "Treatment with Cryotherapy",obses.size()));
		data.addRow(buildColumn("1. 1", "15 - 19 years",gettbscrnByAgeAndGender(15,19,Gender.Female)));

		data.addRow(buildColumn("1. 2","20 - 24 years", gettbscrnByAgeAndGender(20,24,Gender.Female)));
		data.addRow(buildColumn("1. 3","25 - 29 years", gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("1. 4","30 - 49 years", gettbscrnByAgeAndGender(30,49,Gender.Female)));
		data.addRow(buildColumn("1. 5",">= 50 years",gettbscrnByAgeAndGender(51,150,Gender.Female)));
		
		obses = getByScreenType(CXCA_TREATMENT_TYPE_LEEP);
        data.addRow(buildColumn("2","Treatment with LEEP",obses.size()));
		data.addRow(buildColumn("2. 1","15 - 19 years", gettbscrnByAgeAndGender(15,19,Gender.Female)));
		data.addRow(buildColumn("2. 2","20 - 24 years", gettbscrnByAgeAndGender(20,24,Gender.Female)));
		data.addRow(buildColumn("2. 3","25 - 29 years", gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("2. 4","30 - 49 years", gettbscrnByAgeAndGender(30,49,Gender.Female)));
		data.addRow(buildColumn("2. 5",">= 50 years", gettbscrnByAgeAndGender(51,150,Gender.Female)));

		obses = getByScreenType(CXCA_TREATMENT_TYPE_THERMOCOAGULATION);
        data.addRow(buildColumn("3","Treatment with Thermal Ablation/Thermocoagulation",obses.size()));
		data.addRow(buildColumn("3. 1","15 - 19 years",gettbscrnByAgeAndGender(15,19,Gender.Female)));
		data.addRow(buildColumn("3. 2","20 - 24 years",gettbscrnByAgeAndGender(20,24,Gender.Female)));
		data.addRow(buildColumn("3. 3","25 - 29 years",gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("3. 4","30 - 49 years", gettbscrnByAgeAndGender(30,49,Gender.Female)));
		data.addRow(buildColumn("3. 5",">= 50 years", gettbscrnByAgeAndGender(51,150,Gender.Female)));
	
		return data;
	}
	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow hivCxcarxDataSetRow = new DataSetRow();
		hivCxcarxDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		hivCxcarxDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		hivCxcarxDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
				col_3_value);
		
		return hivCxcarxDataSetRow;
	}
	private Integer gettbscrnByAgeAndGender(int minAge, int maxAge, Gender gender) {
        int _age = 0;
		List<Integer> patients = new ArrayList<>();
		String _gender=gender.equals(Gender.Female)?"f":"m";
		if (maxAge > 1){
				maxAge=maxAge+1;
			}
		for (Obs obs :obses) {
			_age =obs.getPerson().getAge();
			if (!patients.contains(obs.getPersonId()) 
			 && (_age>=minAge && _age< maxAge)
			 && (obs.getPerson().getGender().toLowerCase().equals(_gender))) {
				
				patients.add(obs.getPersonId());

			}
		}
		return patients.size();
    }
	
	private List<Obs> getByScreenType(String screenType) {
		List<Integer> patientsId = getCxCaFirstTimeStarted();
		List<Integer> patients = new ArrayList<>();
        List<Obs> localObs = new ArrayList<>();
        if (patientsId == null || patientsId.size() == 0)
                return localObs;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs").from(Obs.class, "obs")
		.whereEqual("obs.concept", conceptService.getConceptByUuid(CXCA_TREATMENT_STARTING_DATE)).and()
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType()).and()
        .whereEqual("obs.valueCoded", conceptService.getConceptByUuid(screenType)).and().whereIdIn("obs.personId", patientsId)
        .and().whereLess("obs.obsDatetime", hdsd.getEndDate());
        queryBuilder.orderDesc("obs.personId,obs.obsDatetime");

        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
			if(!patients.contains(obs.getPersonId()))
					{
					patients.add(obs.getPersonId());
                    localObs.add(obs);
					}
			}		
		return localObs;
		
	}

    private List<Integer> getCxCaFirstTimeStarted() {
        List<Integer> patientsId = getCxCAStartedPatients();
		List<Integer> patients = new ArrayList<>();
        if (patientsId == null || patientsId.size() == 0)
                return patients;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING))
				.and()
				.whereEqual("obs.valueCoded", conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING_FIRST_TIME)).and().whereIdIn("obs.personId", patientsId)
				.and().whereLess("obs.obsDatetime", hdsd.getEndDate());
		queryBuilder.orderDesc("obs.personId,obs.obsDatetime");

		List<Obs> liveObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

		for (Obs obs : liveObs) {
			if (!patients.contains(obs.getPersonId())) {
				patients.add(obs.getPersonId());
			}
		}

		return patients;
	}
 
    
    private List<Integer> getCxCAStartedPatients() {
		List<Integer> patientsId = getArtstartedPatients();
		List<Integer> patients = new ArrayList<>();
        if (patientsId == null || patientsId.size() == 0)
                return patients;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs").from(Obs.class, "obs")
		.whereEqual("obs.concept", conceptService.getConceptByUuid(CXCA_TREATMENT_STARTING_DATE)).and()
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType()).and()
		.whereGreaterOrEqualTo("obs.valueDatetime", hdsd.getStartDate()).and()
        .whereLessOrEqualTo("obs.valueDatetime", hdsd.getEndDate()).and()
        .whereIdIn("obs.personId", patientsId)
        .orderDesc("obs.obsDatetime");

        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
			if(!patients.contains(obs.getPersonId()))
					{
					patients.add(obs.getPersonId());
					}
			}		
		return patients;
		
	}

	private List<Integer> getArtstartedPatients() {
		List<Integer> patientsId = getDatimTxCurrTotalEnrolledPatients();
		List<Integer> patients = new ArrayList<>();
        if (patientsId == null || patientsId.size() == 0)
                return patients;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs").from(Obs.class, "obs")
		.whereEqual("obs.concept", conceptService.getConceptByUuid(ART_START_DATE)).and()
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType()).and()
        .whereLessOrEqualTo("obs.valueDatetime", hdsd.getEndDate()).and()
        .whereIdIn("obs.personId", patientsId)
        .orderDesc("obs.obsDatetime");

        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
			if(!patients.contains(obs.getPersonId()))
					{
					patients.add(obs.getPersonId());
					}
			}		
		return patients;
		
	}
	
	private List<Integer> getDatimTxCurrTotalEnrolledPatients() {

		List<Integer> patientsId = getListOfALiveORRestartPatientObservertions();
		List<Integer> patients = new ArrayList<>();
        if (patientsId == null || patientsId.size() == 0)
                return patients;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs");
        queryBuilder.from(Obs.class, "obs")
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and()
        .whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
        .and()
        .whereGreater("obs.valueDatetime", hdsd.getEndDate())
        .and()
        .whereLess("obs.obsDatetime", hdsd.getEndDate())
        .whereIdIn("obs.personId", patientsId)
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
                if(!patients.contains(obs.getPersonId()))
                        {
                        patients.add(obs.getPersonId());
                        }
        }
		// patients = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
				
		return patients;
	}
	
	private List<Integer> getListOfALiveORRestartPatientObservertions() {

		List<Integer> uniqiObs = new ArrayList<>();
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(FOLLOW_UP_STATUS))
				.and()
                .whereEqual("obs.person.gender", "F")
				.and()
				.whereIn("obs.valueCoded", Arrays.asList(conceptService.getConceptByUuid(ALIVE),conceptService.getConceptByUuid(RESTART)))
				.and().whereLess("obs.obsDatetime", hdsd.getEndDate());
		queryBuilder.orderDesc("obs.personId,obs.obsDatetime");

		List<Obs> liveObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

		for (Obs obs : liveObs) {
			if (!uniqiObs.contains(obs.getPersonId())) {
				uniqiObs.add(obs.getPersonId());
				// patientStatus.put(obs.getPersonId(), obs.getValueCoded());
			}
		}

		return uniqiObs;
	}

}
enum Gender {
	Female,
	Male
}