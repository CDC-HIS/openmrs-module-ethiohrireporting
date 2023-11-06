package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import java.util.HashMap;

import org.openmrs.Cohort;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.ColumnBuilder;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public class AggregateByPregnancyStatus extends ColumnBuilder {
	
	HmisCurrQuery hmisCurrQuery;
	
	SimpleDataSet data;
	
	public AggregateByPregnancyStatus(HmisCurrQuery hmisCurrQuery, SimpleDataSet data) {
		this.hmisCurrQuery = hmisCurrQuery;
		this.data = data;
		buildDatasetByPregnancyStatus();
	}
	
	private void buildDatasetByPregnancyStatus() {
		Cohort femaleCohort = hmisCurrQuery.getCohortByGender("F", hmisCurrQuery.getBaseCohort());
		
		HashMap<Integer, Object> pHashMap = hmisCurrQuery.getByPregnantStatus(femaleCohort);
		int pregnantCount = hmisCurrQuery.countByPregnancyStatus(pHashMap, "YES");
		int unPregnantCount = femaleCohort.size() - pregnantCount;
		data.addRow(buildColumn("HIV_TX_CURR_PREG", "Currently on ART by pregnancy status", femaleCohort.size()));
		data.addRow(buildColumn("HIV_TX_CURR_PREG.1", "Pregnant", pregnantCount));
		data.addRow(buildColumn("HIV_TX_CURR_PREG.2", "Non pregnant", unPregnantCount));
		
	}
}
