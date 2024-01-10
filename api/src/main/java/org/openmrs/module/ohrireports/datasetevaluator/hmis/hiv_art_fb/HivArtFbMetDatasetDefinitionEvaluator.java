package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fb;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ORAL_CONTRACEPTIVE_PILL;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.INJECTABLE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.IMPLANTABLE_HORMONE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.INTRAUTERINE_DEVICE;

import java.util.Arrays;
import java.util.Collections;

import org.omg.DynamicAny._DynAnyFactoryStub;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_art_fb.HivArtFbMetDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivArtFbMetDatasetDefinition.class })
public class HivArtFbMetDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private HivArtFbQuery fbQuery;
	
	private String column_3_name = "Number";
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HivArtFbMetDatasetDefinition _DatasetDefinition = (HivArtFbMetDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(_DatasetDefinition, evalContext);
		
		int oralContraceptive = fbQuery.getPatientByMethodOfOtherFP(Collections.singletonList(ORAL_CONTRACEPTIVE_PILL));
		int injectable = fbQuery.getPatientByMethodOfOtherFP(Collections.singletonList(INJECTABLE));
		int implants = fbQuery.getPatientByMethodOfOtherFP(Collections.singletonList(IMPLANTABLE_HORMONE));
		int iucd = fbQuery.getPatientByMethodOfOtherFP(Collections.singletonList(INTRAUTERINE_DEVICE));
		int others = fbQuery.getPatientByMethodOfOtherFP(Arrays.asList(ORAL_CONTRACEPTIVE_PILL, INJECTABLE,
		    IMPLANTABLE_HORMONE, INTRAUTERINE_DEVICE));
		
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FB_MET");
		row.addColumnValue(
		    new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    "Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by method");
		row.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), oralContraceptive + injectable
		        + implants + iucd + others);
		dataSet.addRow(row);
		
		DataSetRow oralRow = new DataSetRow();
		oralRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FB_MET.1");
		oralRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Oral contraceptives");
		oralRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), oralContraceptive);
		dataSet.addRow(oralRow);
		
		DataSetRow injectableRow = new DataSetRow();
		injectableRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FB_MET.2");
		injectableRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Injectable");
		injectableRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), injectable);
		dataSet.addRow(injectableRow);
		
		DataSetRow implantRow = new DataSetRow();
		implantRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FB_MET.3");
		implantRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Implants");
		implantRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), implants);
		dataSet.addRow(implantRow);
		
		DataSetRow iucdRow = new DataSetRow();
		iucdRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FB_MET.4");
		iucdRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "IUCD");
		iucdRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), iucd);
		dataSet.addRow(iucdRow);
		
		DataSetRow othersRow = new DataSetRow();
		othersRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_ART_FB_MET.5");
		othersRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Others");
		othersRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), others);
		dataSet.addRow(othersRow);
		
		return dataSet;
	}
}
