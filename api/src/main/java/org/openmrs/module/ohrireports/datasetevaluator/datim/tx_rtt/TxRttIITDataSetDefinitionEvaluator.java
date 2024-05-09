package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_rtt;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_rtt.TxRttIITDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

@Handler(supports = { TxRttIITDataSetDefinition.class })
public class TxRttIITDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private RTTQuery rttQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxRttIITDataSetDefinition _datasetDefinition = (TxRttIITDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		DataSetRow belowThreeDateSet = new DataSetRow();
		belowThreeDateSet.addColumnValue(new DataSetColumn("aggByIITrange", "", String.class),
		    "Experienced treatment interruption of <3 months before returning to treatment ");
		belowThreeDateSet.addColumnValue(new DataSetColumn("countedIIT", "", Integer.class), rttQuery
		        .getInterrupationByMonth(0, 3).size());
		set.addRow(belowThreeDateSet);
		
		DataSetRow betweenThreeandFiveDateSet = new DataSetRow();
		betweenThreeandFiveDateSet.addColumnValue(new DataSetColumn("aggByIITrange", "", String.class),
		    "Experienced treatment interruption of 3-5 months before returning to treatment");
		betweenThreeandFiveDateSet.addColumnValue(new DataSetColumn("countedIIT", "", Integer.class), rttQuery
		        .getInterrupationByMonth(3, 6).size());
		set.addRow(betweenThreeandFiveDateSet);
		
		DataSetRow abovesixDateSet = new DataSetRow();
		abovesixDateSet.addColumnValue(new DataSetColumn("aggByIITrange", "", String.class),
		    "Experienced treatment interruption of 6+ months before returning to treatment");
		abovesixDateSet.addColumnValue(new DataSetColumn("countedIIT", "", Integer.class),
		    rttQuery.getInterrupationByMonth(6, Integer.MAX_VALUE).size());
		set.addRow(abovesixDateSet);
		
		return set;
	}
	
}
