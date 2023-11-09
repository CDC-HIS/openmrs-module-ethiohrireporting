package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.RegimentConstant.R1d_AZT_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1e_TDF_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1g_ABC_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1j_TDF_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R1k_AZT_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R2e_AZT_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R2f_AZT_3TC_ATVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R2g_TDF_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R2h_TDF_3TC_ATVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R2i_ABC_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R2j_TDF_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R2k_AZT_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R3a_DRVr_DTG_AZT_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R3b_DRVr_DTG_TDF_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R3c_DRVr_ABC_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R3e_DRVr_TDF_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R3f_DRVr_AZT_3TC_EFV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.ColumnBuilder;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr.AggregateByAgeAndRegiment.LEVEL;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr.HmisCurrQuery.Regiment;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public class AggregateByAgeGenderAndPregnancyStatus extends ColumnBuilder {
	
	HmisCurrQuery hmisCurrQuery;
	
	SimpleDataSet data;
	
	Cohort firstLineCohort, secondLineCohort, thirdLineCohort;
	
	public AggregateByAgeGenderAndPregnancyStatus(HmisCurrQuery hmisCurrQuery, SimpleDataSet data, Cohort firstLineCohort,
	    Cohort secondLineCohort, Cohort thirdLineCohort) {
		this.hmisCurrQuery = hmisCurrQuery;
		this.data = data;
		this.firstLineCohort = firstLineCohort;
		this.secondLineCohort = secondLineCohort;
		this.thirdLineCohort = thirdLineCohort;
		buildDataSet();
	}
	
	private void buildDataSet() {
		Cohort aboveAndEqual20Cohort = hmisCurrQuery.getGreaterOrEqualToAge(20, hmisCurrQuery.getBaseCohort());
		List<String> regimentConceptUUIDS = new ArrayList<>();

		data.addRow(buildColumn("HIV_TX_CURR_REG_20", "Adults >=20 years currently on ART by regimen type",
				aboveAndEqual20Cohort.size()));
		// #region first line
		Cohort cohort = HMISUtilies.getUnion(aboveAndEqual20Cohort, firstLineCohort);
		data.addRow(buildColumn("HIV_TX_CURR_REG_20.1",
				" Adults >=20 years currently on first line regimen by regimen type",
				cohort.size()));
		regimentConceptUUIDS = Arrays.asList(R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1g_ABC_3TC_EFV, R1j_TDF_3TC_DTG,
				R1k_AZT_3TC_DTG, "1i");
		buildRow(regimentConceptUUIDS, LEVEL.FIRST_LINE, cohort, "HIV_TX_CURR_REG_20.1");
		// #endregion
		// #region second line
		cohort = HMISUtilies.getUnion(aboveAndEqual20Cohort, secondLineCohort);
		data.addRow(buildColumn("HIV_TX_CURR_REG_20.2",
				" Adults >=20 years currently on second line regimen by regimen type",
				cohort.size()));
		regimentConceptUUIDS = Arrays.asList(R2e_AZT_3TC_LPVr,
				R2f_AZT_3TC_ATVr,
				R2g_TDF_3TC_LPVr,
				R2h_TDF_3TC_ATVr,
				R2i_ABC_3TC_LPVr,
				R2j_TDF_3TC_DTG,
				R2k_AZT_3TC_DTG,
				"2L");
		buildRow(regimentConceptUUIDS, LEVEL.SECOND_LINE, cohort, "HIV_TX_CURR_REG_20.2");
		// #endregion

		// #region third line
		cohort = HMISUtilies.getUnion(aboveAndEqual20Cohort, thirdLineCohort);
		data.addRow(buildColumn("HIV_TX_CURR_REG_20.3",
				" Adults >=20 years currently on third line regimen by regimen type",
				cohort.size()));
		regimentConceptUUIDS = Arrays.asList(
				R3a_DRVr_DTG_AZT_3TC,
				R3b_DRVr_DTG_TDF_3TC,
				R3c_DRVr_ABC_3TC_DTG,
				R3e_DRVr_TDF_3TC_EFV,
				R3f_DRVr_AZT_3TC_EFV,
				"3d");
		buildRow(regimentConceptUUIDS, LEVEL.THIRD_LINE, cohort, "HIV_TX_CURR_REG_20.3");
		// #endregion

	}
	
	private void buildRow(List<String> regimentConceptUUIDS, LEVEL level, Cohort cohort, String name) {
		
		Cohort maleCohort = hmisCurrQuery.getGenderSpecificCohort("M", cohort);
		Cohort femaleCohort = hmisCurrQuery.getGenderSpecificCohort("F", cohort);
		Cohort pregnantCohort = hmisCurrQuery.getPatientByPregnantStatus(femaleCohort, YES);
		Cohort nonPregnantFemaleCohort = HMISUtilies.getOuterUnion(femaleCohort, pregnantCohort);
		
		List<Regiment> maleRegiments = hmisCurrQuery.getPatientCountByRegiment(regimentConceptUUIDS, maleCohort);
		List<Regiment> femaleNonPregnantRegiments = hmisCurrQuery.getPatientCountByRegiment(regimentConceptUUIDS,
		    nonPregnantFemaleCohort);
		List<Regiment> femalePregnantRegiments = hmisCurrQuery.getPatientCountByRegiment(regimentConceptUUIDS,
		    pregnantCohort);
		
		int femalePregnantCount = 0;
		int femaleNonPregnantCount = 0;
		int maleCount = 0;
		int count = 0;
		int currentCount = 0;
		String otherName = regimentConceptUUIDS.get(regimentConceptUUIDS.size() - 1);
		
		for (Regiment regiment : maleRegiments) {
			count++;
			currentCount = regiment.getCount().intValue();
			maleCount = maleCount + currentCount;
			data.addRow(buildColumn(name + "." + count, regiment.getName() + ", Male", currentCount));
			
			for (Regiment pregnantRegiment : femalePregnantRegiments) {
				if (pregnantRegiment.getConceptId() == regiment.getConceptId()) {
					count++;
					currentCount = pregnantRegiment.getCount().intValue();
					femalePregnantCount = femalePregnantCount + currentCount;
					data.addRow(buildColumn(name + "." + count, pregnantRegiment.getName() + ", Female - pregnant",
					    currentCount));
					break;
				}
			}
			
			for (Regiment nonPregnantRegiment : femaleNonPregnantRegiments) {
				if (nonPregnantRegiment.getConceptId() == regiment.getConceptId()) {
					count++;
					currentCount = nonPregnantRegiment.getCount().intValue();
					femaleNonPregnantCount = femaleNonPregnantCount + currentCount;
					data.addRow(buildColumn(name + "." + count,
					    nonPregnantRegiment.getName() + ", Female - non - pregnant ", currentCount));
					break;
				}
			}
		}
		
		if (level == LEVEL.FIRST_LINE) {
			otherName = otherName + " - other Adult first line regimen ";
		} else if (level == LEVEL.SECOND_LINE) {
			otherName = otherName + " - other Adult second line regimen";
			
		} else {
			otherName = otherName + " - other Adult third line regimen ";
			
		}
		count++;
		data.addRow(buildColumn(name + "." + count, otherName + ", Male", maleCohort.size() - maleCount));
		count++;
		data.addRow(buildColumn(name + "." + count, otherName + ", Female - pregnant", pregnantCohort.size()
		        - femalePregnantCount));
		count++;
		data.addRow(buildColumn(name + "." + count, otherName + ", Female - non-pregnant", nonPregnantFemaleCohort.size()
		        - femaleNonPregnantCount));
		
	}
}
