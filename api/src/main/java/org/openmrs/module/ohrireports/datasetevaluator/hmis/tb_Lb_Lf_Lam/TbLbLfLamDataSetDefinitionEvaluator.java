package org.openmrs.module.ohrireports.datasetevaluator.hmis.tb_Lb_Lf_Lam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openmrs.Obs;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.LBLFLAMQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.tb_Lb_Lf_Lam.TbLbLfLamDataSetDefinition;
import org.openmrs.module.reporting.dataset.*;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TbLbLfLamDataSetDefinition.class })
public class TbLbLfLamDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private LBLFLAMQuery lblflamQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TbLbLfLamDataSetDefinition _datasetDefinition = (TbLbLfLamDataSetDefinition) dataSetDefinition;
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		lblflamQuery.generateReport(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		int positiveCount = lblflamQuery.getByResult(POSITIVE).size();
		int negativeCount = lblflamQuery.getByResult(NEGATIVE).size();
		
		DataSetRow headerRow = buildColumn("TB_LB_LF-LAM",
		    "Total Number of tests performed using Lateral Flow Urine Lipoarabinomannan (LF-LAM) assay", positiveCount
		            + negativeCount);
		data.addRow(buildColumn("TB_LB_LF-LAM. 1", "Positive", positiveCount));
		data.addRow(buildColumn("TB_LB_LF-LAM. 2", "Negative", negativeCount));
		return data;
	}
	
	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow txCurrDataSetRow = new DataSetRow();
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), col_1_value);
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		txCurrDataSetRow.addColumnValue(new DataSetColumn("Number", "Number", Integer.class), col_3_value);
		
		return txCurrDataSetRow;
	}
}
