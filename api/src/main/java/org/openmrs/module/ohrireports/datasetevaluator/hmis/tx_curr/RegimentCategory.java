package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import static org.openmrs.module.ohrireports.constants.RegimentConstant.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegimentCategory {
	
	public static List<String> getRegimentConcepts(REGIMENT_TYPE type) {
        List<String> conceptsUUId = new ArrayList<>();

        switch (type) {
            case FIRST_LINE:

                conceptsUUId.addAll(firstLineRegiment(true));
                conceptsUUId.addAll(firstLineOthersRegiment(true));
                conceptsUUId.addAll(firstLineRegiment(false));
                conceptsUUId.addAll(firstLineOthersRegiment(false));

                break;

            case SECOND_LINE:
                conceptsUUId.addAll(secondLineRegiment(true));
                conceptsUUId.addAll(secondLineOthersRegiment(true));
                conceptsUUId.addAll(secondLineRegiment(false));
                conceptsUUId.addAll(secondLineOthersRegiment(false));

                break;
            case THIRD_LINE:
                conceptsUUId.addAll(thirdLineRegiment(true));
                conceptsUUId.addAll(thirdLineOthersRegiment(true));
                conceptsUUId.addAll(thirdLineRegiment(false));
                conceptsUUId.addAll(thirdLineOthersRegiment(false));
                break;

            case FIRST_LINE_CHILDREN:
                conceptsUUId = firstLineRegiment(true);
                break;
            case FIRST_LINE_OTHERS_CHILDREN:
                conceptsUUId = firstLineOthersRegiment(true);
                break;

            case SECOND_LINE_CHILDREN:
                conceptsUUId = secondLineRegiment(true);
                break;
            case SECOND__LINE_OTHERS_CHILDREN:
                conceptsUUId = secondLineOthersRegiment(true);
                break;
            case THIRD_LINE_CHILDREN:
                conceptsUUId = thirdLineRegiment(true);
                break;
            case THIRD_LINE_OTHERS_CHILDREN:
                conceptsUUId = thirdLineOthersRegiment(true);
                break;
            case FIRST_LINE_ADULT:
                conceptsUUId = firstLineRegiment(false);
                break;
            case FIRST_LINE_OTHERS_ADULT:
                conceptsUUId = firstLineOthersRegiment(false);
                break;
            case SECOND_LINE_ADULT:
                conceptsUUId = secondLineRegiment(false);
                break;
            case SECOND__LINE_OTHERS_ADULT:
                conceptsUUId = secondLineOthersRegiment(false);
                break;
            case THIRD_LINE_ADULT:
                conceptsUUId = thirdLineRegiment(false);
                break;
            case THIRD_LINE_OTHERS_ADULT:
                conceptsUUId = thirdLineOthersRegiment(false);
                break;
            default:
                break;
        }
        return conceptsUUId;
    }
	
	private static List<String> firstLineOthersRegiment(Boolean isChildren) {

        if (isChildren) {
            return Arrays.asList(R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4j_ABC_3TC_DTG,
                    R4k_AZT_3TC_DTG, R4a_D4T_3TC_NVP,
                    R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV,
                    R4h_OTHER_CHILD_1ST_LINE_REGIMEN, R4i_TDF_3TC_DTG, R4L_ABC_3TC_EFV,
                    R1a30_D4T_30_3TC_NVP, R1a40_D4T_40_3TC_NVP, R1b30_D4T_30_3TC_EFV, R1b40_D4T_40_3TC_EFV,
                    R1c_AZT_3TC_NVP, R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1f_TDF_3TC_NVP, R1g_ABC_3TC_EFV,
                    R1h_ABC_3TC_NVP,
                    R1j_TDF_3TC_DTG, R1i_OTHER_ADULT_1ST_LINE_REGIMEN, R1k_AZT_3TC_DTG);

            // adult and include others
        } else if (!isChildren) {
            return Arrays.asList(R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1g_ABC_3TC_EFV,
                    R1j_TDF_3TC_DTG, R1k_AZT_3TC_DTG,
                    R1a30_D4T_30_3TC_NVP, R1a40_D4T_40_3TC_NVP, R1b30_D4T_30_3TC_EFV, R1b40_D4T_40_3TC_EFV,
                    R1c_AZT_3TC_NVP, R1f_TDF_3TC_NVP, R1h_ABC_3TC_NVP, R1i_OTHER_ADULT_1ST_LINE_REGIMEN,
                    R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV,
                    R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4h_OTHER_CHILD_1ST_LINE_REGIMEN, R4i_TDF_3TC_DTG,
                    R4j_ABC_3TC_DTG,
                    R4k_AZT_3TC_DTG, R4L_ABC_3TC_EFV);
        }
        return new ArrayList<>();
    }
	
	private static List<String> secondLineOthersRegiment(Boolean isChildren) {

        // children and includes other
        if (isChildren) {
            return Arrays.asList(R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr,
                    R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr,
                    R2i_ABC_3TC_LPVr, R2j_TDF_3TC_DTG, R2k_AZT_3TC_DTG, R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV,
                    R2c_TDF_DDL_LPV, 
                    R2h_TDF_3TC_ATVr,R2d_TDF_DDL_NFV,
                    R2L_OTHER_ADULT_2ND_LINE_REGIMEN,
                    R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV, R2c_TDF_DDL_LPV,
                    R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr, R2i_ABC_3TC_LPVr,
                    R2j_TDF_3TC_DTG);
        } else if (!isChildren) {
            return Arrays.asList(
                    R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV, R2c_TDF_DDL_LPV, 
                    R2d_TDF_DDL_NFV,
                    R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2i_ABC_3TC_LPVr,
                    R2j_TDF_3TC_DTG,
                    R2k_AZT_3TC_DTG, R2L_OTHER_ADULT_2ND_LINE_REGIMEN,
                    R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV,
                    R5d_TDF_DDL_NFV, R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV,
                    R5i_TDF_3TC_LPVr,
                    R5j_OTHER_CHILD_2ND_LINE_REGIMEN, R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC, R5m_ABC_3TC_DTG,
                    R5n_AZT_3TC_DTG,
                    R5o_TDF_3TC_DTG);

        }
        return new ArrayList<>();
    }
	
	private static List<String> thirdLineOthersRegiment(Boolean isChildren) {

        // children and includes other
        if (isChildren) {
            return Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV,
                    R6h_DRVr_AZT_3TC_EFV, R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6d_DRVr_DTG_TDF_3TC,
                    R6e_OTHER_CHILD_3RD_LINE_REGIMEN,
                    R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG,
                    R3d_OTHER_ADULT_3RD_LINE_REGIMEN, R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV);

            // adult and include others
        } else if (!isChildren) {
            return Arrays.asList(R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG,
                    R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV, R3d_OTHER_ADULT_3RD_LINE_REGIMEN, R6a_DRVr_RAL_AZT_3TC,
                    R6b_DRVr_RAL_TDF_3TC, R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC,
                    R6e_OTHER_CHILD_3RD_LINE_REGIMEN, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);
        }
        return new ArrayList<>();
    }
	
	private static List<String> firstLineRegiment(Boolean isChildren) {
		if (isChildren) {
			return Arrays.asList(R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV,
			    R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4h_OTHER_CHILD_1ST_LINE_REGIMEN, R4i_TDF_3TC_DTG, R4j_ABC_3TC_DTG,
			    R4k_AZT_3TC_DTG, R4L_ABC_3TC_EFV);
		} else {
			return Arrays.asList(R1a30_D4T_30_3TC_NVP, R1a40_D4T_40_3TC_NVP, R1b30_D4T_30_3TC_EFV, R1b40_D4T_40_3TC_EFV,
			    R1c_AZT_3TC_NVP, R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1f_TDF_3TC_NVP, R1g_ABC_3TC_EFV, R1h_ABC_3TC_NVP,
			    R1j_TDF_3TC_DTG, R1i_OTHER_ADULT_1ST_LINE_REGIMEN, R1k_AZT_3TC_DTG);
		}
		
	}
	
	private static List<String> secondLineRegiment(Boolean isChildren) {
		if (isChildren) {
			return Arrays.asList(R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV, R5d_TDF_DDL_NFV, R5e_ABC_3TC_LPVr,
			    R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV, R5i_TDF_3TC_LPVr, R5j_OTHER_CHILD_2ND_LINE_REGIMEN,
			    R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC, R5m_ABC_3TC_DTG, R5n_AZT_3TC_DTG, R5o_TDF_3TC_DTG);
		}
		return Arrays.asList(R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV, R2c_TDF_DDL_LPV, R2h_TDF_3TC_ATVr, R2e_AZT_3TC_LPVr,
		    R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr, R2i_ABC_3TC_LPVr, R2j_TDF_3TC_DTG, R2k_AZT_3TC_DTG,
		    R2L_OTHER_ADULT_2ND_LINE_REGIMEN);
		
	}
	
	public static List<String> thirdLineRegiment(Boolean isChildren) {
		if (isChildren) {
			return Arrays.asList(R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC,
			    R6e_OTHER_CHILD_3RD_LINE_REGIMEN, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);
		}
		return Arrays.asList(R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG,
		    R3d_OTHER_ADULT_3RD_LINE_REGIMEN, R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV);
		
	}
	
	enum REGIMENT_TYPE {
		FIRST_LINE, SECOND_LINE, THIRD_LINE, FIRST_LINE_CHILDREN, FIRST_LINE_OTHERS_CHILDREN,
		
		SECOND_LINE_CHILDREN, SECOND__LINE_OTHERS_CHILDREN,
		
		THIRD_LINE_CHILDREN, THIRD_LINE_OTHERS_CHILDREN,
		
		FIRST_LINE_ADULT, FIRST_LINE_OTHERS_ADULT,
		
		SECOND_LINE_ADULT, SECOND__LINE_OTHERS_ADULT,
		
		THIRD_LINE_ADULT, THIRD_LINE_OTHERS_ADULT,
		
	}
}
