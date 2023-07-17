package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.tb_scrn;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.tb_scrn.HmisTbScrnDataSetDefinition;
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

@Handler(supports = { HmisTbScrnDataSetDefinition.class })
public class HmisTbScrnDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private HmisTbScrnDataSetDefinition hdsd;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	List<Obs> obses = new ArrayList<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (HmisTbScrnDataSetDefinition) dataSetDefinition;
		context = evalContext;
	
		MapDataSet data = new MapDataSet(dataSetDefinition, context);
				
        data.addData(new DataSetColumn("HIV_TB_SCRN","Proportion of patients enrolled in HIV care who were screened for TB (FD)",String.class)
		," ");
		obses = getARTstarted("a");
        data.addData(new DataSetColumn("HIV_TB_SCRN.1","Number of NEWLY Enrolled ART clients who were screened for TB during the reporting period",Integer.class)
		,obses.size());
		data.addData(new DataSetColumn("HIV_TB_SCRN.1.1","< 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN.1.2","< 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Female));
		data.addData(new DataSetColumn("HIV_TB_SCRN.1.3",">= 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN.1.4",">= 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Female));

		obses = getARTstarted("p");
        data.addData(new DataSetColumn("HIV_TB_SCRN_P","Screened Positive for TB",Integer.class)
		,obses.size());
		data.addData(new DataSetColumn("HIV_TB_SCRN_P.1","< 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN_P.2","< 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Female));
		data.addData(new DataSetColumn("HIV_TB_SCRN_P.3",">= 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN_P.4",">= 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Female));

		obses = getPreviouslyOnART("a");
        data.addData(new DataSetColumn("HIV_TB_SCRN_ART","Number of PLHIVs PREVIOUSLY on ART and screened for TB",Integer.class)
		,obses.size());
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART. 1","< 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART. 2","< 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Female));
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART. 3",">= 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART. 4",">= 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Female));

		obses = getPreviouslyOnART("p");
        data.addData(new DataSetColumn("HIV_TB_SCRN_ART_P","Screened Positive for TB",Integer.class)
		,obses.size());
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART_P. 1","< 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART_P. 2","< 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(0,15,Gender.Female));
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART_P. 3",">= 15 years, Male",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Male));
		data.addData(new DataSetColumn("HIV_TB_SCRN_ART_P. 4",">= 15 years, female",Integer.class)
		,gettbscrnByAgeAndGender(15,150,Gender.Female));

		return data;
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
	public List<Obs> getPreviouslyOnART(String result){
		List<Integer> tbstarted = new ArrayList<>();
		if (result == "p"){
			tbstarted = getTBscreenedPositive();
		}
		else{
			tbstarted = getTBscreenedInReportingPeriod();
		}
		List<Obs> obsARTstarted = new ArrayList<>();
		List<Integer> artstarted = new ArrayList<>();
		if (tbstarted==null || tbstarted.size() ==0){
			return obsARTstarted;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs") .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and().whereEqual("obs.concept", conceptService.getConceptByUuid(ART_START_DATE)).and().whereLess("obs.valueDatetime", hdsd.getStartDate()).and().whereIdIn("obs.personId", tbstarted).orderDesc("obs.personId, obs.obsDatetime");
		for (Obs obs: evaluationService.evaluateToList(queryBuilder, Obs.class, context)){
				if (!artstarted.contains(obs.getPersonId())){
					artstarted.add(obs.getPersonId());
					obsARTstarted.add(obs);

				}
		}
		return obsARTstarted;
	}
	public List<Obs> getARTstarted(String result){
		List<Integer> tbstarted = new ArrayList<>();
		if (result == "p"){
			tbstarted = getTBscreenedPositive();
		}
		else{
			tbstarted = getTBscreenedInReportingPeriod();
		}
		
		List<Obs> obsARTstarted = new ArrayList<>();
		List<Integer> artstarted = new ArrayList<>();
		if (tbstarted==null || tbstarted.size() ==0){
			return obsARTstarted;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs") .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and().whereEqual("obs.concept", conceptService.getConceptByUuid(ART_START_DATE)).and().whereGreater("obs.valueDatetime", hdsd.getStartDate()).and().whereLess("obs.valueDatetime", hdsd.getEndDate()).and().whereIdIn("obs.personId", tbstarted).orderDesc("obs.personId, obs.obsDatetime");
		for (Obs obs: evaluationService.evaluateToList(queryBuilder, Obs.class, context)){
				if (!artstarted.contains(obs.getPersonId())){
					artstarted.add(obs.getPersonId());
					obsARTstarted.add(obs);

				}
		}
		return obsARTstarted;
	}
	

	public List<Integer> getTBscreenedPositive(){
		List<Integer> tbscreened = new ArrayList<>();
		List<Obs> obstbstarted = new ArrayList<>();
		List<Integer> dispense = getTBscreenedInReportingPeriod();
		if (dispense == null || dispense.size()==0){
			return tbscreened;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs").whereEqual("obs.concept", conceptService.getConceptByUuid(TB_DIAGNOSTIC_TEST_RESULT)).and().whereEqual("obs.valueCoded", conceptService.getConceptByUuid(POSITIVE)).and().whereIdIn("obs.personId", dispense).orderDesc("obs.personId, obs.obsDatetime");
		obstbstarted=evaluationService.evaluateToList(queryBuilder,Obs.class,context);
		for (Obs obs:obstbstarted){
			if (!tbscreened.contains(obs.getPersonId())){
				tbscreened.add(obs.getPersonId());
			}
		}
		return tbscreened;
	}

	public List<Integer> getTBscreenedInReportingPeriod(){
		List<Integer> tbscreened = new ArrayList<>();
		List<Obs> obstbstarted = new ArrayList<>();
		List<Integer> dispense = getDispenseDose();
		if (dispense == null || dispense.size()==0){
			return tbscreened;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs").whereEqual("obs.concept", conceptService.getConceptByUuid(TB_SCREENING_DATE)).and().whereGreater("obs.valueDatetime", hdsd.getStartDate()).and().whereLess("obs.valueDatetime",hdsd.getEndDate()).and().whereIdIn("obs.personId", dispense).orderDesc("obs.personId, obs.obsDatetime");
		obstbstarted=evaluationService.evaluateToList(queryBuilder,Obs.class,context);
		for (Obs obs:obstbstarted){
			if (!tbscreened.contains(obs.getPersonId())){
				tbscreened.add(obs.getPersonId());
			}
		}
		return tbscreened;
	}

	public List<Integer> getDispenseDose() {
		List<Integer> pList = getValidTratmentEndDatePatients();
		List<Integer> patients = new ArrayList<>();
		if (pList == null || pList.size() == 0)
			return patients;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class, "obs").whereEqual("obs.concept", conceptService.getConceptByUuid(ARV_DISPENSED_IN_DAYS)).and().whereIn("obs.personId", pList).whereLess("obs.obsDatetime", hdsd.getEndDate()).orderDesc("obs.personId,obs.obsDatetime");		
		List<Obs> arvObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);
		for (Obs obs : arvObs) {
			if(!patients.contains(obs.getPersonId()))
				{
				patients.add(obs.getPersonId());
				}
		}
		return patients;	
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