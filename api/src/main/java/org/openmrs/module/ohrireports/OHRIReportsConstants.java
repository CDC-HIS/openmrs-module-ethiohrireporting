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
	
	public final static String PREP_FOLLOW_UP_ENCOUNTER_TYPE = "bc423d48-af6f-4354-af22-fec8ff1c0308";
	
	public final static String PREP_SCREENING_ENCOUNTER_TYPE = "8c2b6a9b-4795-417d-affe-2530a753b715";
	
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
	
	public final static String UAN_PATIENT_IDENTIFIERS = "td956f302-1723-4eca-9548-fb7e66706d34";
	
	public final static String OPENMRS_PATIENT_IDENTIFIERS = "8d793bee-c2cc-11de-8d13-0010c6dffd0f";
	
	public final static String DISPENSED_DOSE = "ddf10471-2e25-4010-b539-fff2c2900780";
	
	public final static String CURRENTLY_BREAST_FEEDING_CHILD = "5632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String WEIGHT = "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String PREGNANT_STATUS = "5272AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String YES = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String NO = "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String UNKNOWN = "1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	/**
	 * Associated Concepts
	 */
	public final static String VACCINATION_DATE = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String COVID_TREATMENT_OUTCOME = "a845f3e6-4432-4de4-9fff-37fa270b1a06";
	
	public final static String SPECIMEN_COLLECTION_DATE = "159951AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String COVID_LAB_TEST_CONFIRMATION_DATE = "a51c05e1-5ad5-420d-a082-966d2989b716";
	
	public final static String FINAL_COVID19_TEST_RESULT = "5da5c21b-969f-41bd-9091-e40d4c707544";
	
	public final static String NEXT_VISIT_DATE = "5096AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
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
	
	public final static String TRANSFERRED_IN = "160563AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String REASON_FOR_ART_ELIGIBILITY = "613718f1-ecf2-4228-b9c2-6157d574bd0b";
	
	public final static String FOLLOW_UP_DATE = "b8cd8630-56dd-495e-8c84-e36a636febe7";
	
	public final static String REGIMEN = "6d7d0327-e1f8-4246-bfe5-be1e82d94b14";
	
	public final static String ARV_DISPENSED_IN_DAYS = "f3911009-1a8f-42ee-bdfc-1e343c2839aa";
	
	public final static String ARV_30_Day = "fba421cf-a483-4329-b8b1-6a3ef16081bc";
	
	public final static String ARV_60_Day = "75d94023-7804-44f8-9998-9d678488af3e";
	
	public final static String ARV_90_Day = "4abbd98d-0c07-42f4-920c-7bbf0f5824dc";
	
	public final static String ARV_120_Day = "684c450f-878b-4b96-ab1b-2b539c30f033";
	
	public final static String ARV_150_Day = "fa23df4a-dd90-4a0b-a1c9-b44b7f820c93";
	
	public final static String ARV_180_Day = "e5f7cc4d-922a-4838-8c75-af9bdbb59bc8";
	
	public final static String TREATMENT_END_DATE = "164384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TB_ACTIVE_DATE = "159948AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TB_SCREENING_DATE = "179497a0-6f07-469f-bb2e-9b85644a82af";
	
	public final static String TB_TREATMENT_STATUS = "d4bc3007-3ba8-40ae-8d64-830bcbde56e2";
	
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
	
	public final static String DATE_COUNSELING_GIVEN = "72a28ebe-77ba-4592-9291-ac91e46ea770";
	
	public final static String CXC_SCREENING_ACCEPTED_DATE = "c7ecf767-325a-41c2-80a7-79c91762ab3e";
	
	public final static String CXCA_SCREENING_DONE = "01c546b4-e08a-4c0c-82ef-d387cab6bbbf";
	
	public final static String CXCA_SCREENING_DONE_YES = "165619AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_FIRST_TIME_SCREENING = "3a8bb4b4-7496-415d-a327-57ae3711d4eb";
	
	public final static String CXCA_TREATMENT_TYPE = "6b78badd-0b92-47f8-b16c-46559d5179b2";
	
	public final static String CXCA_TREATMENT_PRECANCEROUS_LESIONS = "c7ca4fc7-8944-4824-84b0-c299d59a89c0";
	
	public final static String REASON_FOR_REFERRAL = "52106755-062c-4cd5-a627-2373f5a0cef0";
	
	public final static String FEEDBACK = "98105b54-7453-4717-8bd2-249b7dcbdb98";
	
	public final static String REFERRAL_CONFIRMED_DATE = "88571a39-5caf-4260-b8d6-d0e28ca37410";
	
	public final static String CX_CA_SCREENING_OFFERED = "fc5ec0e6-8e56-4a23-8bf9-fbe464da12c7";
	
	public final static String CX_CA_SCREENING_ACCEPTED = "1b1dc36e-fe65-4f4b-8304-09fbd9c106ad";
	
	public final static String LINKED_TO_CX_CA_UNIT = "a3998691-d9cc-492b-81f2-7bd28a6e413b";
	
	public final static String BIOPSY_SAMPLE_COLLECTED_DATE = "5c93668e-6206-4cce-bdf9-7c6fb02991df";
	
	public final static String BIOPSY_RESULT_RECEIVED_DATE = "5c93668e-6206-4cce-bdf9-7c6fb02991df";
	
	public final static String DATE_OF_REFERRAL_TO_OTHER_HF = "5c93668e-6206-4cce-bdf9-7c6fb02991df";
	
	public final static String BIOPSY_RESULT = "df94b4c4-8a3a-46b2-be5b-e948403081a0";
	
	public final static String CXCA_TREATMENT_TYPE_NO_TREATMENT = "1107AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_CRYOTHERAPY = "162812AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_THERMOCOAGULATION = "166706AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_LEEP = "165084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_TYPE_OTHER_TREATMENT = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TREATMENT_STARTING_DATE = "163526AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DATE_LINKED_TO_CXCA_UNIT = "2df6bd1b-c200-4363-8293-0d72ef24e8b7";
	
	public final static String CXCA_TYPE_OF_SCREENING = "2c6f75a8-f35c-4671-939e-ebcc680c48a0";
	
	public final static String CXCA_FIRST_TIME_SCREENING_TYPE = "165269AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String CXCA_TYPE_OF_SCREENING_FIRST_TIME = "3a8bb4b4-7496-415d-a327-57ae3711d4eb";
	
	public final static String CXCA_TYPE_OF_SCREENING_RESCREEN = "13c3ee77-4e7c-4224-ae40-0b2727932a0f";
	
	public final static String CXCA_TYPE_OF_SCREENING_POST_TREATMENT = "3f4a6148-39c1-4980-81c6-6d703367c4a6";
	
	public final static String SCREENING_STRATEGY = "c842a287-f94c-48ee-a370-bd6540a0d1af";
	
	public final static String HPV_DNA_SCREENING_VIA_TRIAGE = "d3989991-4f6d-4336-9f84-cb4208d39ae6";
	
	public final static String VIA = "19cdb2fa-e25f-48bd-9e86-b00a72f9b4e1";
	
	public final static String HPV_DNA_SAMPLE_COLLECTION_DATE = "8b57d62c-c9a3-454a-b1af-929ca69603ce";
	
	public final static String HPV_DNA_SCREENING_RESULT = "159859AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String HPV_DNA_RESULT_RECEIVED_DATE = "510f2a47-3761-4903-b7eb-8ea389cecfe9";
	
	public final static String HPV_SUB_TYPE = "7bb81ac2-7a2a-4870-b965-fd3883d36f20";
	
	public final static String VIA_SCREENING_RESULT = "ff6b60e4-7310-4ddc-98ce-a2910c32a7a0";
	
	public final static String VIA_SCREENING_DATE = "f46c7ed3-65c3-451c-a8e1-4c615f795db1";
	
	public final static String VIA_NEGATIVE = "a08ab377-30bc-4ef6-bb9d-4cf6a0564ccc";
	
	public final static String VIA_POSITIVE_ELIGIBLE_FOR_CRYO = "7bc7c4f3-a636-478d-8a3f-65116093e37a";
	
	public final static String VIA_POSITIVE_NON_ELIGIBLE_FOR_CRYO = "be297cab-5ae6-4e7c-8657-b82730b7b8f1";
	
	public final static String VIA_SUSPICIOUS_RESULT = "159008AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String COLPOSCOPY_EXAM_DATE = "eb135e8e-5e19-4d6e-ad71-c6bdab26f73d";
	
	public final static String COLPOSCOPY_EXAM_FINDING = "93bf2a3e-1675-44c9-b7ee-b8ba9cb32b22";
	
	public final static String COLPOSCOPY_LOW_GRADE_SIL = "f0f52e6c-56fa-44c6-a81e-a3a7ac8548c4";
	
	public final static String COLPOSCOPY_HIGH_GRADE_SIL = "7276fa8a-3bab-4bd7-b647-8e9c8536ef30";
	
	public final static String CXCA_SCREENING_ACCEPTED_DATE = "8cb69148-cdce-448e-90c6-f582b5c169da";
	
	public final static String CYTOLOGY_RESULT = "9e5c5bd8-276c-497b-9ea1-9a5c9f94faa7";
	
	public final static String CYTOLOGY_RESULT_RECEIVED_DATE = "f0892f21-406c-446b-abd5-bb62f3ea2387";
	
	public final static String CYTOLOGY_NEGATIVE = "5e4fc757-0b14-49ae-b3b7-419666f41e15";
	
	public final static String CYTOLOGY_SAMPLE_COLLECTION_DATE = "3b5034de-ce0f-4017-80ab-17746ab3fe15";
	
	public final static String CYTOLOGY_ASCUS = "145822AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
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
	
	public final static String TRANSFERRED_OUT_UUID = "159492AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String ALIVE = "160429AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DEAD = "160432AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String STOP = "1260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TRANSFER_OUT = "1693AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String DROP = "160431AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	// public final static String RESTART = "ee957295-85b9-4433-bf12-45886cdc7dd1";
	public final static String RESTART = "162904AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String PR_EP_STARTED = "3b4bc0b2-acbb-4fb5-82eb-6f0479915862";
	
	// Prep
	
	public final static String PREP_SCREENED_DATE = "bd09b775-0294-4775-9615-964d98e06a4f";
	
	public final static String PREP_STARTED_DATE = "163526AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String PREP_FOLLOWUP_STATUS = "1f3a74f3-cafd-4bd0-9e3c-5251bd0c05e5";
	
	public final static String UNIQUE_IDENTIFICATION_CODE = "164402AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String PREP_NEXT_VISIT_DATE = "c596f199-4d76-4eca-b3c4-ffa631c0aee9";
	
	public final static String PREP_REGIMEN = "722ff3de-e2d1-4df4-8d05-ca881dc7073b";
	
	public final static String PREP_DOSE = "f3911009-1a8f-42ee-bdfc-1e343c2839aa";
	
	public final static String PREP_DOSE_END_DATE = "1dcc457b-638c-4103-887b-4e8581e052f8";
	
	public final static String SELF_IDENTIFYING_FSW = "160579AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String HAVE_HIV_POSITIVE_PARTNER = "1436AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String HIV_TEST_FINAL_RESULT = "40d1c129-5373-4005-95b1-409e56db9743";
	
	public final static String TB_SCREENED_RESULT = "160108AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String STI_SCREENING_RESULT = "7a643a93-3f11-4ad0-acfa-b15f2d7c8ddc";
	
	public final static String EGFR_ESTIMATE = "165570AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String PREP_MISSED_TABLETS = "a10bb1c2-64a5-4f62-9d2f-05ef1b261fb8";
	
	public final static String REASON_FOR_STOPPING_PREP = "4bf84596-dd44-4b81-a638-4617e189a89d";
	
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
	
	// #endregion
	
	// #region TPT (TB prevention)
	public final static String TPT_START_DATE = "162320AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TPT_TYPE = "54084c9e-bc87-4d95-89fc-eb9a2cffb592";
	
	public final static String TPT_ADHERENCE = "23d97715-589c-4dcf-bb86-70e26bba2269";
	
	public final static String ARV_ADHERENCE = "b1a646d3-78ff-4dd5-823a-5bef7d69ff3d";
	
	public final static String TPT_DISCONTINUED_DATE = "162281AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TPT_COMPLETED_DATE = "162279AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
	public final static String TPT_ALTERNATE_TYPE = "c1af657e-bc31-46a2-9f5e-55a1c9ae7507";
	
	public final static String TPT_DOSE_DAY_TYPE_ALTERNATE = "cc80b9ac-2ed1-4fd5-969a-9e324e91e95e";
	
	public final static String TPT_DOSE_DAY_TYPE_INH = "ad542a8d-cd7c-4d70-8ef3-829b89c05009";
	
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
	public final static String HIV_CONFIRMED_DATE = "160554AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	
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
