package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_intr;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_art_intr.HivArtIntrDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_intr.MLHmisQuery.Range;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HivArtIntrDatasetDefinition.class })
public class HivArtIntrDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private HivArtIntrDatasetDefinition _datasetDefinition;
	
	private String baseName = "HIV_ART_INTR_OUT ";
	
	private String column_3_name = "Number";
	
	@Autowired
	private MLHmisQuery hivArtIntrQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		_datasetDefinition = (HivArtIntrDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		hivArtIntrQuery.loadInterruptedCohort(_datasetDefinition.getStartDate(), _datasetDefinition.getEndDate());
		buildDataSet(dataSet);
		
		return dataSet;
	}
	
	public void buildDataSet(SimpleDataSet dataSet) {
		
		// lost to follow-up Lost after treatemnt < 3month
		Cohort cohort = hivArtIntrQuery.getBelowThreeMonthInterruption(hivArtIntrQuery.getLostToFollowUp(_datasetDefinition
		        .getEndDate()));
		List<Person> personList = hivArtIntrQuery.getPerson(cohort);
		
		dataSet.addRow(buildColumn(".1", "Lost after treatment < 3month", cohort.getSize()));
		
		dataSet.addRow(buildColumn(".1. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".1. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildColumn(".1. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".1. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		// lost to follow-up Lost after treatemnt > 3month
		cohort = hivArtIntrQuery.getAboveThreeMonthInterruption(hivArtIntrQuery.getLostToFollowUp(_datasetDefinition
		        .getEndDate()));
		personList = hivArtIntrQuery.getPerson(cohort);
		
		dataSet.addRow(buildColumn(".2", "Lost after treatement > 3month", cohort.getSize()));
		
		dataSet.addRow(buildColumn(".2. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".2. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildColumn(".2. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".2. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
		// Transferred out
		cohort = hivArtIntrQuery.getTransferredOut(hivArtIntrQuery.getBaseCohort());
		personList = hivArtIntrQuery.getPerson(cohort);
		dataSet.addRow(buildColumn(".3", "Transferred out", cohort.getSize()));
		
		dataSet.addRow(buildColumn(".3. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".3. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildColumn(".3. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".3. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
		// Refused (stopped) treatment
		cohort = hivArtIntrQuery.getRefusedOrStopped(hivArtIntrQuery.getBaseCohort());
		personList = hivArtIntrQuery.getPerson(cohort);
		dataSet.addRow(buildColumn(".4", "Refused (stopped) treatment", cohort.getSize()));
		
		dataSet.addRow(buildColumn(".4. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".4. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildColumn(".4. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".4. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
		// Died
		cohort = hivArtIntrQuery.getDead(hivArtIntrQuery.getBaseCohort());
		personList = hivArtIntrQuery.getPerson(cohort);
		dataSet.addRow(buildColumn(".5", "Died", cohort.getSize()));
		
		dataSet.addRow(buildColumn(".5. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".5. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildColumn(".5. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildColumn(".5. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
	}
	
	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer value) {
		DataSetRow hivTxNewDataSetRow = new DataSetRow();
		
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), baseName + ""
		        + col_1_value);
		
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), value);
		
		return hivTxNewDataSetRow;
	}
	
}
