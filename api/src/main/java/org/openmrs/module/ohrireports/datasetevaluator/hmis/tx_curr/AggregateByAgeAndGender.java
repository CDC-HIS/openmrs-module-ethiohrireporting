package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import org.openmrs.Cohort;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.ColumnBuilder;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public class AggregateByAgeAndGender extends ColumnBuilder {
	
	HmisCurrQuery hmisCurrQuery;
	
	Cohort firstLineCohort, secondLineCohort, thirdLineCohort;
	
	SimpleDataSet data;
	
	public AggregateByAgeAndGender(HmisCurrQuery hmisCurrQuery, Cohort firstLineCohort, Cohort secondLineCohort,
	    Cohort thirdLineCohort, SimpleDataSet data) {
		this.hmisCurrQuery = hmisCurrQuery;
		this.firstLineCohort = firstLineCohort;
		this.secondLineCohort = secondLineCohort;
		this.thirdLineCohort = thirdLineCohort;
		this.data = data;
		buildAggregateByAgeAndGenderDataset();
	}
	
	private void buildAggregateByAgeAndGenderDataset() {
		int minAge = 1;
		int maxAge = 4;
		int interval = 4;
		String name = "HIV_TX_CURR_U";
		
		Cohort baseCohort = hmisCurrQuery.getLessThanAge(15, hmisCurrQuery.getBaseCohort());
		data.addRow(buildColumn("HIV_TX_CURR_U15", "Number of children (<15) who are currently on ART", baseCohort.size()));
		
		Cohort cohort = hmisCurrQuery.getLessThanAge(1, baseCohort);
		getDataRowForUpperOrLowerAge(cohort, minAge);
		
		while (minAge <= 50) {
			if (minAge < 15) {
				
				getDataRowForRange(minAge, maxAge, name, baseCohort);
				
			} else {
				if (minAge == 15) {
					baseCohort = hmisCurrQuery.getGreaterOrEqualToAge(15, hmisCurrQuery.getBaseCohort());
					data.addRow(buildColumn("HIV_TX_CURR_ADULT", "Number of adults who are currently on ART (>=15)",
					    baseCohort.size()));
				}
				if (minAge == 50) {
					cohort = hmisCurrQuery.getGreaterOrEqualToAge(50, baseCohort);
					getDataRowForUpperOrLowerAge(cohort, minAge);
				} else {
					getDataRowForRange(minAge, maxAge, name, baseCohort);
					
				}
				
			}
			minAge = maxAge + 1;
			maxAge = minAge + interval;
		}
		
	}
	
	private void getDataRowForUpperOrLowerAge(Cohort cohort, int minAge) {
		String name = "HIV_TX_CURR_U";
		String desc = "";
		if (minAge >= 50) {
			name = name + "50";
			desc = "Adult currently on ART aged 50+ yr  ";
		} else {
			name = name + "1";
			desc = "Children currently on ART aged <1 yr  ";
			
		}
		data.addRow(buildColumn(name + "", desc, cohort.size()));
		
		Cohort intersectCohort = HMISUtilies.getUnion(cohort, firstLineCohort);
		buildDataSetByRegiment(name + ".1", desc + " First-line regimen by sex", intersectCohort);
		
		intersectCohort = HMISUtilies.getUnion(cohort, secondLineCohort);
		buildDataSetByRegiment(name + ".2", desc + " Second-line regimen by sex", intersectCohort);
		
		intersectCohort = HMISUtilies.getUnion(cohort, thirdLineCohort);
		buildDataSetByRegiment(name + ".3", desc + " Third-line regimen by sex", intersectCohort);
	}
	
	private void getDataRowForRange(int minAge, int maxAge, String name, Cohort baseCohort) {
		String tempName;
		tempName = name + "" + (maxAge == 4 ? 5 : maxAge);
		
		String desc = maxAge >= 15 ? "Adult" : "Children";
		Cohort cohort = hmisCurrQuery.getBetweenAge(minAge, maxAge, baseCohort);
		data.addRow(buildColumn(tempName, desc + " currently on ART aged " + minAge + "-" + maxAge + " yr", cohort.size()));
		
		Cohort intersectCohort = HMISUtilies.getUnion(cohort, firstLineCohort);
		
		desc = desc + " currently on ART aged " + minAge + "-" + maxAge + " yr on  ";
		
		buildDataSetByRegiment(tempName + ".1", desc + " First-line regimen by sex", intersectCohort);
		intersectCohort = HMISUtilies.getUnion(cohort, secondLineCohort);
		buildDataSetByRegiment(tempName + ".2", desc + " Second-line regimen by sex", intersectCohort);
		
		intersectCohort = HMISUtilies.getUnion(cohort, thirdLineCohort);
		buildDataSetByRegiment(tempName + ".3", desc + " Third-line regimen by sex", intersectCohort);
	}
	
	private void buildGenderDataSet(String name, Cohort cohort) {
		Cohort maleCohort = hmisCurrQuery.getCohortByGender("M", cohort);
		Cohort femaleCohort = hmisCurrQuery.getCohortByGender("F", cohort);
		
		data.addRow(buildColumn(name + ".1", "Male", maleCohort.size()));
		data.addRow(buildColumn(name + ".2", "Female", femaleCohort.size()));
		
	}
	
	private void buildDataSetByRegiment(String name, String desc, Cohort cohort) {
		data.addRow(buildColumn(name, desc, cohort.size()));
		buildGenderDataSet(name, cohort);
	}
}
