package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_tb_denominator;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_tb_denominator.TxTbDenominatorSpecimenTypeDataSetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

@Handler(supports = { TxTbDenominatorSpecimenTypeDataSetDefinition.class })
public class TxTbDenominatorSpecimenTypeDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private TBQuery tbQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxTbDenominatorSpecimenTypeDataSetDefinition hdsd = (TxTbDenominatorSpecimenTypeDataSetDefinition) dataSetDefinition;
		
		Cohort xrayCohort = tbQuery.getTPTByConceptCohort(tbQuery.getTbScreeningEncounter(), tbQuery.getDenomiatorCohort(),
		    FollowUpConceptQuestions.OTHER_TB_DIAGNOSTIC_TEST, Arrays.asList(ConceptAnswer.CHEST_X_RAY));
		int screenOnlyCount = tbQuery.getDenomiatorCohort().size() - xrayCohort.size();
		DataSetRow row = new DataSetRow();
		int subTotal = 0;
		int molecularWRD = 0;
		row.addColumnValue(new DataSetColumn("", "", String.class),
		    "Number of patient who had a TB screening by Screening Type ");
		
		//TODO: calculate value
		row.addColumnValue(new DataSetColumn("Symptom Screen Only", "Symptom Screen Only ", Integer.class), screenOnlyCount);
		
		//TODO: calculate value
		row.addColumnValue(new DataSetColumn("Chest X-Ray", "Chest X-Ray ", Integer.class), xrayCohort.size());
		
		//TODO: calculate value
		row.addColumnValue(new DataSetColumn("Molecular WHO Recommended Diagnostic Test(mWRD)",
		        "Molecular WHO Recommended Diagnostic Test(mWRD)", Integer.class), molecularWRD);
		
		//TODO: calculate value
		subTotal += screenOnlyCount + xrayCohort.size() + molecularWRD;
		row.addColumnValue(new DataSetColumn("Sub total ", "Subtotal ", Integer.class), subTotal);
		
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		set.addRow(row);
		return set;
	}
	
}
