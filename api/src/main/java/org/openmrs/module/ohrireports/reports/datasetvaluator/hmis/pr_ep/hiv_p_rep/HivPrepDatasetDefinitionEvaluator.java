package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.hiv_p_rep;


import java.util.HashSet;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_p_rep.HivPrepDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.HivPrEpQuery;
import org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.HmisPrepDatasetBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivPrepDatasetDefinition.class })
public class HivPrepDatasetDefinitionEvaluator implements DataSetEvaluator {
	@Autowired
	private HivPrEpQuery hivPrEpQuery;
	private Set<Integer> patientIds = new HashSet<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		HivPrepDatasetDefinition _Definition = (HivPrepDatasetDefinition)dataSetDefinition;
		hivPrEpQuery.initializeDate(_Definition.getStartDate(), _Definition.getEndDate());
		patientIds= hivPrEpQuery.getPatientsOnPrEp();
		
		new HmisPrepDatasetBuilder(dataSet, hivPrEpQuery.getPersons(new Cohort(patientIds)), "HIV_PrEP.1.1");

		return dataSet;
	}
	
}
