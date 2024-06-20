package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fp;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ORAL_CONTRACEPTIVE_PILL;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.INJECTABLE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.IMPLANTABLE_HORMONE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.INTRAUTERINE_DEVICE;

import java.util.Arrays;
import java.util.Collections;

import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HIVArtFpMetEvaluator {
	
	@Autowired
	private HivArtFpQuery fbQuery;
	
	public void buildDataset(SimpleDataSet dataSet) {
		
		int oralContraceptive = fbQuery
		        .getPatientByMethodOfOtherFP(Collections.singletonList(ORAL_CONTRACEPTIVE_PILL), true);
		int injectable = fbQuery.getPatientByMethodOfOtherFP(Collections.singletonList(INJECTABLE), true);
		int implants = fbQuery.getPatientByMethodOfOtherFP(Collections.singletonList(IMPLANTABLE_HORMONE), true);
		int iucd = fbQuery.getPatientByMethodOfOtherFP(Collections.singletonList(INTRAUTERINE_DEVICE), true);
		int others = fbQuery.getPatientByMethodOfOtherFP(
		    Arrays.asList(ORAL_CONTRACEPTIVE_PILL, INJECTABLE, IMPLANTABLE_HORMONE, INTRAUTERINE_DEVICE), false);
		
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FP_MET");
		row.addColumnValue(
		    new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    "Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by method");
		String column_3_name = "Number";
		row.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), oralContraceptive + injectable
		        + implants + iucd + others);
		dataSet.addRow(row);
		
		DataSetRow oralRow = new DataSetRow();
		oralRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FP_MET.1");
		oralRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Oral contraceptives");
		oralRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), oralContraceptive);
		dataSet.addRow(oralRow);
		
		DataSetRow injectableRow = new DataSetRow();
		injectableRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FP_MET.2");
		injectableRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Injectable");
		injectableRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), injectable);
		dataSet.addRow(injectableRow);
		
		DataSetRow implantRow = new DataSetRow();
		implantRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FP_MET.3");
		implantRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Implants");
		implantRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), implants);
		dataSet.addRow(implantRow);
		
		DataSetRow iucdRow = new DataSetRow();
		iucdRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FP_MET.4");
		iucdRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "IUCD");
		iucdRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), iucd);
		dataSet.addRow(iucdRow);
		
		DataSetRow othersRow = new DataSetRow();
		othersRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FP_MET.5");
		othersRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Others");
		othersRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), others);
		dataSet.addRow(othersRow);
		
	}
}
