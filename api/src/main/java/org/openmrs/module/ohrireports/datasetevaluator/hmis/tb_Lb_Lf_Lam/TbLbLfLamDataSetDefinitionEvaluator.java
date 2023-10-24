package org.openmrs.module.ohrireports.datasetevaluator.hmis.tb_Lb_Lf_Lam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.Obs;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.tb_Lb_Lf_Lam.TbLbLfLamDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TbLbLfLamDataSetDefinition.class })
public class TbLbLfLamDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	
	private EvaluationContext context;
	
	private TbLbLfLamDataSetDefinition hdsd;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TbLbLfLamDataSetDefinition) dataSetDefinition;
		context = evalContext;
		
		MapDataSet data = new MapDataSet(dataSetDefinition, context);
		
		data.addData(new DataSetColumn("TB_LB_LF-LAM",
		        "Total Number of tests performed using Lateral Flow Urine Lipoarabinomannan (LF-LAM) assay", String.class),
		    " ");
		data.addData(new DataSetColumn("TB_LB_LF-LAM. 1", "Positive", Integer.class), getLFLAM(POSITIVE));
		data.addData(new DataSetColumn("TB_LB_LF-LAM. 2", "Negative", Integer.class), getLFLAM(CYTOLOGY_NEGATIVE));
		
		return data;
	}
	
	public Integer getLFLAM(String result){
		List<Integer> mwrd = new ArrayList<>();
		List<Obs> obsmwrd = new ArrayList<>();
		List<Integer> specimenSents = getTBscreenedInReportingPeriod();
		if (specimenSents == null || specimenSents.size()==0){
			return mwrd.size();
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs").whereEqual("obs.concept", conceptService.getConceptByUuid(LF_LAM_RESULT)).and().whereEqual("obs.valueCoded", conceptService.getConceptByUuid(result)).and().whereIdIn("obs.personId", specimenSents).and().whereLess("obs.obsDatetime", hdsd.getEndDate()).orderDesc("obs.personId, obs.obsDatetime");
		obsmwrd=evaluationService.evaluateToList(queryBuilder,Obs.class,context);
		for (Obs obs:obsmwrd){
			if (!mwrd.contains(obs.getPersonId())){
				mwrd.add(obs.getPersonId());
			}
		}
		return mwrd.size();
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
	Female, Male
}
