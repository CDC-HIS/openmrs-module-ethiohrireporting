package org.openmrs.module.ohrireports.datasetevaluator.hmis;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

public abstract class ColumnBuilder {
	
	private final String COLUMN_3_NAME = "Number";
	
	protected DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow txCurrDataSetRow = new DataSetRow();
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), col_1_value);
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class), col_3_value);
		
		return txCurrDataSetRow;
	}
	
	protected DataSetRow buildColumn(String col_1_value, String col_2_value, String col_3_value) {
		DataSetRow txCurrDataSetRow = new DataSetRow();
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), col_1_value);
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class), col_3_value);
		
		return txCurrDataSetRow;
	}
	
	protected DataSetRow buildColumnForTitle(String col_1_value, String col_2_value) {
		DataSetRow txCurrDataSetRow = new DataSetRow();
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), col_1_value);
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		return txCurrDataSetRow;
	}
	
}
