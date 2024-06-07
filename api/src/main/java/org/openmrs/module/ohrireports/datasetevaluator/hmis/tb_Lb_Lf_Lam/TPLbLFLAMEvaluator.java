package org.openmrs.module.ohrireports.datasetevaluator.hmis.tb_Lb_Lf_Lam;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import org.openmrs.module.ohrireports.api.impl.query.LBLFLAMQuery;
import org.openmrs.module.reporting.dataset.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class TPLbLFLAMEvaluator {
	
	@Autowired
	private LBLFLAMQuery lblflamQuery;
	
	public void buildDataset(Date start, Date end, SimpleDataSet dataset) {
		
		lblflamQuery.generateReport(start, end);
		int positiveCount = lblflamQuery.getByResult(POSITIVE).size();
		int negativeCount = lblflamQuery.getByResult(NEGATIVE).size();
		
		DataSetRow headerRow = buildColumn("TB_LB_LF-LAM",
		    "Total Number of tests performed using Lateral Flow Urine Lipoarabinomannan (LF-LAM) assay", positiveCount
		            + negativeCount);
		dataset.addRow(headerRow);
		dataset.addRow(buildColumn("TB_LB_LF-LAM. 1", "Positive", positiveCount));
		dataset.addRow(buildColumn("TB_LB_LF-LAM. 2", "Negative", negativeCount));
	}
	
	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow txCurrDataSetRow = new DataSetRow();
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), col_1_value);
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		txCurrDataSetRow.addColumnValue(new DataSetColumn("Number", "Number", Integer.class), col_3_value);
		
		return txCurrDataSetRow;
	}
}
