package org.openmrs.module.ohrireports.datasetevaluator.hmis.pep;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.SEXUAL_ASSAULT;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.OCCUPATIONAL;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NON_OCCUPATIONAL;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.PepQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_p_r_ep_cat.HivPEPCategoryDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivPEPCategoryDatasetDefinition.class })
public class HivPEPCategoryDatasetDefinitionEvaluators implements DataSetEvaluator {
	
	@Autowired
	private PepQuery query;
	
	private String column_3_name = "Number";
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HivPEPCategoryDatasetDefinition _DatasetDefinition = (HivPEPCategoryDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		query.generateReport(_DatasetDefinition.getStartDate(), _DatasetDefinition.getEndDate());
		int total = 0;
		int occupationCount = query.getCountByExposureType(OCCUPATIONAL);
		int sexualAssaultCount = query.getCountByExposureType(SEXUAL_ASSAULT);
		int nonOccupationCount = query.getCountByExposureType(NON_OCCUPATIONAL);
		
		DataSetRow hivPepRow = new DataSetRow();
		hivPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP");
		hivPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    "Number of persons provided with post-exposure prophylaxis (PEP) for risk of HIV infection by exposure type");
		hivPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), total);
		
		dataSet.addRow(hivPepRow);
		
		DataSetRow occupationalPepRow = new DataSetRow();
		occupationalPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP.1");
		occupationalPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Occupation");
		occupationalPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), occupationCount);
		
		dataSet.addRow(occupationalPepRow);
		
		DataSetRow SexualAssaultPepRow = new DataSetRow();
		SexualAssaultPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP.2");
		SexualAssaultPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Sexual violence");
		SexualAssaultPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    sexualAssaultCount);
		
		dataSet.addRow(SexualAssaultPepRow);
		
		DataSetRow otherNonOccupationPepRow = new DataSetRow();
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP.3");
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    "Other Non-occupational");
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    nonOccupationCount);
		
		dataSet.addRow(otherNonOccupationPepRow);
		return dataSet;
	}
	
}
