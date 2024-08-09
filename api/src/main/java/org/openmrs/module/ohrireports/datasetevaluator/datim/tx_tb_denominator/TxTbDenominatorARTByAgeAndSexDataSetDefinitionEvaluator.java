package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorARTByAgeAndSexDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

@Handler(supports = { TxTbDenominatorARTByAgeAndSexDataSetDefinition.class })
public class TxTbDenominatorARTByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private TBQuery tbQuery;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	private HashMap<Integer, Object> followUpdate;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxTbDenominatorARTByAgeAndSexDataSetDefinition hdsd = (TxTbDenominatorARTByAgeAndSexDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		Cohort newOnArtCohort = tbQuery.getNewOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(),
		    tbQuery.getDenomiatorCohort(), tbQuery.getTbScreeningEncounter());
		
		Cohort alreadyOnArtCohort = getAlreadyOnARTCohort(newOnArtCohort, tbQuery.getDenomiatorCohort());
		
		followUpdate = tbQuery.getObValue(FollowUpConceptQuestions.FOLLOW_UP_DATE, alreadyOnArtCohort,
		    PatientQueryImpDao.ObsValueType.DATE_VALUE, tbQuery.getTbScreeningEncounter());
		
		followUpdate.putAll(tbQuery.getObValue(FollowUpConceptQuestions.FOLLOW_UP_DATE, newOnArtCohort,
		    PatientQueryImpDao.ObsValueType.DATE_VALUE, tbQuery.getTbScreeningEncounter()));
		
		_AggregateBuilder.setFollowUpDate(followUpdate);
		_AggregateBuilder.setLowerBoundAge(0);
		_AggregateBuilder.setUpperBoundAge(65);
		buildRowWithAggregate(set, newOnArtCohort, "New On ART");
		
		buildRowWithAggregate(set, alreadyOnArtCohort, "Already On ART");
		
		return set;
	}
	
	private void buildRowWithAggregate(SimpleDataSet set, Cohort cohort, String type) {
		Cohort femalePositiveCohort = tbQuery.getCohortByTbScreenedPositive(cohort, "F");
		
		Cohort femaleNegativeCohort = tbQuery.getCohortByTbScreenedNegative(cohort, "F");
		
		Cohort malePositiveCohort = tbQuery.getCohortByTbScreenedPositive(cohort, "M");
		
		Cohort maleNegativeCohort = tbQuery.getCohortByTbScreenedNegative(cohort, "M");
		
		DataSetRow positiveDescriptionDsRow = new DataSetRow();
		positiveDescriptionDsRow.addColumnValue(new DataSetColumn("", "Category", String.class), type + "/Screen Positive ");
		set.addRow(positiveDescriptionDsRow);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(femalePositiveCohort));
		DataSetRow _femalePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumnWithFollowUpDate(_femalePositive, "F");
		set.addRow(_femalePositive);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(malePositiveCohort));
		DataSetRow _malePositive = new DataSetRow();
		_AggregateBuilder.buildDataSetColumnWithFollowUpDate(_malePositive, "M");
		set.addRow(_malePositive);
		
		DataSetRow negativeDescriptionDsRow = new DataSetRow();
		negativeDescriptionDsRow.addColumnValue(new DataSetColumn("", "Category", String.class), type + "/Screen Negative ");
		set.addRow(negativeDescriptionDsRow);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(femaleNegativeCohort));
		DataSetRow _femaleNegative = new DataSetRow();
		_AggregateBuilder.buildDataSetColumnWithFollowUpDate(_femaleNegative, "F");
		set.addRow(_femaleNegative);
		
		_AggregateBuilder.setPersonList(tbQuery.getPersons(maleNegativeCohort));
		DataSetRow _maleNegative = new DataSetRow();
		_AggregateBuilder.buildDataSetColumnWithFollowUpDate(_maleNegative, "M");
		set.addRow(_maleNegative);
	}
	
	private Cohort getAlreadyOnARTCohort(Cohort newCohort, Cohort allCohort) {
		Cohort alreadyOnArtCohort = new Cohort();
		for (CohortMembership membership : allCohort.getMemberships()) {
			if (!newCohort.contains(membership.getPatientId())) {
				alreadyOnArtCohort.addMembership(membership);
			}
		}
		return alreadyOnArtCohort;
	}
}
