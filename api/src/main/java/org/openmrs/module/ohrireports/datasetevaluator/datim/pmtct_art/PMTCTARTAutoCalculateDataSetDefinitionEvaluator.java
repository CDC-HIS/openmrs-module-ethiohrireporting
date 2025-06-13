package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct_art;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.ARTQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_art.PMTCTARTAutoCalculateDataSetDefinition;
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

@Handler(supports = { PMTCTARTAutoCalculateDataSetDefinition.class })
public class PMTCTARTAutoCalculateDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private PMTCTARTAutoCalculateDataSetDefinition hdsd;
	
	@Autowired
	private ARTQuery artQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (PMTCTARTAutoCalculateDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(hdsd.getStartDate(), hdsd.getEndDate(), set);
		if (_dataSet != null)
			return _dataSet;
		
		if (!hdsd.getHeader()) {
			context = evalContext;
			
			artQuery.setStartDate(hdsd.getStartDate());
			artQuery.setEndDate(hdsd.getEndDate());
			
			DataSetRow dataSet = new DataSetRow();
			dataSet.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), artQuery.getPmtctARTCohort()
			        .size());
			set.addRow(dataSet);
		}
		return set;
	}
}
