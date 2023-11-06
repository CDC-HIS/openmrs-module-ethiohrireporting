package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import static org.openmrs.module.ohrireports.RegimentConstant.R1a30_D4T_30_3TC_NVP;
import static org.openmrs.module.ohrireports.RegimentConstant.R1a40_D4T_40_3TC_NVP;
import static org.openmrs.module.ohrireports.RegimentConstant.R1b30_D4T_30_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1b40_D4T_40_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1c_AZT_3TC_NVP;
import static org.openmrs.module.ohrireports.RegimentConstant.R1d_AZT_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1e_TDF_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1f_TDF_3TC_NVP;
import static org.openmrs.module.ohrireports.RegimentConstant.R1g_ABC_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R1h_ABC_3TC_NVP;
import static org.openmrs.module.ohrireports.RegimentConstant.R1i_OTHER_ADULT_1ST_LINE_REGIMEN;
import static org.openmrs.module.ohrireports.RegimentConstant.R1j_TDF_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R1k_AZT_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R2L_OTHER_ADULT_2ND_LINE_REGIMEN;
import static org.openmrs.module.ohrireports.RegimentConstant.R2a_ABC_DDL_LPV;
import static org.openmrs.module.ohrireports.RegimentConstant.R2b_ABC_DDL_NFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R2c_TDF_DDL_LPV;
import static org.openmrs.module.ohrireports.RegimentConstant.R2d_TDF_DDL_NFV;
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
import static org.openmrs.module.ohrireports.RegimentConstant.R3d_OTHER_ADULT_3RD_LINE_REGIMEN;
import static org.openmrs.module.ohrireports.RegimentConstant.R3e_DRVr_TDF_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R3f_DRVr_AZT_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R4L_ABC_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R4a_D4T_3TC_NVP;
import static org.openmrs.module.ohrireports.RegimentConstant.R4b_D4T_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R4c_AZT_3TC_NVP;
import static org.openmrs.module.ohrireports.RegimentConstant.R4d_AZT_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R4e_TDF_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R4f_AZT_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R4g_ABC_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R4h_OTHER_CHILD_1ST_LINE_REGIMEN;
import static org.openmrs.module.ohrireports.RegimentConstant.R4i_TDF_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R4j_ABC_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R4k_AZT_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R5L_RAL_ABC_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R5a_ABC_DDL_LPY;
import static org.openmrs.module.ohrireports.RegimentConstant.R5b_ABC_DDL_NFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R5c_TDF_DDL_LPV;
import static org.openmrs.module.ohrireports.RegimentConstant.R5d_TDF_DDL_NFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R5e_ABC_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R5f_AZT_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R5g_TDF_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R5h_ABC_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R5i_TDF_3TC_LPVr;
import static org.openmrs.module.ohrireports.RegimentConstant.R5j_OTHER_CHILD_2ND_LINE_REGIMEN;
import static org.openmrs.module.ohrireports.RegimentConstant.R5k_RAL_AZT_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R5m_ABC_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R5n_AZT_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R5o_TDF_3TC_DTG;
import static org.openmrs.module.ohrireports.RegimentConstant.R6a_DRVr_RAL_AZT_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R6b_DRVr_RAL_TDF_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R6c_DRVr_DTG_AZT_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R6d_DRVr_DTG_TDF_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R6e_OTHER_CHILD_3RD_LINE_REGIMEN;
import static org.openmrs.module.ohrireports.RegimentConstant.R6f_DRVr_DTG_ABC_3TC;
import static org.openmrs.module.ohrireports.RegimentConstant.R6g_DRVr_ABC_3TC_EFV;
import static org.openmrs.module.ohrireports.RegimentConstant.R6h_DRVr_AZT_3TC_EFV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.ColumnBuilder;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr.HmisCurrQuery.Regiment;
import org.openmrs.module.reporting.dataset.SimpleDataSet;

public class AggregateByAgeAndRegiment extends ColumnBuilder {
	
	enum LEVEL {
		FIRST_LINE, SECOND_LINE, THIRD_LINE
	}
	
	HmisCurrQuery hmisCurrQuery;
	
	SimpleDataSet data;
	
	Cohort firstLineCohort, secondLineCohort, thirdLineCohort;
	
	public AggregateByAgeAndRegiment(HmisCurrQuery hmisCurrQuery, SimpleDataSet data, Cohort firstLineCohort,
	    Cohort secondLineCohort, Cohort thirdLineCohort) {
		this.hmisCurrQuery = hmisCurrQuery;
		this.data = data;
		this.firstLineCohort = firstLineCohort;
		this.secondLineCohort = secondLineCohort;
		this.thirdLineCohort = thirdLineCohort;
		buildAggregateByAgeAndRegimentDataset();
	}
	
	private void buildAggregateByAgeAndRegimentDataset() {
                List<String> concepts = new ArrayList<>();
                Cohort cohort = new Cohort();
                Cohort under19Cohort = hmisCurrQuery.getLessThanOrEqualAge(19, hmisCurrQuery.getBaseCohort());
                data.addRow(buildColumn("HIV_TX_CURR_REG_U19",
                                "Number of children (<19) who are currently on ART by regimen type",
                                under19Cohort.size()));

                // #region 1<
                cohort = hmisCurrQuery.getLessThanAge(1, under19Cohort);
                data.addRow(buildColumn("HIV_TX_CURR_REG_U1", "Children currently on ART aged <1 yr by regimen type",
                                cohort.size()));

                concepts = Arrays.asList(R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG,
                                "4h");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U1.1", LEVEL.FIRST_LINE, "Children currently on ART aged <1 yr ");
                concepts = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5m_ABC_3TC_DTG, R5n_AZT_3TC_DTG, "5j");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U1.2", LEVEL.SECOND_LINE, "Children currently on ART aged <1 yr ");

                concepts = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6f_DRVr_DTG_ABC_3TC, "6e");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U1.3", LEVEL.THIRD_LINE, "Children currently on ART aged <1 yr ");
                // #endregion 1<

                // #region 1-4
                cohort = hmisCurrQuery.getBetweenAge(1, 4, under19Cohort);
                data.addRow(buildColumn("HIV_TX_CURR_REG_U4.1", "Children currently on ART aged 1-4 yr by regimen type",
                                cohort.size()));
                concepts = Arrays.asList(R4d_AZT_3TC_EFV, R4f_AZT_3TC_LPVr, R4j_ABC_3TC_DTG, R4g_ABC_3TC_LPVr,
                                R4k_AZT_3TC_DTG,
                                R4L_ABC_3TC_EFV,
                                "4h");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U4.1", LEVEL.FIRST_LINE, "Children currently on ART aged 1-4 yr ");
                concepts = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5h_ABC_3TC_EFV, R5m_ABC_3TC_DTG,
                                R5n_AZT_3TC_DTG,
                                "5j");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U4.2", LEVEL.SECOND_LINE, "Children currently on ART aged 1-4 yr ");

                concepts = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV,
                                R6h_DRVr_AZT_3TC_EFV,
                                "6e");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U4.3", LEVEL.THIRD_LINE, "Children currently on ART aged 1-4 yr ");

                // #endregion 1-4

                // #region 5-9
                cohort = hmisCurrQuery.getBetweenAge(5, 9, under19Cohort);
                data.addRow(buildColumn("HIV_TX_CURR_REG_U9", "Children currently on ART aged 5-9 yr by regimen type",
                                cohort.size()));

                concepts = Arrays.asList(R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV, R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr,
                                R4i_TDF_3TC_DTG,
                                R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG,
                                R4L_ABC_3TC_EFV,
                                "4h");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U9.1", LEVEL.FIRST_LINE, "Children currently on ART aged 5-9 yr ");

                concepts = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV,
                                R5i_TDF_3TC_LPVr,
                                R5m_ABC_3TC_DTG,
                                R5n_AZT_3TC_DTG, R5o_TDF_3TC_DTG,
                                "5j");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U9.2", LEVEL.SECOND_LINE, "Children currently on ART aged 5-9 yr ");

                concepts = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC, R6f_DRVr_DTG_ABC_3TC,
                                R6g_DRVr_ABC_3TC_EFV,
                                R6h_DRVr_AZT_3TC_EFV,
                                "6e");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U9.3", LEVEL.THIRD_LINE, "Children currently on ART aged 5-9 yr ");
                // #endregion 5-9

                // #region 10-14
                cohort = hmisCurrQuery.getBetweenAge(10, 14, under19Cohort);
                data.addRow(buildColumn("HIV_TX_CURR_REG_U14",
                                "Children currently on ART aged 10-14 yr by regimen type",
                                cohort.size()));
                concepts = Arrays.asList(R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV, R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr,
                                R4i_TDF_3TC_DTG,
                                R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG,
                                R4L_ABC_3TC_EFV,
                                "4h");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U14.1", LEVEL.FIRST_LINE, "Children currently on ART aged 10-14 yr ");

                concepts = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV,
                                R5i_TDF_3TC_LPVr,
                                R5m_ABC_3TC_DTG,
                                R5n_AZT_3TC_DTG, R5o_TDF_3TC_DTG,
                                "5j");

                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U14.2", LEVEL.SECOND_LINE, "Children currently on ART aged 10-14 yr ");

                concepts = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC, R6f_DRVr_DTG_ABC_3TC,
                                R6g_DRVr_ABC_3TC_EFV,
                                R6h_DRVr_AZT_3TC_EFV,
                                "6e");

                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U14.3", LEVEL.THIRD_LINE, "Children currently on ART aged 10-14 yr ");

                // #endregion 10-14

                // #region 15-19
                cohort = hmisCurrQuery.getBetweenAge(15, 19, under19Cohort);
                data.addRow(buildColumn("HIV_TX_CURR_REG_U19", "Adult currently on ART aged 15-19 yr by regimen type",
                                cohort.size()));
                concepts = Arrays.asList(R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1g_ABC_3TC_EFV, R1j_TDF_3TC_DTG,
                                R1k_AZT_3TC_DTG,
                                "1i");
                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U19.1", LEVEL.FIRST_LINE, "Adult currently on ART aged 15-19 yr ");

                concepts = Arrays.asList(R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr,
                                R2i_ABC_3TC_LPVr, R2j_TDF_3TC_DTG, R2k_AZT_3TC_DTG,
                                "2L");

                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U19.2", LEVEL.SECOND_LINE, "Adult currently on ART aged 15-19 yr ");

                concepts = Arrays.asList(R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG,
                                R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV,
                                "3d");

                buildDataSetByRegiment(cohort,
                                concepts,
                                "HIV_TX_CURR_REG_U19.3", LEVEL.THIRD_LINE, "Adult currently on ART aged 15-19 yr ");

                // #endregion 15-19
        }
	
	private void buildDataSetByRegiment(Cohort cohort, List<String> conceptUUDs, String name,
                        LEVEL level, String base) {
                List<Regiment> regiments = new ArrayList<>();
                int foundCount = 0;
                int nameCount = 0;
                Cohort foundCohort = new Cohort();
                String desc = base;
                String lastName = conceptUUDs.get(conceptUUDs.size() - 1);
                String otherName = lastName;
                switch (level) {
                        case FIRST_LINE:
                                desc = desc + " on first line regimen by regimen type";
                                otherName = otherName + " other first line";
                                foundCohort = HMISUtilies.getUnion(cohort, firstLineCohort);
                                break;
                        case SECOND_LINE:
                                desc = desc + " on second line regimen by regimen type";
                                otherName = otherName + " other second line";
                                foundCohort = HMISUtilies.getUnion(cohort, secondLineCohort);

                                break;
                        case THIRD_LINE:
                                desc = desc + " on third line regimen by regimen type";
                                otherName = otherName + " other third line";
                                foundCohort = HMISUtilies.getUnion(cohort, thirdLineCohort);

                                break;
                        default:
                                break;
                }

                data.addRow(buildColumn(name, desc, foundCohort.size()));

                regiments = hmisCurrQuery.getPatientCountByRegiment(conceptUUDs, foundCohort);

                for (Regiment regiment : regiments) {
                        nameCount++;
                        foundCount = foundCount + regiment.getCount().intValue();
                        data.addRow(buildColumn(name + "." + nameCount, regiment.getName(),
                                        regiment.getCount().intValue()));

                }
                data.addRow(buildColumn(name + "." + nameCount, otherName, foundCohort.size() - foundCount));

        }
}
