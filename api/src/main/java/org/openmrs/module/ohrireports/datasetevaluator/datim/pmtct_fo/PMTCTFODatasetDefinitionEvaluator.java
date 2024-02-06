package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct_fo;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.FOQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_fo.PMTCTFODataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { PMTCTFODataSetDefinition.class })
public class PMTCTFODatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private PMTCTFODataSetDefinition pmtctfoDataSetDefinition;
	
	@Autowired
	private AggregateBuilder aggregateBuilder;
	
	@Autowired
	private FOQuery foQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		pmtctfoDataSetDefinition = (PMTCTFODataSetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow pmtctOutcomeRow = new DataSetRow();
		buildDataSetColumn(pmtctOutcomeRow);
		dataSet.addRow(pmtctOutcomeRow);
		
		return dataSet;
		
	}
	
	public void buildDataSetColumn(DataSetRow dataSet) {
		dataSet.addColumnValue(new DataSetColumn("HIVInfected", "HIV-infected", String.class),
		    foQuery.getPmtctByHivInfectedStatus());
		dataSet.addColumnValue(new DataSetColumn("HIVUninfected", "HIV-uninfected", Integer.class),
		    foQuery.getPmtctByHivUninfectedStatus());
		dataSet.addColumnValue(new DataSetColumn("HIVFinalStatusUnknown", "HIV-final status unknown", Integer.class),
		    foQuery.getPmtctByHivStatusUnknown());
		dataSet.addColumnValue(new DataSetColumn("DiedWithoutStatusKnown", "Died without status known", Integer.class),
		    foQuery.getPmtctDiedWithoutStatusKnown());
	}
}
