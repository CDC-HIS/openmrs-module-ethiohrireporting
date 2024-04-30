package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.util.List;

public class LineListUtilities {
	
	public static DataSetRow buildEmptyRow(List<String> columns) {
		DataSetRow row = new DataSetRow();
		for (String column : columns) {
			row.addColumnValue(new DataSetColumn(column, column, String.class), "");
		}
		return row;
	}
}
