package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_ml;

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
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_ml.TxMlTransferOutByAgeAndSexDataSetDefinition;
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

@Handler(supports = { TxMlTransferOutByAgeAndSexDataSetDefinition.class })
public class TxMlTransferOutByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TxMlTransferOutByAgeAndSexDataSetDefinition hdsd;
	
	private Concept artConcept;
	
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	private int minCount = 0;
    private int maxCount = 4;
	List<Obs> obses = new ArrayList<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxMlTransferOutByAgeAndSexDataSetDefinition) dataSetDefinition;
		context = evalContext;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);

        DataSetRow femaleDateSet = new DataSetRow();
        obses = getDatimTxMlTransferOut("F");
        femaleDateSet.addColumnValue(new DataSetColumn("FineByAgeAndSexData", "Gender",
                Integer.class), "Female");
        femaleDateSet.addColumnValue(new DataSetColumn("unkownAge", "Unkown Age", Integer.class),
        getUnknownAgeByGender());

        femaleDateSet.addColumnValue(new DataSetColumn("<1", "Below One (<1)", Integer.class),
        getEnrolledByAgeAndGender(0, 1));

        buildDataSet(femaleDateSet, "F");

        set.addRow(femaleDateSet);
        obses = getDatimTxMlTransferOut("M");
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
	
	private List<Obs> getDatimTxMlTransferOut(String gender){
		List<Integer> patientsTreatmentstoped = getDatimTxMlTreatmentStoped();
		List<Obs> deadObs = new ArrayList<>();
		if (patientsTreatmentstoped == null || patientsTreatmentstoped.size() == 0)
			return deadObs;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(PATIENT_STATUS))
				.and()
                .whereEqual("obs.person.gender", gender)
				.and()
				.whereEqual("obs.valueCoded", conceptService.getConceptByUuid(TRANSFER_OUT))
				.whereIdIn("obs.personId", patientsTreatmentstoped).and().orderDesc("obs.personId,obs.obsDatetime");

		List<Obs> liveObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

		for (Obs obs : liveObs) {
			if (!deadObs.contains(obs.getPersonId())) {
				deadObs.add(obs);

			}
		}

		return deadObs;

	}
	private List<Integer> getDatimTxMlTreatmentStoped(){
		List<Integer> patientsTreatmentEnd = getDatimTxMlTreatmentEndDate();
		List<Integer> patients = new ArrayList<>();
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
		.whereIdIn("obs.personId", patientsTreatmentEnd)
        
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
                if(!patients.contains(obs.getPersonId()))
                        {
                        patients.add(obs.getPersonId());
                        }
        }
		List<Integer> stopedPatients = new ArrayList<>();
		for (Integer tr : patientsTreatmentEnd){
			if (!patients.contains(tr)){
				stopedPatients.add(tr);
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
                        }
        }
				
		return patients;
	}

}