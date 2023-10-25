package org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt_cr_1;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import java.util.Calendar;
import java.util.Date;

import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.art_tpt_cr_1.HmisArtTptCrOneDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Handler(supports = { HmisArtTptCrOneDataSetDefinition.class })
public class HmisArtTptCrOneDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	private String baseName = "HIV_ART_TPT_CR.";
	private String COLUMN_3_NAME = "Number";
	
	private HmisArtTptCrOneDataSetDefinition hdsd;
		
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	List<Obs> obses = new ArrayList<>();
	Date prevTwelveMonth = new Date();
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (HmisArtTptCrOneDataSetDefinition) dataSetDefinition;
		context = evalContext;
		Calendar subTwelveMonth = Calendar.getInstance();
		subTwelveMonth.setTime(hdsd.getStartDate());
		subTwelveMonth.add(Calendar.MONTH, -6);
		prevTwelveMonth = subTwelveMonth.getTime();
	
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);	
				
        data.addRow(buildColumn("1","Number of ART patients who were initiated on any course of TPT 12 months before the reporting period",0));
		obses = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE,TB_PROPHYLAXIS_TYPE_INH);
        data.addRow(buildColumn("1.1","Patients on 6H 12 months prior to the reporting period"
		,obses.size()));
		data.addRow(buildColumn("1.1. 1","< 15 years, Male"
		,gettbscrnByAgeAndGender(0,15,Gender.Male)));
		data.addRow(buildColumn("1.1. 2","< 15 years, female"
		,gettbscrnByAgeAndGender(0,15,Gender.Female)));
		data.addRow(buildColumn("1.1. 3",">= 15 years, Male"
		,gettbscrnByAgeAndGender(15,150,Gender.Male)));
		data.addRow(buildColumn("1.1. 4",">= 15 years, female"
		,gettbscrnByAgeAndGender(15,150,Gender.Female)));

		
		obses = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE,TB_PROPHYLAXIS_TYPE_ALTERNATE_3HP);
        data.addRow(buildColumn("1.2","Patients on 3HP 12 months prior to the reporting period"
		,obses.size()));
		data.addRow(buildColumn("1.2. 1","< 15 years, Male"
		,gettbscrnByAgeAndGender(0,15,Gender.Male)));
		data.addRow(buildColumn("1.2 2","< 15 years, female"
		,gettbscrnByAgeAndGender(0,15,Gender.Female)));
		data.addRow(buildColumn("1.2. 3",">= 15 years, Male"
		,gettbscrnByAgeAndGender(15,150,Gender.Male)));
		data.addRow(buildColumn("1.2. 4",">= 15 years, female"
		,gettbscrnByAgeAndGender(15,150,Gender.Female)));

		obses = getTPTTreatmentBYType(TB_PROPHYLAXIS_TYPE_ALTERNATE,TB_PROPHYLAXIS_TYPE_ALTERNATE_3HR);
        data.addRow(buildColumn("1.3","Patients on 3HR 12 months prior to the reporting period"
		,obses.size()));
		data.addRow(buildColumn("1.3. 1","< 15 years, Male"
		,gettbscrnByAgeAndGender(0,15,Gender.Male)));
		data.addRow(buildColumn("1.3. 2","< 15 years, female"
		,gettbscrnByAgeAndGender(0,15,Gender.Female)));
		data.addRow(buildColumn("1.3. 3",">= 15 years, Male"
		,gettbscrnByAgeAndGender(15,150,Gender.Male)));
		data.addRow(buildColumn("1.3. 4",">= 15 years, female"
		,gettbscrnByAgeAndGender(15,150,Gender.Female)));

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
	
	public List<Obs> getTPTTreatmentBYType(String question,String answer){

		List<Integer> tbstarted = new ArrayList<>();
		List<Obs> tptTreatmList = new ArrayList<>();
		List<List<Integer>> tx_curr_patients = getTPTreatmentStartedDate();
		
		if (tx_curr_patients == null || tx_curr_patients.size()==0){
			return tptTreatmList;
		}
		List<Integer> patientsIds= tx_curr_patients.get(0);
		List<Integer> encounterIds=tx_curr_patients.get(1);
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
                .and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(question))
				.and().whereEqual("obs.valueCoded", conceptService.getConceptByUuid(answer)).
				 and().whereGreater("obs.obsDatetime", prevTwelveMonth).and().whereLess("obs.obsDatetime",hdsd.getStartDate()).and().whereIn("obs.encounter.encounterId", encounterIds).and().whereIdIn("obs.personId", patientsIds).orderDesc("obs.personId,obs.obsDatetime");

		for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
			if (!tbstarted.contains(obs.getPersonId())) {
				tbstarted.add(obs.getPersonId());
				tptTreatmList.add(obs);
		
			}
		}
		return tptTreatmList;
		
	}

	public List<List<Integer>> getTPTreatmentStartedDate(){
		List<Integer> tbstarted = new ArrayList<>();
		List<Integer> encounterIdList = new ArrayList<>();
		List<List<Integer>> listPatientandEncounter = new ArrayList<List<Integer>>();
		List<Integer> tx_curr_patients = getValidTratmentEndDatePatients();
		if (tx_curr_patients == null || tx_curr_patients.size()==0){
			return listPatientandEncounter;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs").whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and().whereEqual("obs.concept", conceptService.getConceptByUuid(TPT_START_DATE)).and().whereGreater("obs.valueDatetime", prevTwelveMonth).and().whereLess("obs.valueDatetime",hdsd.getStartDate()).and().whereIdIn("obs.personId", tx_curr_patients).orderDesc("obs.personId, obs.obsDatetime");
		for (Obs obs: evaluationService.evaluateToList(queryBuilder, Obs.class, context)){
			if (!tbstarted.contains(obs.getPersonId())){
				tbstarted.add(obs.getPersonId());
				// obstbstarted.add(obs);
			}
			if (!encounterIdList.contains(obs.getEncounter().getEncounterId())){
				encounterIdList.add(obs.getEncounter().getEncounterId());
			}
		}
		listPatientandEncounter.add(tbstarted);
		listPatientandEncounter.add(encounterIdList);
		return listPatientandEncounter;
	}
	
	
	private List<Integer> getValidTratmentEndDatePatients() {

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
        .whereGreater("obs.valueDatetime", prevTwelveMonth)
        .and()
        .whereLess("obs.obsDatetime", hdsd.getStartDate())
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
        .and().whereEqual("obs.concept", conceptService.getConceptByUuid(FOLLOW_UP_STATUS))
		.and().whereIn("obs.valueCoded", Arrays.asList(conceptService.getConceptByUuid(ALIVE),conceptService.getConceptByUuid(RESTART)))
		.and().whereGreater("obs.valueDatetime", prevTwelveMonth)
        .and().whereLess("obs.obsDatetime", hdsd.getStartDate());
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