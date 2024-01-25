package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct.eid;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.EidNumeratorDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { EidNumeratorDatasetDefinition.class })
public class EidNumeratorDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	EIDQuery eidQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		EidNumeratorDatasetDefinition _datasetDefinition = (EidNumeratorDatasetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		
		eidQuery.generateReportForDATIM(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), eidQuery.getBaseCohort().size());
		
		data.addRow(row);
		
		return null;
	}
}
