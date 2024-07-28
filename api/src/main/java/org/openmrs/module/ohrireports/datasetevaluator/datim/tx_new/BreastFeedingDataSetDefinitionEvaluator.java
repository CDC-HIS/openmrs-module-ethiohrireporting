package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.CURRENTLY_BREAST_FEEDING_CHILD;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.YES;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.BreastFeedingStatusDataSetDefinition;
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

@Handler(supports = { BreastFeedingStatusDataSetDefinition.class })
public class BreastFeedingDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private BreastFeedingStatusDataSetDefinition hdsd;
	
	private Concept breastFeeding, breastFeedingYes;
	
	@Autowired
	private ConceptService conceptService;
	
	private PatientQueryService patientQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private EvaluationService evaluationService;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (BreastFeedingStatusDataSetDefinition) dataSetDefinition;
		context = evalContext;
		patientQuery = Context.getService(PatientQueryService.class);
		breastFeeding = conceptService.getConceptByUuid(CURRENTLY_BREAST_FEEDING_CHILD);
		breastFeedingYes = conceptService.getConceptByUuid(YES);
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("breastFeeding", "BreastFeeding", Integer.class),
		    getNumberOfEnrolledBreastFeeding());
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		set.addRow(dataSet);
		return set;
	}
	
	public int getNumberOfEnrolledBreastFeeding() {
		List<Integer> encounter = encounterQuery.getAliveFollowUpEncounters(hdsd.getStartDate(), hdsd.getEndDate());
		
		Cohort pList = patientQuery.getNewOnArtCohort("F", hdsd.getStartDate(), hdsd.getEndDate(), null, encounter);
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("distinct obs.personId").from(Obs.class, "obs").whereEqual("obs.concept", breastFeeding).and()
		        .whereEqual("obs.valueCoded", breastFeedingYes).and().whereIn("obs.personId", pList.getMemberIds());
		List<Integer> personIDs = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
		return personIDs.size();
		
	}
}
