package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_intr;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_intr.MLHmisQuery.Range;

import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HIVARTIntrEvaluator {
	
	private Date end;
	
	@Autowired
	private MLHmisQuery hivArtIntrQuery;
	
	public DataSet buildDataset(Date start, Date end, SimpleDataSet dataSet) {
		this.end = end;
		hivArtIntrQuery.loadInterruptedCohort(start, end);
		buildDataSet(dataSet);
		
		return dataSet;
	}
	
	public void buildDataSet(SimpleDataSet dataSet) {
		
		// lost to follow-up Lost after treatment < 3month
		Cohort cohort = hivArtIntrQuery.getBelowThreeMonthInterruption(hivArtIntrQuery.getLostToFollowUp(end));
		List<Person> personList = hivArtIntrQuery.getPerson(cohort);
		int total = cohort.size();
		dataSet.addRow(buildColumn("HIV_ART_INTR", "Number of ART Clients that interrupted Treatment", ""));
		dataSet.addRow(buildColumn("HIV_ART_INTR_OUT", "Number of ART Clients Interrupted treatment by outcome", ""));
		
		int headerIndex = dataSet.getRows().size();
		
		dataSet.addRow(buildRow(".1", "Lost after treatment < 3month", cohort.size()));
		
		dataSet.addRow(buildRow(".1. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".1. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildRow(".1. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".1. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		// lost to follow-up Lost after treatemnt > 3month
		cohort = hivArtIntrQuery.getAboveThreeMonthInterruption(hivArtIntrQuery.getLostToFollowUp(end));
		personList = hivArtIntrQuery.getPerson(cohort);
		total += cohort.size();
		
		dataSet.addRow(buildRow(".2", "Lost after treatement > 3month", cohort.size()));
		
		dataSet.addRow(buildRow(".2. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".2. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildRow(".2. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".2. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
		// Transferred out
		cohort = hivArtIntrQuery.getTransferredOut(hivArtIntrQuery.getBaseCohort());
		personList = hivArtIntrQuery.getPerson(cohort);
		total += cohort.size();
		
		dataSet.addRow(buildRow(".3", "Transferred out", cohort.size()));
		
		dataSet.addRow(buildRow(".3. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".3. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildRow(".3. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".3. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
		// Refused (stopped) treatment
		cohort = hivArtIntrQuery.getRefusedOrStopped(hivArtIntrQuery.getBaseCohort());
		personList = hivArtIntrQuery.getPerson(cohort);
		total += cohort.size();
		
		dataSet.addRow(buildRow(".4", "Refused (stopped) treatment", cohort.size()));
		
		dataSet.addRow(buildRow(".4. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".4. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildRow(".4. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".4. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
		// Died
		cohort = hivArtIntrQuery.getDead(hivArtIntrQuery.getBaseCohort());
		personList = hivArtIntrQuery.getPerson(cohort);
		total += cohort.size();
		
		dataSet.addRow(buildRow(".5", "Died", cohort.size()));
		
		dataSet.addRow(buildRow(".5. 1", "< 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".5. 2", "< 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.LESS_THAN_FIFTY, "F", personList)));
		
		dataSet.addRow(buildRow(".5. 3", ">= 15 years, Male",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "M", personList)));
		
		dataSet.addRow(buildRow(".5. 4", ">= 15 years, Female",
		    hivArtIntrQuery.getByAgeAndGender(Range.ABOVE_OR_EQUAL_TO_FIFTY, "F", personList)));
		
		//updating header total
		dataSet.addRow(headerIndex - 1,
		    buildColumn("HIV_ART_INTR", "Number of ART Clients that interrupted Treatment", total));
		dataSet.addRow(headerIndex,
		    buildColumn("HIV_ART_INTR_OUT", "Number of ART Clients Interrupted treatment by outcome", total));
		
	}
	
	private DataSetRow buildRow(String col_1_value, String col_2_value, Integer value) {
		
		String baseName = "HIV_ART_INTR_OUT ";
		col_1_value = baseName + col_1_value;
		
		return buildColumn(col_1_value, col_2_value, value);
	}
	
	private DataSetRow buildColumn(String col_1_value, String col_2_value, Object value) {
		DataSetRow hivTxNewDataSetRow = new DataSetRow();
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), col_1_value);
		
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		String column_3_name = "Number";
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), value);
		
		return hivTxNewDataSetRow;
	}
	
}
