package org.openmrs.module.ohrireports.reports.datasetevaluator.datim.tx_ml;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_ml.TxMlAutoCalculateDataSetDefinition;
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

@Handler(supports = { TxMlAutoCalculateDataSetDefinition.class })
public class TxMlAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TxMlAutoCalculateDataSetDefinition hdsd;
	
	private Concept artConcept;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxMlAutoCalculateDataSetDefinition) dataSetDefinition;
		context = evalContext;
		artConcept = conceptService.getConceptByUuid(ART_START_DATE);
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("adultAndChildrenEnrolled", "Numerator", Integer.class),
		    getDatimTxMlTreatmentStoped());
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
	private Integer getDatimTxMlTreatmentStoped(){
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
			
		return stopedPatients.size();
	}	private List<Integer> getDatimTxMlTreatmentEndDate() {

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
