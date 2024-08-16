package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct_fo;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.FOQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_fo.PMTCTFOAutoCalculateDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { PMTCTFOAutoCalculateDataSetDefinition.class })
public class PMTCTFOAutoCalculateDataSetDefinisionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private PMTCTFOAutoCalculateDataSetDefinition _dataSetDefinition;
	
	@Autowired
	private FOQuery foQuery;
	
	private PMTCTFO pmtctfo;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		_dataSetDefinition = (PMTCTFOAutoCalculateDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		if (!_dataSetDefinition.getHeader()) {
			context = evalContext;
			
			DataSetRow dataSet = new DataSetRow();
			
			if (_dataSetDefinition.getDenominator()) {
				init();
				
				dataSet.addColumnValue(new DataSetColumn("Denominator", "Denominator", Integer.class), foQuery
				        .getBaseCohort().size());
			} else {
				dataSet.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), pmtctfo.getTotal());
			}
			set.addRow(dataSet);
			
		}
		return set;
	}
	
	private void init() {
		foQuery.setStartDate(_dataSetDefinition.getStartDate());
		foQuery.setEndDate(_dataSetDefinition.getEndDate());
		
		pmtctfo = new PMTCTFO(foQuery.getPMTCTByHivInfectedStatus(), foQuery.getPMTCTByHivUninfectedStatus(),
		        foQuery.getPMTCTByHivStatusUnknown(), foQuery.getPMTCTDiedWithoutStatusKnown());
	}
	
}
