package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.hiv_p_rep_cat;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.SEXUAL_ASSAULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.OCCUPATIONAL;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NON_OCCUPATIONAL;

import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.COLUMN_2_NAME;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_p_r_ep_cat.HivPrEpCatagoriesDatasetDefinition;
import org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep.HivPrEpQuery;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivPrEpCatagoriesDatasetDefinition.class })
public class HivPrEpCatagoriesDatasetDefinitionEvaluators implements DataSetEvaluator {
	
	@Autowired
	private HivPrEpQuery query;
	
	private String column_3_name = "Tir 15";
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HivPrEpCatagoriesDatasetDefinition _DatasetDefinition = (HivPrEpCatagoriesDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		query.initializeDate(_DatasetDefinition.getStartDate(), _DatasetDefinition.getEndDate());
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
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP");
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    "Other Non-occupational");
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    nonOccupationCount);
		
		dataSet.addRow(otherNonOccupationPepRow);
		return dataSet;
	}
	
}
