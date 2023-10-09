package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_prev_denominator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev_denominator.TbPrevDominatorDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TbPrevDominatorDatasetDefinition.class })
public class TbPrevDenominatorDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TbPrevDominatorDatasetDefinition hdsd;
	
	@Autowired
	private TbPrevQuery tbPrevQuery;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TbPrevDominatorDatasetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		tbPrevQuery.instantiate(hdsd.getStartDate(), hdsd.getEndDate());
		
		if (!hdsd.getAggregateType()) {
			
			DataSetRow dataSet = new DataSetRow();
			dataSet.addColumnValue(new DataSetColumn("AutoCalculate", "Auto-Calculate", String.class), "Denominator");
			dataSet.addColumnValue(new DataSetColumn("description",
			        "Number of ART patients who were initiated on any course of TPT during the previous reporting period",
			        Integer.class), tbPrevQuery.getOnTbPrevPatientId().size());
			set.addRow(dataSet);
			
		} else {
			
			// Disaggregated By ART Start by Age/Sex
			DataSetRow dataSetRow = new DataSetRow();
			dataSetRow.addColumnValue(new DataSetColumn("type", "Status", String.class), "Newly enrolled on ART");
			set.addRow(dataSetRow);
			_AggregateBuilder.buildDataSetColumn(set, "F", 15);
			_AggregateBuilder.setPersonList(tbPrevQuery.getOnArtNewAndOnTbPrevPatient("F"));
			_AggregateBuilder.buildDataSetColumn(set, "M", 15);
			_AggregateBuilder.setPersonList(tbPrevQuery.getOnArtNewAndOnTbPrevPatient("M"));
			
			DataSetRow dataSetRowPrev = new DataSetRow();
			dataSetRowPrev.addColumnValue(new DataSetColumn("type", "Status", String.class), "Perviously enrolled on ART");
			set.addRow(dataSetRowPrev);
			_AggregateBuilder.buildDataSetColumn(set, "F", 15);
			_AggregateBuilder.setPersonList(tbPrevQuery.getOnArtOldAndOnTbPrevPatient("F"));
			_AggregateBuilder.buildDataSetColumn(set, "M", 15);
			_AggregateBuilder.setPersonList(tbPrevQuery.getOnArtOldAndOnTbPrevPatient("M"));
		}
		
		return set;
	}
	
}
