/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.ohrireports;

public class OHRIReportsConstants {
	
	/**
	 * Encounter types
	 */
	public final static String COVID_ASSESSMENT_ENCOUNTER_TYPE = "253a43d3-c99e-415c-8b78-ee7d4d3c1d54";
	
	public final static String CARE_AND_TREATMENT_SERVICE_ENROLLMENT_ENCOUNTER_TYPE = "7e54cd64-f9c3-11eb-8e6a-57478ce139b0";
	
	public final static String HTS_ENCOUNTER_TYPE = "30b849bd-c4f4-4254-a033-fe9cf01001d8";
	
	public final static String HTS_FOLLOW_UP_ENCOUNTER_TYPE = "136b2ded-22a3-4831-a39a-088d35a50ef5";
	
	public final static String COVID_VACCINATION_ENCOUNTER_TYPE = "5b37ce7a-c55e-4226-bdc8-5af04025a6de";
	
	public final static String HTS_RETROSPECTIVE_ENCOUNTER_TYPE = "79c1f50f-f77d-42e2-ad2a-d29304dde2fe";
	
	/**
	 * Cohort definitions
	 */
	public final static String CLIENTS_ASSESSED_FOR_COVID = "ec373b01-4ba3-488e-a322-9dd6a50cfdf7";
	
	public final static String CLIENTS_ENROLLED_TO_CARE = "51bec6f7-df43-426e-a83e-c1ae5501372f";
	
	public final static String HTS_CLIENTS = "7c1b4906-1caf-4a8e-a51d-7abdbb896805";
	
	public final static String CLIENTS_VACCINATED_FOR_COVID = "b5d52da9-10c2-43af-ae23-552acc5e445b";
	
	public final static String CLIENTS_WITH_COVID_OUTCOMES = "afb0d950-48fd-44d7-ae2c-79615cd125f0";
	
	public final static String COVID_CLIENTS_WITH_COLLECTED_SAMPLES = "a56b9edb-454a-4524-bc91-f5e3cdd10b6a";
	
	public final static String COVID_CLIENTS_WITH_CONFIRMED_LAB_RESULTS = "0cb7a13d-9088-4be4-9279-51190f9abd1b";
	
	public final static String TODAYZ_APPOINTMENTS = "ccbcf6d8-77b7-44a5-bb43-d352478ea4e9";
	
	public final static String CLIENTS_WITHOUT_COVID_19_OUTCOMES = "db6c4a18-28c6-423c-9da0-58d19e364a7f";
	
	public final static String COVID_CLIENTS_WITH_PENDING_LAB_RESULTS = "166aa2b1-ce55-4d16-9643-ca9d2e2694ea";
	
	public final static String ALL_PATIENTS_COHORT_UUID = "895d0025-84e2-4306-bdd9-66acc150ec21";
	
	public final static String MRN_PATIENT_IDENTIFIERS = "f85081e2-b4be-4e48-b3a4-7994b69bb101";
	
	public final static String OPENMRS_PATIENT_IDENTIFIERS = "8d793bee-c2cc-11de-8d13-0010c6dffd0f";
	
	public final static String DISPENSED_DOSE = "ddf10471-2e25-4010-b539-fff2c2900780";
	
	public final static String CURRENTLY_BREAST_FEEDING_CHILD = "5632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String PREGNANT_STATUS = "5272AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String YES = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String NO = "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String UNKNOWN = "1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TPT_START_DATE = "162320AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TPT_COMPLETED_DATE = "162279AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	/**
	 * Associated Concepts
	 */
	public final static String VACCINATION_DATE = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String COVID_TREATMENT_OUTCOME = "a845f3e6-4432-4de4-9fff-37fa270b1a06";
	
	public final static String SPECIMEN_COLLECTION_DATE = "159951AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String COVID_LAB_TEST_CONFIRMATION_DATE = "a51c05e1-5ad5-420d-a082-966d2989b716";
	
	public final static String FINAL_COVID19_TEST_RESULT = "5da5c21b-969f-41bd-9091-e40d4c707544";
	
	public final static String RETURN_VISIT_DATE = "5096AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// public final static String POSITIVE = "6378487b-584d-4422-a6a6-56c8830873ff";
	public final static String POSITIVE = "703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String NEGATIVE = "664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String SETTING_OF_HIV_TEST = "13abe5c9-6de2-4970-b348-36d352ee8eeb";
	
	public final static String APPROACH = "9641ead9-8821-4898-b633-a8e96c0933cf";
	
	public final static String POPULATION_TYPE = "166432AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String INITIAL_HIV_TEST_RESULT = "e767ba5d-7560-43ba-a746-2b0ff0a2a513";
	
	public final static String CONFIRMATORY_HIV_TEST_RESULT = "dbc4f8e9-7098-4585-9509-e2f84a4d8c6e";
	
	public final static String FINAL_HIV_RESULT = "e16b0068-b6a2-46b7-aba9-e3be00a7b4ab";
	
	public final static String DATE_CLIENT_RECEIVED_FINAL_RESULT = "160082AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String LINKED_TO_CARE_AND_TREATMENT_IN_THIS_FACILITY = "e8e8fe71-adbb-48e7-b531-589985094d30";
	
	// public final static String ART_START_DATE =
	// "159599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String ART_START_DATE = "ae329187-6232-4142-aa91-22c85bc8e5b5";
	
	public final static String TRANSFERRED_IN = "1693AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String REASON_FOR_ART_ELIGIBILITY = "613718f1-ecf2-4228-b9c2-6157d574bd0b";
	
	public final static String FOLLOW_UP_DATE = "b8cd8630-56dd-495e-8c84-e36a636febe7";
	
	public final static String REGIMEN = "6d7d0327-e1f8-4246-bfe5-be1e82d94b14";
	
	public final static String ARV_DISPENSED_IN_DAYS = "3a0709e9-d7a8-44b9-9512-111db5ce3989";
	
	public final static String TREATMENT_END_DATE = "164384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TB_SCREENING_DATE = "179497a0-6f07-469f-bb2e-9b85644a82af";
	
	public final static String TB_DIAGNOSTIC_TEST_RESULT = "c20140f7-d45d-4b44-a1b9-0534861a615d";
	
	public final static String DIAGNOSTIC_TEST = "002240c0-8672-4631-a32d-9bb9c34e4665";
	
	public final static String SMEAR_ONLY = "3a6f3d9d-623c-452b-8f96-bbdb8805aa97";
	
	public final static String MTB_RIF_ASSAY_WITH_OTHER_TESTING = "8fc383d0-7739-40ba-91a9-d0351c533284";
	
	public final static String MTB_RIF_ASSAY_WITH_OTHEROUT_TESTING = "d89ac55a-6c83-458b-a31d-2a28965955d5";
	
	public final static String LF_LAM_MTB_RIF = "9f4d51da-b09f-4de7-ac55-678f700eadfd";
	
	public final static String LF_LAM = "34e4571e-7950-42e8-9936-a204f5f01a5b";
	
	public final static String LF_LAM_RESULT = "98ff157c-c736-4078-8acd-847a74accb64";
	
	public final static String GENE_XPERT_RESULT = "a10a7db7-5ec5-4d62-9b35-cc9ac222a05c";
	
	public final static String ADDITIONAL_TEST_OTHERTHAN_GENE_XPERT = "9224c3c7-d2d7-4165-88cb-81e5aec30d70";
	
	public final static String SPECIMEN_SENT = "161934AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_FIRST_TIME_SCREENING = "3a8bb4b4-7496-415d-a327-57ae3711d4eb";
	
	public final static String CXCA_TREATMENT_TYPE_NO_TREATMENT = "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_CRYOTHERAPY = "162812AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_THERMOCOAGULATION = "166706AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_LEEP = "165084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_OTHER_TREATMENT = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_STARTING_DATE = "163526AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TYPE_OF_SCREENING = "9bd94bd2-059c-4273-8b6b-31a8e02c6f02";
	
	public final static String CXCA_TYPE_OF_SCREENING_FIRST_TIME = "3a8bb4b4-7496-415d-a327-57ae3711d4eb";
	
	public final static String CXCA_TYPE_OF_SCREENING_RESCREEN = "13c3ee77-4e7c-4224-ae40-0b2727932a0f";
	
	public final static String CXCA_TYPE_OF_SCREENING_POST_TREATMENT = "3f4a6148-39c1-4980-81c6-6d703367c4a6";
	
	public final static String SCREENING_STRATEGY = "53ff5cd0-0f37-4190-87b1-9eb439a15e94";
	
	public final static String HPV_DNA_SCREENING_RESULT = "159859AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String VIA_SCREENING_RESULT = "ff6b60e4-7310-4ddc-98ce-a2910c32a7a0";
	
	public final static String VIA_NEGATIVE = "a08ab377-30bc-4ef6-bb9d-4cf6a0564ccc";
	
	public final static String VIA_POSITIVE = "7bc7c4f3-a636-478d-8a3f-65116093e37a";
	
	public final static String VIA_SUSPICIOUS_RESULT = "159008AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_SCREENING_ACCEPTED_DATE = "8cb69148-cdce-448e-90c6-f582b5c169da";
	
	public final static String CYTOLOGY_RESULT = "9e5c5bd8-276c-497b-9ea1-9a5c9f94faa7";
	
	public final static String CYTOLOGY_NEGATIVE = "5e4fc757-0b14-49ae-b3b7-419666f41e15";
	
	public final static String CYTOLOGY_ASCUS_POSITIVE = "145822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CYTOLOGY_GREATER_ASCUS_SUSPICIOUS = "912a5c48-8b07-4fd7-b2c3-ccb94fde7c68";
	
	/*
	 * undernourished concepts
	 */
	public final static String UNDERNOURISHED = "123815AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String NUTRITIONAL_STATUS = "ae4d72a4-ccf5-49ff-b395-6687c534b1a2";
	
	public final static String OVERWEIGHT = "114413AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String NORMAL = "1115AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String MODERATE_MAL_NUTRITION = "134722AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String MILD_MAL_NUTRITION = "134723AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String THERAPEUTIC_SUPPLEMENTARY_FOOD = "161005AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String SEVERE_MAL_NUTRITION = "126598AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	/**
	 * Viral Load Constant
	 */
	public final static String HIV_VIRAL_LOAD_COUNT = "856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DATE_VIRAL_TEST_RESULT_RECEIVED = "beeede36-cae4-4f6e-b4b9-e39e37353a82";
	
	public final static String HIV_ROUTINE_VIRAL_LOAD_COUNT = "9b8cef86-9093-4737-a641-3b8399618c85";
	
	public final static String HIV_TARGET_VIRAL_LOAD_COUNT = "8f75ce27-29fa-4a67-bc8a-295c94323220";
	
	public final static String HIV_VIRAL_LOAD_LOW_LEVEL_VIREMIA = "167378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String HIV_VIRAL_LOAD_STATUS = "2dc9ee04-4d12-4606-ae0f-86895bf14a44";
	
	public final static String HIV_VIRAL_LOAD_SUPPRESSED = "5d5e42cc-acc4-4069-b3a8-7163e0db5d96";
	
	public final static String HIV_VIRAL_LOAD_UNSUPPRESSED = "a6768be6-c08e-464d-8f53-5f4229508e54";
	
	public final static String HIV_HIGH_VIRAL_LOAD = "162185AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String VIRAL_LOAD_TEST_INDICATION = "6bb5b796-60bc-406c-abd9-fb9362ed5e80";
	
	/*
	 * End Of Viral Load Constant
	 */
	/**
	 * Reports
	 */
	public final static String HTS_REPORT_UUID = "3ffa5a53-fc65-4a1e-a434-46dbcf1c2de2";
	
	public final static String HTS_FOLLOW_UP_REPORT_UUID = "136b2ded-22a3-4831-a39a-088d35a50ef5";
	
	public final static String HTS_REPORT_DESIGN_UUID = "13aae526-a565-489f-b529-b1d96cca5f7c";
	
	public final static String COVID19_REPORT_UUID = "ecabd559-14f6-4c65-87af-1254dfdf1304";
	
	public final static String COVID19_REPORT_DESIGN_UUID = "4e33bb15-ac1c-4e82-a863-77cb705c6512";
	
	public final static String PATIENT_STATUS = "160433AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String FOLLOW_UP_STATUS = "222f64a8-a603-4d2e-b70e-2d90b622bb04";
	
	public final static String TRANSFERRED_UUID = "1693AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String ALIVE = "160429AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DEAD = "160432AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String STOP = "1260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TRANSFER_OUT = "1693AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DROP = "160431AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// public final static String RESTART = "ee957295-85b9-4433-bf12-45886cdc7dd1";
	public final static String RESTART = "162904AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// 162904AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
	
	public final static String PR_EP_STARTED = "3b4bc0b2-acbb-4fb5-82eb-6f0479915862";
	
	// Prep
	public final static String DISCORDANT_COUPLE = "6096AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String FEMALE_SEX_WORKER = "160579AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// pep
	public final static String EXPOSURE_TYPE = "916eebc3-1141-40e6-beaa-ad2b5685956b";
	
	public final static String OCCUPATIONAL = "453bf209-c408-4692-83e7-17d21282a8ae";
	
	public final static String NON_OCCUPATIONAL = "164837AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String SEXUAL_ASSAULT = "126582AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// #region
	public final static String UNIQUE_ANTIRETROVAIRAL_THERAPY_UAN = "164402AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String SERVICE_DELIVERY_POINT_NUMBER_MRN = "162054AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TB_TREATMENT_START_DATE = "1113AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TB_SCREENING_RESULT = "160108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// TODO: update as soon as the concept created
	public final static String ADHERENCE_UUID = "";
	
	// #endregion
	
	// #region TPT (TB prevention)
	public final static String TB_START_DATE = "162320AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// #endregion
	
	// #region Report Group
	public final static String DATIM_REPORT = "DATIM";
	
	public final static String HMIS_REPORT = "HMIS";
	
	public final static String LINE_LIST_REPORT = "LINELIST";
	
	// #endregion
	
	// #region drug concept uuid
	public final static String TDF_TENOFOVIR_DRUG = "84795AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TDF_FTC_DRUG = "104567AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TDF_3TC_DRUG = "97d14de6-89c4-49cb-9553-55de5cbc9b03";
	
	public final static String PREGNANCY_STATUS = "5272AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// #endregion
	
	public final static String REPORT_VERSION = "1.0.0-SNAPSHOT";
	
	/*
	 * Linkage Indicators Concepts
	 */
	public final static String HIV_POSITIVE_DATE = "160554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String LINKED_TO_CARE_TREATMENT = "c1bb9738-10aa-4905-bb5d-af4e55b4bb69";
	
	public final static String STARTED_ART_OR_kNOWN_POSITIVE_ON_ART = "1149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String FINAL_OUT_COME = "4599ebf7-6120-4593-80f0-72458b9fadad";
	
	public final static String LOST_TO_FOLLOW_UP = "5240AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CONFIRMED_REFERRAL = "4680464a-47ba-4b9c-bd62-ec228c2c1822";
	
	public final static String STARTED_ART_IN_OTHER_FACILITY = "4de0ba18-205b-4df9-be26-bdd08d964e6a";
	
	public final static String REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY = "d8ffb301-9a47-45ee-a465-f053cc060aab";
	
	public final static String REFERRED_TX_NOT_INITIATED = "97c667c6-567b-4b7b-adc1-12f06c584cd4";
	
	public final static String DIED = "160034AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// Regimens
	public final static String R1a30_D4T_30_3TC_NVP = "2798d3bc-2e0a-459c-b249-9516b380a69e";
	
	public final static String R1a40_D4T_40_3TC_NVP = "3495d89f-4d46-44d8-b1c9-d101bc9f15d4";
	
	public final static String R1b30_D4T_30_3TC_EFV = "ae0dc59c-eb3d-421b-913b-ee5a06ec6182";
	
	public final static String R1b40_D4T_40_3TC_EFV = "c5995b5c-19bb-42f6-895b-0b4627bc9dd1";
	
	public final static String R1c_AZT_3TC_NVP = "a9da3e97-3916-4834-854c-6bcbc5142aca";
	
	public final static String R1d_AZT_3TC_EFV = "b5951dd9-6bb2-4b63-af20-0707500108ea";
	
	public final static String R1e_TDF_3TC_EFV = "1f735bdd-4ccd-40ce-8746-28f031f38675";
	
	public final static String R1f_TDF_3TC_NVP = "8b727274-4a3c-4a81-ae50-9f759a87358d";
	
	public final static String R1g_ABC_3TC_EFV = "a664a63f-57f5-4cfc-80c0-4a8456725c2a";
	
	public final static String R1h_ABC_3TC_NVP = "71ecf102-8d3d-4a2a-b799-30773ad5d447";
	
	public final static String R1j_TDF_3TC_DTG = "2a811ceb-3c34-47ff-943a-425b41651784";
	
	public final static String R1i_OTHER_ADULT_1ST_LINE_REGIMEN = "eb171a2d-1b67-4adc-ae09-49b6d272810d";
	
	public final static String R1k_AZT_3TC_DTG = "c56be3a9-d678-4693-9566-84430239a2f5";
	
	public final static String R2a_ABC_DDL_LPV = "a87d9674-11af-4440-afd0-d097d84d0393";
	
	public final static String R2b_ABC_DDL_NFV = "82e23d8c-4c9d-4b18-a960-9f8890b86f2f";
	
	public final static String R2c_TDF_DDL_LPV = "cf29c24b-b796-4536-aaa5-8303f45bb5f8";
	
	public final static String R2d_TDF_DDL_NFV = "2e84a82a-8b93-40fc-a556-2028d39f5710";
	
	public final static String R2e_AZT_3TC_LPVr = "a1056dfb-c16d-4e5c-a56b-e33228d634f8";
	
	public final static String R2f_AZT_3TC_ATVr = "891bf06e-298a-40ee-8970-2311d18fed48";
	
	public final static String R2g_TDF_3TC_LPVr = "e2d1786e-4849-473e-b32a-2c82bd35e947";
	
	public final static String R2h_TDF_3TC_ATVr = "2e84a82a-8b93-40fc-a556-2028d39f5710";
	
	public final static String R2i_ABC_3TC_LPVr = "73c10f0d-0a24-411f-9c97-49ab9e7eb2ce";
	
	public final static String R2j_TDF_3TC_DTG = "2a17d96a-3457-4b93-b007-b540a7543618";
	
	public final static String R2k_AZT_3TC_DTG = "707d6943-9ec0-4c6b-8c72-f64143663043";
	
	public final static String R2L_OTHER_ADULT_2ND_LINE_REGIMEN = "25758e89-bfb1-4b9f-ba6d-240d953c99e8";
	
	public final static String R3a_DRVr_DTG_AZT_3TC = "ab265c19-afab-4497-84a9-c8b44a202ca5";
	
	public final static String R3b_DRVr_DTG_TDF_3TC = "7f9aed33-cc7e-4043-adc8-b947dd9638eb";
	
	public final static String R3c_DRVr_ABC_3TC_DTG = "664137bb-d538-4afc-bbd1-7538cf158882";
	
	public final static String R3d_OTHER_ADULT_3RD_LINE_REGIMEN = "dea28eb8-1f9b-455f-afcc-dc566898cb86";
	
	public final static String R3e_DRVr_TDF_3TC_EFV = "74f685e8-7ea1-40e1-adf1-651cdf30b272";
	
	public final static String R3f_DRVr_AZT_3TC_EFV = "e8cdf6eb-2b36-4cde-bb4b-f0fd1d6d748b";
	
	public final static String R4a_D4T_3TC_NVP = "c33efc4a-734f-4e89-92a4-71b259a5e547";
	
	public final static String R4b_D4T_3TC_EFV = "7bbee32a-31a8-4199-a8a2-2d15c748d80a";
	
	public final static String R4c_AZT_3TC_NVP = "756b4487-f358-4817-b248-9c05080bad67";
	
	public final static String R4d_AZT_3TC_EFV = "ad169a6d-cf1a-4363-a2da-be85998612bd";
	
	public final static String R4e_TDF_3TC_EFV = "d3eedcf3-9571-42a2-b8b0-189e93f72f47";
	
	public final static String R4f_AZT_3TC_LPVr = "0343175a-e931-4f6c-a0c2-99e55637bda9";
	
	public final static String R4g_ABC_3TC_LPVr = "e4353cd1-5a0e-4870-8af4-f012ca0145fe";
	
	public final static String R4h_OTHER_CHILD_1ST_LINE_REGIMEN = "582c6b67-2f0a-4cc0-b7c0-632b4b387a17";
	
	public final static String R4i_TDF_3TC_DTG = "0dbe6b49-7356-4cf0-b489-890e18c38465";
	
	public final static String R4j_ABC_3TC_DTG = "eae01634-65ec-4174-a95c-622456663727";
	
	public final static String R4k_AZT_3TC_DTG = "dabcfa6d-56e5-4e5d-a2f2-faf4e18b9211";
	
	public final static String R4L_ABC_3TC_EFV = "c94ca2da-8066-42f7-b4c9-582dae83e552";
	
	public final static String R5a_ABC_DDL_LPY = "fcafc964-f018-41c8-9dc9-343a2f4a94b5";
	
	public final static String R5b_ABC_DDL_NFV = "7c30ed1a-a770-4b74-8704-82077427128d";
	
	public final static String R5c_TDF_DDL_LPV = "18c78c5f-082c-4acc-a121-62fa334f34d3";
	
	public final static String R5d_TDF_DDL_NFV = "5c9424e6-4f18-44e1-a074-e561643a7d91";
	
	public final static String R5e_ABC_3TC_LPVr = "fbb4db0f-4cf4-4ca3-bde0-e20eb065e7da";
	
	public final static String R5f_AZT_3TC_LPVr = "9ef4d67d-0e05-422a-aac5-80eaf71daadc";
	
	public final static String R5g_TDF_3TC_EFV = "ee5b1c56-1131-40a6-a880-5dd33a72a861";
	
	public final static String R5h_ABC_3TC_EFV = "e341cf6c-4923-4eb1-8c47-e76eef19e8cf";
	
	public final static String R5i_TDF_3TC_LPVr = "1294646b-2c36-4169-a0aa-a0c7692003fc";
	
	public final static String R5j_OTHER_CHILD_2ND_LINE_REGIMEN = "0cc9e467-c1ce-4eff-879d-8936a81b6ea3";
	
	public final static String R5k_RAL_AZT_3TC = "2e58ef33-d019-4bd9-97b8-553e39e12646";
	
	public final static String R5L_RAL_ABC_3TC = "8feb33db-71db-4fd0-a0e7-c02f81fcc76f";
	
	public final static String R5m_ABC_3TC_DTG = "3ed5d8a0-c957-4439-8f86-6c2538e69d3a";
	
	public final static String R5n_AZT_3TC_DTG = "10c0f78a-cd78-40cd-9824-8d24dc95bb90";
	
	public final static String R5o_TDF_3TC_DTG = "d34b5c26-c70d-4720-9784-cf8a0673d6b0";
	
	public final static String R6a_DRVr_RAL_AZT_3TC = "5415f1b1-a680-4870-89c5-3324d42da408";
	
	public final static String R6b_DRVr_RAL_TDF_3TC = "3e7d018b-9669-4f48-9298-d7c633f86b0f";
	
	public final static String R6c_DRVr_DTG_AZT_3TC = "5c8d27eb-4642-4c6f-b131-1182df8d1276";
	
	public final static String R6d_DRVr_DTG_TDF_3TC = "ee226f28-5e37-4c40-a8e0-47dfade32712";
	
	public final static String R6e_OTHER_CHILD_3RD_LINE_REGIMEN = "57eef726-e977-4efd-996b-a94860341320";
	
	public final static String R6f_DRVr_DTG_ABC_3TC = "e3eb6ae1-fd70-47b2-b1a6-702fae87b061";
	
	public final static String R6g_DRVr_ABC_3TC_EFV = "a270c994-8182-4204-9a1e-c7aa78a43b23";
	
	public final static String R6h_DRVr_AZT_3TC_EFV = "f0724e33-ca6e-415e-b385-91d53e125cfb";
	
	public final static String DSD_CATEGORY = "defeb4ff-d07b-4e4a-bbd6-d4281c1384a2";
	
	public final static String DSD_6MMD = "0f8de420-27b1-46bb-9619-b2f86bc775bb";
	
	public final static String DSD_3MMD = "c4797711-5a47-49ad-980c-bed044a91c6d";
	
	public final static String DSD_FTAR = "347b4cbf-76ec-47f7-bdd0-f75f159d0665";
	
	public final static String DSD_PCAD = "6624ab9c-7aeb-4a26-942f-f5717739a762";
	
	public final static String DSD_HEP_CAG = "83b84a66-7b31-4b61-be54-f14bc4d50da7";
	
	public final static String DSD_ADOLESCENT = "7b30d810-4c0c-4bb7-a0b6-c1be570d3e80";
	
	public final static String DSD_KP = "a8e97cdd-b094-4589-a724-3b123be7391e";
	
	public final static String DSD_MCH = "e2d258e7-31fb-403b-92b2-8dd309695f02";
	
	public final static String DSD_OTHER = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DSD_AHDCM = "69e20fac-25ff-46c5-8681-756ba0078c80";
	
	public final static String TB_SCREENING_OBSERVATION_ = "246831e5-65e8-411f-aac9-57adcc4fb12c";
	
	public final static String TB_PROPHYLAXIS_TYPE_ALTERNATE = "f18eded5-67a9-4f02-b131-9a6230e64d18";
	
	public final static String TB_PROPHYLAXIS_TYPE_ALTERNATE_3HP = "e8588507-635c-4e28-a1f8-3c493e30b2f5";
	
	public final static String TB_PROPHYLAXIS_TYPE_ALTERNATE_3HR = "c989f10b-6ab4-44d2-a8cd-b4f45fc3ab9b";
	
	public final static String TB_PROPHYLAXIS_TYPE = "54084c9e-bc87-4d95-89fc-eb9a2cffb592";
	
	public final static String TB_PROPHYLAXIS_TYPE_INH = "8ba598a1-9e8a-4499-bd7a-54c3d0c94f01";
	
	/*
	 * Family planning methods and others related to family planning
	 */
	public final static String FAMILY_PLANNING_METHODS = "374AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String ORAL_CONTRACEPTIVE_PILL = "780AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String INJECTABLE = "5279AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DIAPHRAGM_CERVICAL_CAP = "5278AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String IMPLANTABLE_HORMONE = "159589AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String INTRAUTERINE_DEVICE = "5275AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
}
