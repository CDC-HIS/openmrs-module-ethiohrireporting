package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_new;

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TXNewQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_new.AutoCalculateDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { AutoCalculateDataSetDefinition.class })
public class AutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private AutoCalculateDataSetDefinition hdsd;
	
	@Autowired
	TXNewQuery txNewQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (AutoCalculateDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (!hdsd.getHeader()) {
			txNewQuery.generateReport(hdsd.getStartDate(), hdsd.getEndDate());
			
			DataSetRow dataSet = new DataSetRow();
			dataSet.addColumnValue(new DataSetColumn("adultAndChildrenEnrolled", "Numerator", Integer.class), txNewQuery
			        .getBaseCohort().size());
			
			set.addRow(dataSet);
		}
		return set;
	}
	
}
