package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_prev_numerator;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Calendar;
import java.util.Date;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev_numerator.TbPrevNumeratorARTByAgeAndSexDataSetDefinition;
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

@Handler(supports = { TbPrevNumeratorARTByAgeAndSexDataSetDefinition.class })
public class TbPrevNumeratorARTByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TbPrevNumeratorARTByAgeAndSexDataSetDefinition hdsd;
	private int total = 0;
	private int maletotal = 0;
	private int femaletotal = 0;
		
	Date prevSixMonth=new Date();
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	List<Obs> obses = new ArrayList<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TbPrevNumeratorARTByAgeAndSexDataSetDefinition) dataSetDefinition;
		context = evalContext;
		Calendar subSixMonth = Calendar.getInstance();
		subSixMonth.setTime(hdsd.getStartDate());
		subSixMonth.add(Calendar.MONTH, -6);
		prevSixMonth = subSixMonth.getTime();
		total=0;
		maletotal = 0;
		femaletotal = 0;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow newARTstarted = new DataSetRow();
		obses = getARTstarted("F");
		femaletotal+=obses.size();
		newARTstarted.addColumnValue(new DataSetColumn("", "", String.class),
        "Newly enrolled on ART");
		newARTstarted.addColumnValue(new DataSetColumn("funkownAge", "Female Unkown Age", Integer.class),
        getUnknownAgeByGender());
		newARTstarted.addColumnValue(new DataSetColumn("f<15", "Female <15", Integer.class),getEnrolledByAgeAndGender(0, 14));
		newARTstarted.addColumnValue(new DataSetColumn("f+15", "female +15", Integer.class),getEnrolledByAgeAndGender(15, 150));
		obses = getARTstarted("M");
		maletotal+=obses.size();
		newARTstarted.addColumnValue(new DataSetColumn("munkownAge", "Male Unkown Age", Integer.class),
        getUnknownAgeByGender());
		newARTstarted.addColumnValue(new DataSetColumn("m<15", "Male <15", Integer.class),getEnrolledByAgeAndGender(0, 14));
		newARTstarted.addColumnValue(new DataSetColumn("m+15", "Male +15", Integer.class),getEnrolledByAgeAndGender(15, 150));
		set.addRow(newARTstarted);

		DataSetRow oldARTstarted = new DataSetRow();
		obses = getPreviouslyOnART("F");
		femaletotal+=obses.size();
		oldARTstarted.addColumnValue(new DataSetColumn("", "", String.class),
        "Previously enrolled on ART");
		oldARTstarted.addColumnValue(new DataSetColumn("funkownAge", "Female Unkown Age", Integer.class),
        getUnknownAgeByGender());
		oldARTstarted.addColumnValue(new DataSetColumn("f<15", "Female <15", Integer.class),getEnrolledByAgeAndGender(0, 14));
		oldARTstarted.addColumnValue(new DataSetColumn("f+15", "female +15", Integer.class),getEnrolledByAgeAndGender(15, 150));

		obses = getPreviouslyOnART("M");
		maletotal+=obses.size();
		oldARTstarted.addColumnValue(new DataSetColumn("munkownAge", "Male Unkown Age", Integer.class),
        getUnknownAgeByGender());
		oldARTstarted.addColumnValue(new DataSetColumn("m<15", "Male <15", Integer.class),getEnrolledByAgeAndGender(0, 14));
		oldARTstarted.addColumnValue(new DataSetColumn("m+15", "Male +15", Integer.class),getEnrolledByAgeAndGender(15, 150));
		set.addRow(oldARTstarted);

        DataSetRow tSetRow = new DataSetRow();
		tSetRow.addColumnValue(new DataSetColumn("", "", Integer.class),
                "Sub-Total");
		tSetRow.addColumnValue(new DataSetColumn("f<15", "feSub-Total", Integer.class),
                femaletotal);
		tSetRow.addColumnValue(new DataSetColumn("m<15", "maleSub-Total", Integer.class),
                maletotal);
		tSetRow.addColumnValue(new DataSetColumn("total", "Total", Integer.class),
                total);
        
        set.addRow(tSetRow);
	
		return set;
	}
	private int getUnknownAgeByGender(){
        int count=0;
        for (Obs obs : obses) {   
                if ( Objects.isNull(obs.getPerson().getAge())|| obs.getPerson().getAge()==0) {
                count++;
            }
        }
        total = total + count;
        return count;
    }
	private int getEnrolledByAgeAndGender(int min, int max) {
        int count = 0;
        for (Obs obs : obses) {
            
            if (obs.getPerson().getAge() >= min && obs.getPerson().getAge() <= max) {
                count++;
            }

        }
        total = total + count;
       
        return count;
    }
	public List<Obs> getPreviouslyOnART(String gender){
		List<Integer> tbstarted = getTPTreatmentEndDateInLastSixMonths();
		List<Obs> obsARTstarted = new ArrayList<>();
		List<Integer> artstarted = new ArrayList<>();
		if (tbstarted==null || tbstarted.size() ==0){
			return obsARTstarted;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs").whereEqual("obs.concept", conceptService.getConceptByUuid(ART_START_DATE)).and().whereLess("obs.valueDatetime", prevSixMonth).and()
		.whereEqual("obs.person.gender", gender).and().whereIdIn("obs.personId", tbstarted).orderDesc("obs.personId, obs.obsDatetime");
		for (Obs obs: evaluationService.evaluateToList(queryBuilder, Obs.class, context)){
				if (!artstarted.contains(obs.getPersonId())){
					artstarted.add(obs.getPersonId());
					obsARTstarted.add(obs);

				}
		}
		return obsARTstarted;
	}
	
	public List<Obs> getARTstarted(String gender){
		List<Integer> tbstarted = getTPTreatmentEndDateInLastSixMonths();
		List<Obs> obsARTstarted = new ArrayList<>();
		List<Integer> artstarted = new ArrayList<>();
		if (tbstarted==null || tbstarted.size() ==0){
			return obsARTstarted;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs")
		.from(Obs.class,"obs")
		.whereEqual("obs.concept", conceptService.getConceptByUuid(ART_START_DATE))
		.and()
		.whereGreater("obs.valueDatetime", prevSixMonth)
		.and().whereLess("obs.valueDatetime", hdsd.getStartDate()).and()
		.whereEqual("obs.person.gender", gender)
		.and()
		.whereIdIn("obs.personId", tbstarted)
		.orderDesc("obs.personId, obs.obsDatetime");
		for (Obs obs: evaluationService.evaluateToList(queryBuilder, Obs.class, context)){
				if (!artstarted.contains(obs.getPersonId())){
					artstarted.add(obs.getPersonId());
					obsARTstarted.add(obs);

				}
		}
		return obsARTstarted;
	}

	public List<Integer> getTPTreatmentEndDateInLastSixMonths(){
		List<Integer> tbstarted = new ArrayList<>();
		List<Obs> obstbstarted = new ArrayList<>();
		List<Integer> tptstarteddate = getTPTreatmentStartedDateInLastSixMonths();
		if (tptstarteddate == null || tptstarteddate.size()==0){
			return tbstarted;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs").whereEqual("obs.concept", conceptService.getConceptByUuid(TPT_COMPLETED_DATE)).and().whereGreater("obs.valueDatetime", prevSixMonth).and().whereLess("obs.valueDatetime",hdsd.getStartDate()).and().whereIdIn("obs.personId", tptstarteddate).orderDesc("obs.personId, obs.obsDatetime");
		obstbstarted=evaluationService.evaluateToList(queryBuilder,Obs.class,context);
		for (Obs obs:obstbstarted){
			if (!tbstarted.contains(obs.getPersonId())){
				tbstarted.add(obs.getPersonId());
			}
		}
		return tbstarted;
	}
	
	
	public List<Integer> getTPTreatmentStartedDateInLastSixMonths(){
		List<Integer> tbstarted = new ArrayList<>();
		List<Obs> obstbstarted = new ArrayList<>();
		List<Integer> dispense = getDatimValidTreatmentEndDatePatients();
		if (dispense == null || dispense.size()==0){
			return tbstarted;
		}
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class,"obs").whereEqual("obs.concept", conceptService.getConceptByUuid(TPT_START_DATE)).and().whereGreater("obs.valueDatetime", prevSixMonth).and().whereLess("obs.valueDatetime",hdsd.getStartDate()).and().whereIdIn("obs.personId", dispense).orderDesc("obs.personId, obs.obsDatetime");
		obstbstarted=evaluationService.evaluateToList(queryBuilder,Obs.class,context);
		for (Obs obs:obstbstarted){
			if (!tbstarted.contains(obs.getPersonId())){
				tbstarted.add(obs.getPersonId());
			}
		}
		return tbstarted;
	}
	
	
	private List<Integer> getDatimValidTreatmentEndDatePatients() {

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
        .whereGreater("obs.valueDatetime", prevSixMonth)
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
				.and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(PATIENT_STATUS))
				.and()
				.whereIn("obs.valueCoded", Arrays.asList(conceptService.getConceptByUuid(ALIVE),conceptService.getConceptByUuid(RESTART)))
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
