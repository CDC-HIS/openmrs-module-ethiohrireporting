package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr.RegimentCategory.REGIMENT_TYPE;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
public class HMISTXCurrEvaluator {
	
	@Autowired
	private HmisCurrQuery hmisCurrQuery;
	
	private Date endDate;
	
	Cohort firstLineCohort;
	
	Cohort secondLineCohort;
	
	Cohort thirdLineCohort;
	
	public void buildDataSet(SimpleDataSet dataSetDefinition, Date date, List<Integer> aliveFollowUpEncounters) {
		this.endDate = date;
		hmisCurrQuery.loadInitialCohort(endDate, aliveFollowUpEncounters);
		
		setHeaderRow(dataSetDefinition);
		
		firstLineCohort = hmisCurrQuery.getByRegiment(RegimentCategory.getRegimentConcepts(REGIMENT_TYPE.FIRST_LINE),
		    hmisCurrQuery.getBaseCohort());
		secondLineCohort = hmisCurrQuery.getByRegiment(RegimentCategory.getRegimentConcepts(REGIMENT_TYPE.SECOND_LINE),
		    hmisCurrQuery.getBaseCohort());
		thirdLineCohort = hmisCurrQuery.getByRegiment(RegimentCategory.getRegimentConcepts(REGIMENT_TYPE.THIRD_LINE),
		    hmisCurrQuery.getBaseCohort());
		
		new AggregateByAgeAndGender(hmisCurrQuery, firstLineCohort, secondLineCohort, thirdLineCohort, dataSetDefinition);
		new AggregateByPregnancyStatus(hmisCurrQuery, dataSetDefinition);
		new AggregateByAgeAndRegiment(hmisCurrQuery, dataSetDefinition, firstLineCohort, secondLineCohort, thirdLineCohort);
		new AggregateByAgeGenderAndPregnancyStatus(hmisCurrQuery, dataSetDefinition, firstLineCohort, secondLineCohort,
		        thirdLineCohort, aliveFollowUpEncounters);
		
	}
	
	private void setHeaderRow(SimpleDataSet dataSetDefinition) {
		DataSetRow mainHeaderRow = new DataSetRow();
		mainHeaderRow.addColumnValue(new DataSetColumn("S.NO", "S.NO", String.class), "HIV_HIV_Treatment");
		mainHeaderRow.addColumnValue(new DataSetColumn("Activity", "Activity", String.class),
		    "Does health facility provide Monthly PMTCT / ART Treatment Service?");
		
		dataSetDefinition.addRow(mainHeaderRow);
		
		DataSetRow subHeaderRow = new DataSetRow();
		subHeaderRow.addColumnValue(new DataSetColumn("S.NO", "S.NO", String.class), "HIV_TX_CURR_ALL");
		subHeaderRow.addColumnValue(new DataSetColumn("Activity", "Activity", String.class),
		    "Number of adults and children who are currently on ART by age, sex and regimen category");
		subHeaderRow.addColumnValue(new DataSetColumn("Number", "Number", Integer.class), hmisCurrQuery.getBaseCohort()
		        .size());
		
		dataSetDefinition.addRow(subHeaderRow);
		
	}
	
}
