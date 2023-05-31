package org.openmrs.module.ohrireports.reports.datasetevaluator.datim.tx_ml;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
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
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_ml.TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition;

@Handler(supports = { TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition.class })
public class TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition hdsd;
		
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	private int minCount = 0;
    private int maxCount = 4;
	List<Obs> obses = new ArrayList<>();
	Date prevThreeMonth=new Date();
	Date prevFiveMonth=new Date();
    HashMap<Integer, Date> patientTreatmentEndDate = new HashMap<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxMlInterruption3to5MonthsByAgeAndSexDataSetDefinition) dataSetDefinition;
		context = evalContext;
	
        SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
        patientTreatmentEndDate = new HashMap<>();
        DataSetRow femaleDateSet = new DataSetRow();
        obses = getStartedARTBetween3to5Months("F");
        femaleDateSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender",
                Integer.class), "Female");
        femaleDateSet.addColumnValue(new DataSetColumn("unkownAge", "Unkown Age", Integer.class),
        getUnknownAgeByGender());

        femaleDateSet.addColumnValue(new DataSetColumn("<1", "Below One (<1)", Integer.class),
        getEnrolledByAgeAndGender(0, 1));

        buildDataSet(femaleDateSet, "F");

        set.addRow(femaleDateSet);
        patientTreatmentEndDate = new HashMap<>();
        obses = getStartedARTBetween3to5Months("M");
        DataSetRow maleDataSet = new DataSetRow();
        maleDataSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender",
                Integer.class), "Male");
        maleDataSet.addColumnValue(new DataSetColumn("unkownAge", "Unkown Age", Integer.class),
        getUnknownAgeByGender());
        maleDataSet.addColumnValue(new DataSetColumn("<1", "Below One (<1)", Integer.class),
                getEnrolledByAgeAndGender(0, 1));

        buildDataSet(maleDataSet, "M");

        set.addRow(maleDataSet);
        return set;
	}

	private void buildDataSet(DataSetRow dataSet, String gender) {
        minCount = 1;
        maxCount = 4;
        while (minCount <= 65) {
            if (minCount == 65) {
                dataSet.addColumnValue(new DataSetColumn("65+", "65+", Integer.class),
                        getEnrolledByAgeAndGender(65, 200));
            } else {
                dataSet.addColumnValue(
                        new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                        getEnrolledByAgeAndGender(minCount, maxCount));
            }

            minCount=1+maxCount;
            maxCount = minCount + 4;
        }
    }
    private int getUnknownAgeByGender(){
        int count=0;
        for (Obs obs : obses) {   
            if ( Objects.isNull(obs.getPerson().getAge())|| obs.getPerson().getAge()==0) {
                count++;
            }
        }
        return count;
    }

    private int getEnrolledByAgeAndGender(int min, int max) {
        int count =0;
        for (Obs obs : obses) {         
            if (obs.getPerson().getAge() >= min && obs.getPerson().getAge() <= max) {
                count++;
            }
        }
        return count;
    }
	
	private List<Obs> getARTstartedDate(String gender) {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		List<Integer> patientsTreatmentStoped = getDatimTxMlTreatmentStoped();
        List<Integer> p = new ArrayList<>();
		List<Obs> between3and5months = new ArrayList<>();
		if (patientsTreatmentStoped == null || patientsTreatmentStoped.size() == 0)
			return between3and5months;
            queryBuilder.select("obs").from(Obs.class, "obs")
            .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType()).and()
            .whereEqual("obs.concept", conceptService.getConceptByUuid(ART_START_DATE)).and().whereIdIn("obs.personId", patientsTreatmentStoped).whereEqual("obs.person.gender", gender);
            
            for(Obs obs: evaluationService.evaluateToList(queryBuilder, Obs.class, context)){
                if (!p.contains(obs.getPersonId())){
                    between3and5months.add(obs);
                    p.add(obs.getPersonId()); 
                }
            }
		return between3and5months;
	}

    private List<Obs> getStartedARTBetween3to5Months(String gender){
        List<Obs> patientARTstartedDate = getARTstartedDate(gender);
        List<Obs> startedARTlessthan3Months = new ArrayList<>();
        if (patientARTstartedDate == null || patientARTstartedDate.size() == 0)
                return startedARTlessthan3Months;
        for (Obs obs : patientARTstartedDate){
            Date treatEnd=patientTreatmentEndDate.get(obs.getPersonId());
            if (treatEnd!=null){
            Calendar subFiveMonth = Calendar.getInstance();
            subFiveMonth.setTime(treatEnd);
            subFiveMonth.add(Calendar.MONTH, -5);
            prevFiveMonth = subFiveMonth.getTime();
            Calendar subThreeMonth = Calendar.getInstance();
            subThreeMonth.setTime(treatEnd);
            subThreeMonth.add(Calendar.MONTH, -3);
            prevThreeMonth = subThreeMonth.getTime();
            if(obs.getValueDatetime().before(prevThreeMonth) && obs.getValueDatetime().after(prevFiveMonth)){
                startedARTlessthan3Months.add(obs);
            } 
        }
        }
        return startedARTlessthan3Months;
    }
	
	
	private List<Integer> getDatimTxMlTreatmentStoped(){
		List<Integer> patientsTend = getDatimTxMlTreatmentEndDate();  
		List<Integer> patients = new ArrayList<>();
        if (patientsTend == null || patientsTend.size() == 0)
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
        .whereGreater("obs.obsDatetime", hdsd.getStartDate())
        .and()
		.whereLessOrEqualTo("obs.obsDatetime", hdsd.getEndDate()).and()
		.whereIdIn("obs.personId", patientsTend)
        
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
                if(!patients.contains(obs.getPersonId()))
                        {
                        patients.add(obs.getPersonId());
                        }
        }
		List<Integer> stopedPatients = new ArrayList<>();
		for (Integer tr : patientsTend){
			if (!patients.contains(tr)){
				stopedPatients.add(tr);        
			}
            else{
                patientTreatmentEndDate.remove(tr);
            }
		}
			
		return stopedPatients;
	}	
	
	private List<Integer> getDatimTxMlTreatmentEndDate() {

		List<Integer> patients = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs");
        queryBuilder.from(Obs.class, "obs")
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and()
        .whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
        .and()
        .whereGreater("obs.valueDatetime", hdsd.getStartDate())
        .and()
		.whereLessOrEqualTo("obs.valueDatetime", hdsd.getEndDate())
        
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
                if(!patients.contains(obs.getPersonId()))
                        {
                        patients.add(obs.getPersonId());
                        patientTreatmentEndDate.put(obs.getPersonId(),obs.getValueDatetime());
                        }
        }
				
		return patients;
	}

}
