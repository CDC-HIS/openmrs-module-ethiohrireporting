package org.openmrs.module.ohrireports.reports.datasetevaluator.datim.tx_rtt;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import java.util.ArrayList;
import java.util.List;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_rtt.TxRttAutoCalculateDataSetDefinition;
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

@Handler(supports = { TxRttAutoCalculateDataSetDefinition.class })
public class TxRttAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TxRttAutoCalculateDataSetDefinition hdsd;
	
	private Concept artConcept;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxRttAutoCalculateDataSetDefinition) dataSetDefinition;
		context = evalContext;
		artConcept = conceptService.getConceptByUuid(ART_START_DATE);
		
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("adultAndChildrenEnrolled", "Numerator", Integer.class),
		getDatimTxRttTreatmentRestarted());
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
	private Integer getDatimTxRttTreatmentRestarted(){
		List<Integer> prevPatientsTreatmentEnd = getDatimTxrttPrevTreatmentEndDate();
		List<Integer> patients = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs");
        queryBuilder.from(Obs.class, "obs")
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and()
        .whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
		// .and()
        // .whereGreater("obs.valueDatetime", hdsd.getEndDate())
        .and()
        .whereGreater("obs.obsDatetime", hdsd.getStartDate())
        .and()
		.whereLessOrEqualTo("obs.obsDatetime", hdsd.getEndDate()).and()
		.whereIdIn("obs.personId", prevPatientsTreatmentEnd)
        
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
                if(!patients.contains(obs.getPersonId()))
                        {
                        patients.add(obs.getPersonId());
                        }
        }
			
		return patients.size();
	}	
	
	private List<Integer> getDatimTxrttPrevTreatmentEndDate() {

		List<Integer> patients = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs");
        queryBuilder.from(Obs.class, "obs")
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and()
        .whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
        .and()
        .whereLess("obs.valueDatetime", hdsd.getStartDate()).and().whereLess("obs.obsDatetime", hdsd.getStartDate())
        .and()        
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
