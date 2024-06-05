package org.openmrs.module.ohrireports.api.impl.query.pmtct;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.dao.PMTCTEncounter;
import org.openmrs.module.ohrireports.api.dao.PMTCTPatient;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.pmtct.PMTCTPatientRapidAntiBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class EIDQuery extends PatientQueryImpDao {
	
	public static final int MONTHS_IN_A_YEAR = 12;
	
	private final DbSessionFactory sessionFactory;
	
	private final Cohort baseCohort = new Cohort();
	
	private HashMap<Integer, PMTCTPatient> patientEncounterHashMap;
	@Autowired
	EncounterQuery encounterQuery;
	private List<Integer> followUpEncounter = new ArrayList<>();
	
	@Autowired
	public EIDQuery(DbSessionFactory _sessionFactory) {
		setSessionFactory(_sessionFactory);
		sessionFactory = _sessionFactory;
	}
	
	public HashMap<Integer, PMTCTPatient> getPatientEncounterHashMap() {
		return patientEncounterHashMap;
	}
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	//NOTE: Every encounter with sample collection date in a reporting date should be considered
	public void generateReport(Date start, Date end, String conceptUUID) {
		patientEncounterHashMap = getPMTCTPatientEncounter(start, end, conceptUUID,Arrays.asList(PMTCT_INITIAL_TEST, PMTCT_NINE_MONTH_FOR_PREVIOUS_NEGATIVE_TEST, PMTCT_DIAGNOSTIC_REPEAT_TEST));
	}
	public void generateReportForHMIS(Date start, Date end, String conceptUUID){
		patientEncounterHashMap = getPMTCTPatientEncounter(start,end,conceptUUID,Arrays.asList(PMTCT_INITIAL_TEST));
	}
	
	public void generateReportForLineList(Date start, Date end) {
		patientEncounterHashMap = getPMTCTPatientEncounterWithAllFields(start, end);
	}
	
	public List<PMTCTPatientRapidAntiBody> getPMTCTRapidAntiDote(Date start, Date end) {
		StringBuilder sqlBuilder = new StringBuilder("select distinct ob.person_id, concat(pn.given_name,' ',pn.middle_name,' ',pn.family_name), pi.identifier,p.gender,p.birthdate, ob.value_datetime,rapid_result.name as antibody_result from obs as ob inner join encounter as e on ob.encounter_id = e.encounter_id ");
		sqlBuilder.append(" inner join person as p on p.person_id = ob.person_id ");
		sqlBuilder.append(" inner join person_name as  pn on pn.person_id = ob.person_id ");
		sqlBuilder.append(" inner join patient_identifier as pi on pi.patient_id = p.person_id ");
		sqlBuilder.append(" inner join patient_identifier_type as pit on pit.patient_identifier_type_id = pi.identifier_type and pi.uuid ='")
				.append(MRN_PATIENT_IDENTIFIERS).append("' ");
		sqlBuilder.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type and et.uuid ='")
				.append(PMTCT_CHILD_FOLLOW_UP_ENCOUNTER_TYPE).append("' ");
		sqlBuilder.append(getObsAnswerName(PMTCT_RAPID_ANTIBODY_RESULT, "rapid_result"));
		sqlBuilder.append(" where ob.concept_id = ").append(conceptQuery(PMTCT_FOLLOW_UP_DATE))
				.append(" and ob.value_datetime >= :start  and ob.value_datetime <= :end ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setDate("start", start);
		query.setDate("end", end);
		
		List list = query.list();
		List<PMTCTPatientRapidAntiBody> rapidAntiBodyList = new ArrayList<>();
		for (Object objects : list) {
			Object[] object = (Object[]) objects;
			rapidAntiBodyList.add(new PMTCTPatientRapidAntiBody(
					(int) object[0],
					(String) object[1],
					(String) object[2],
					(String) object[3],
					EthiOhriUtil.getAgeInMonth((Date) object[4], (Date) object[5]),
					(Date) object[5],
					(String) object[6]
			
			));
		}
		
		return rapidAntiBodyList;
	}

	public HashMap<Integer, Object> getValueFromHEIEnrollment(String concept, List<Integer> person) {
		StringBuilder sqlBuilder = new StringBuilder("select ob.person_id, cn.name from obs as ob ");
		sqlBuilder.append(" inner join concept_name as cn on cn.concept_id = ob.value_coded ");
		sqlBuilder.append(" inner join encounter as e on e.encounter_id = ob.encounter_id ");
		sqlBuilder.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type and et.uuid='")
				.append(PMTCT_CHILD_ENROLLMENT_ENCOUNTER_TYPE).append("' ");
		sqlBuilder.append(" where ob.concept_id = ").append(conceptQuery(concept));
		sqlBuilder.append(" and ob.person_id in (:personId)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("personId", person);
		HashMap<Integer, Object> hashMap = new HashMap<>();
		List list = query.list();
		for (Object object : list) {
			Object[] objects = (Object[]) object;
			hashMap.put((Integer) objects[0], objects[1]);
		}
		
		return hashMap;
	}
	public List getPMTCTHEIPatientEncounterWithAllFields(Date start, Date end) {
		followUpEncounter = encounterQuery.getEncounters(Collections.singletonList(PMTCT_FOLLOW_UP_DATE),null,end,PMTCT_CHILD_FOLLOW_UP_ENCOUNTER_TYPE);
		StringBuilder sqlBuilder = new StringBuilder("select " +
				                                             /*0*/ "distinct p.person_id," +
				                                             /*1*/  "pi.identifier, " +
				                                             /*2*/  "concat(pn.given_name,' ',pn.middle_name,' ',pn.family_name), " +
				                                             /*3*/   "p.gender," +
				                                             /*4*/   "e.encounter_id," +
				                                             /*5*/   "p.birthdate," +
				                                             /*6*/   "heiCode.value_text as heiCode, "+
				                                             /*7*/   "infantReferred.name as infantReferred,"+
				                                             /*8*/    "enrollment.value_datetime as enrollmentDate,"+
				                                             /*9*/    "arv.name as arv,"+
				                                             /*10*/   "immunization.name as immunization, "+
				                                             /*11*/   "motherIntervention.name as intervention,"+
				                                             /*12*/   "followUpDate.value_datetime as followUpDate,"+
				                                             /*13*/   "birthWeight.value_numeric as birthWeight, "+
				                                             /*14*/   "followUpWeight.value_numeric as followUpWeight,"+
				                                             /*15*/   "growthPattern.name as growthPattern,"+
				                                             /*16*/   "growthFailed.name as growthFailed,"+
				                                             /*17*/   "developMilestone.name as developMilestone,"+
				                                             /*18*/   "reasonForRedFlag.name as reasonForRedFlag,"+
				                                             /*19*/   "feedingPractice.name as feedingPractice,"+
				                                             /*20*/   "breastCondition.name as breastCondition,"+
				                                             /*21*/   "rapidAntibody.name as rapidAntibody,"+
				                                             /*22*/   "dnaPcrResult.name as dnaPcrResult,"+
				                                             /*23*/   "sampleCollectionDate.value_datetime as sampleCollectionDate,"+
				                                             /*24*/   "finalOutComeDate.value_datetime as finalOutComeDate, "+
				                                             /*25*/   "finalOutCome.name as finalOutCome,"+
				                                             /*26*/   "conclusion.name as conclusion,"+
				                                             /*27*/   "decision.name as decision, "+
				                                             /*28*/	 "dose.name as dose"+
				                                             " from obs as ob ");
		sqlBuilder.append(" inner join person as p on p.person_id = ob.person_id  ");
		sqlBuilder.append(" inner join person_name as  pn on pn.person_id =ob.person_id ");
		sqlBuilder.append(" inner join patient_identifier as pi on pi.patient_id = p.person_id ");
		sqlBuilder.append(" inner join patient_identifier_type as pit on pit.patient_identifier_type_id = pi.identifier_type and pi.uuid ='").append(MRN_PATIENT_IDENTIFIERS).append("' ");
		sqlBuilder.append(" inner join encounter as e on e.encounter_id = ob.encounter_id ");
		sqlBuilder.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sqlBuilder.append(getQuery(PMTCT_CHILD_ENROLLMENT_DATE, "enrollment", PMTCT_CHILD_ENROLLMENT_ENCOUNTER_TYPE));
		sqlBuilder.append(getQuery(PMTCT_HEI_CODE, "heiCode", PMTCT_CHILD_ENROLLMENT_ENCOUNTER_TYPE));
		sqlBuilder.append(getQuery(PMTCT_INFANT_REFERRED_UUID, "infantReferred", PMTCT_CHILD_ENROLLMENT_ENCOUNTER_TYPE));
		sqlBuilder.append(getObsAnswerName(PMTCT_ARV_PROPHYLAXIS, "arv"));
		sqlBuilder.append(getObsAnswerName(WEIGHT, "followUpWeight"));
		sqlBuilder.append(getQuery(PMTCT_IMMUNIZATION, "immunization", PMTCT_IMMUNIZATION_ENCOUNTER_TYPE));
		sqlBuilder.append(getQuery(PMTCT_MOTHER_INTERVENTION, "motherIntervention", PMTCT_CHILD_ENROLLMENT_ENCOUNTER_TYPE));
		sqlBuilder.append(getQuery(PMTCT_BIRTH_WEIGHT, "birthWeight", PMTCT_CHILD_ENROLLMENT_ENCOUNTER_TYPE));
		sqlBuilder.append(getObsAnswerDate(FOLLOW_UP_DATE, "followUpDate"));
		sqlBuilder.append(getObsAnswerName(PMTCT_GROWTH_PATTERN, "growthPattern"));
		sqlBuilder.append(getObsAnswerName(PMTCT_GROWTH_FAILER, "growthFailed"));
		sqlBuilder.append(getObsAnswerName(PMTCT_DEVELOPMENT_MILESTONE, "developMilestone"));
		sqlBuilder.append(getObsAnswerName(PMTCT_REASON_FOR_RED_FLAG, "reasonForRedFlag"));
		sqlBuilder.append(getObsAnswerName(PMTCT_FEEDING_PRACTICE, "feedingPractice"));
		sqlBuilder.append(getObsAnswerName(PMTCT_BREEST_CONDITION, "breastCondition"));
		sqlBuilder.append(getObsAnswerName(PMTCT_RAPID_ANTIBODY_RESULT, "rapidAntibody"));
		sqlBuilder.append(getObsAnswerName(PMTCT_DNA_PCR_RESULT, "dnaPcrResult"));
		sqlBuilder.append(getObsAnswerName(PMTCT_DOSE,"dose"));
		sqlBuilder.append(getObsAnswerDate(PMTCT_SPECIMEN_COLLECTION_DATE, "sampleCollectionDate"));
		sqlBuilder.append(getQuery(FINAL_OUT_COME_DATE, "finalOutComeDate", PMTCT_FINAL_OUT_COME_ENCOUNTER_TYPE));
		sqlBuilder.append(getObsAnswerName(PMTCT_CONCLUSION, "conclusion"));
		sqlBuilder.append(getObsAnswerName(PMTCT_DESCISION, "decision"));
		sqlBuilder.append(getQuery(PMTCT_FINAL_OUT_COME, "finalOutCome", PMTCT_FINAL_OUT_COME_ENCOUNTER_TYPE));
		
		sqlBuilder.append("and et.uuid= '").append(PMTCT_CHILD_FOLLOW_UP_ENCOUNTER_TYPE).append("' ");
		sqlBuilder.append(" where ob.concept_id = ").append(conceptQuery(PMTCT_SAMPLE_COLLECTION_DATE));
		sqlBuilder.append(" and ob.encounter_id in (:encounterId) ");
		if (start != null) sqlBuilder.append(" and ob.value_datetime >= :start ");
		
		if (end != null) sqlBuilder.append(" and ob.value_datetime <= :end ");
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		q.setParameterList("encounterId",followUpEncounter);
		
		if (start != null) q.setDate("start", start);
		
		if (end != null) q.setDate("end", end);
		
		List list = q.list();
		
		return list;
	}
	
	private HashMap<Integer, PMTCTPatient> getPMTCTPatientEncounter(Date start, Date end, String conceptUUID,List<String> testTypesConcepts) {
		
		StringBuilder sqlBuilder = new StringBuilder("select  p.person_id, pn.given_name+' '+pn.middle_name+' '+pn.family_name,p.gender,e.encounter_id,p.birthdate,ob.value_datetime,testInd.uuid,result.uuid as result,finalOutcomeResult.uuid as finalOutCome from obs as ob ");
		sqlBuilder.append(" inner join person as p on p.person_id = ob.person_id  ");
		sqlBuilder.append(" inner join person_name as pn on pn.person_id = ob.person_id ");
		sqlBuilder.append(" inner join encounter as e on e.encounter_id = ob.encounter_id ");
		sqlBuilder.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sqlBuilder.append(" inner join ");
		sqlBuilder.append(" (select tob.encounter_id,tob.person_id, c.uuid from obs as tob inner join concept as c on c.concept_id =tob.value_coded  where tob.concept_id = ")
				.append(conceptQuery(PMTCT_TEST_INDICATION)).append(" and tob.value_coded in ")
				.append(conceptQuery(testTypesConcepts)).append(") as testInd ");
		sqlBuilder.append("  on testInd.encounter_id = ob.encounter_id and testInd.person_id = ob.person_id");
		sqlBuilder.append(" and et.uuid= '").append(PMTCT_CHILD_FOLLOW_UP_ENCOUNTER_TYPE).append("' ");
		sqlBuilder.append(getObsAnswerName(PMTCT_DNA_PCR_RESULT, "result"));
		sqlBuilder.append(getObsFromFinalOutComeAnswerName(PMTCT_FINAL_OUT_COME, "finalOutcomeResult"));
		sqlBuilder.append(" where ob.concept_id = ").append(conceptQuery(conceptUUID));
		
		if (start != null) sqlBuilder.append(" and ob.value_datetime >= :start ");
		
		if (end != null) sqlBuilder.append(" and ob.value_datetime <= :end ");
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		if (start != null) q.setDate("start", start);
		
		if (end != null) q.setDate("end", end);
		
		List list = q.list();
		HashMap<Integer, PMTCTPatient> patientEncounters = new HashMap<>();
		Object[] objects;
		int personId = 0;
		int encounterId = 0;
		int age = 0;
		Date birthDate;
		Date sampleDate;
		String testType, resultPncr, finalOutcomeResult;
		for (Object object : list) {
			objects = (Object[]) object;
			personId = (Integer) objects[0];
			birthDate = (Date) objects[2];
			encounterId = (Integer) objects[1];
			sampleDate = (Date) objects[3];
			testType = (String) objects[4];
			resultPncr = (String) objects[5];
			finalOutcomeResult = (String) objects[6];
			age = EthiOhriUtil.getAgeInMonth(birthDate, sampleDate);
			
			if (age <= MONTHS_IN_A_YEAR) {
				PMTCTPatient patientEncounter = patientEncounters.get(personId);
				
				if (!Objects.nonNull(patientEncounter)) {
					patientEncounter = new PMTCTPatient(personId, birthDate);
				}
				if (testType.equals(PMTCT_INITIAL_TEST) && patientEncounter.getEncounterList().stream().noneMatch(p -> p.getTestType().equals(PMTCT_INITIAL_TEST))) {
					patientEncounter.addEncounter(new PMTCTEncounter(sampleDate, encounterId, testType, age, resultPncr));
				} else {
					patientEncounter.addEncounter(new PMTCTEncounter(sampleDate, encounterId, testType, age, resultPncr));
					
				}
				
			}
			
		}
		return patientEncounters;
	}
	
	private HashMap<Integer, PMTCTPatient> getPMTCTPatientEncounterWithAllFields(Date start, Date end) {
		
		StringBuilder sqlBuilder = new StringBuilder("select distinct p.person_id," +
				                                             "pi.identifier, " +
				                                             "concat(pn.given_name,' ',pn.middle_name,' ',pn.family_name), " +
				                                             "p.gender," +
				                                             "e.encounter_id," +
				                                             "p.birthdate," +
				                                             "ob.value_datetime as sample_date," +
				                                             "follow_date.value_datetime as follow_up, " +
				                                             "indication.name as indication," +
				                                             "arv.name as arv," +
				                                             "mt_status.name as mt_status," +
				                                             "specimen_type.name as specimen_type, " +
				                                             "dna_pcr_result.name as dna_pcr, " +
				                                             "dbs_result_date.value_datetime as dbs_result_date, " +
				                                             "dbs_referral.value_datetime as referral, " +
				                                             "regional_name.name as regional_name, " +
				                                             "date_sample_received.value_datetime as sample_received, " +
				                                             "sample_quality.name as quality, " +
				                                             "sample_rejection.name as rejection," +
				                                             "date_test_performed.value_datetime as performed_date, " +
				                                             "platform_used.name as platform," +
				                                             "sample_collection_date.value_datetime as sample_col_Date from obs as ob ");
		sqlBuilder.append(" inner join person as p on p.person_id = ob.person_id  ");
		sqlBuilder.append(" inner join person_name as  pn on pn.person_id =ob.person_id ");
		sqlBuilder.append(" inner join patient_identifier as pi on pi.patient_id = p.person_id ");
		sqlBuilder.append(" inner join patient_identifier_type as pit on pit.patient_identifier_type_id = pi.identifier_type and pi.uuid ='").append(MRN_PATIENT_IDENTIFIERS).append("' ");
		sqlBuilder.append(" inner join encounter as e on e.encounter_id = ob.encounter_id ");
		sqlBuilder.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sqlBuilder.append(getObsAnswerName(PMTCT_TEST_INDICATION, "indication"));
		sqlBuilder.append(getObsAnswerName(PMTCT_ARV_PROPHYLAXIS, "arv"));
		sqlBuilder.append(getObsAnswerName(PMTCT_MATERNAL_ART_STATUS, "mt_status "));
		sqlBuilder.append(getObsAnswerName(PMTCT_SPECIMENT_TYPE, "specimen_type"));
		sqlBuilder.append(getObsAnswerDate(PMTCT_FOLLOW_UP_DATE, "follow_date"));
		sqlBuilder.append(getObsAnswerName(PMTCT_DNA_PCR_RESULT, "dna_pcr_result"));
		sqlBuilder.append(getObsAnswerDate(PMTCT_DBS_RESULT_RECEIVED_DATE, "dbs_result_date"));
		sqlBuilder.append(getObsAnswerDate(PMTCT_DBS_REFERRAL_TO_REGIONAL_LAB_DATE, "dbs_referral"));
		sqlBuilder.append(getObsAnswerDate(PMTCT_SPECIMEN_COLLECTION_DATE, "sample_collection_date"));
		sqlBuilder.append(getObsAnswerName(PMTCT_NAME_OF_REGIONAL_LAB_NAME, "regional_name"));
		sqlBuilder.append(getObsAnswerDate(PMTCT_DATE_OF_SAMPLE_RECEIVED_BY_LAB, "date_sample_received"));
		sqlBuilder.append(getObsAnswerName(PMTCT_SAMPLE_QUALITY, "sample_quality"));
		sqlBuilder.append(getObsAnswerName(PMTCT_REASON_FOR_SAMPLE_REJECTION, "sample_rejection"));
		sqlBuilder.append(getObsAnswerDate(PMTCT_DATE_TEST_PERFORMED_BY_LAB, "date_test_performed"));
		sqlBuilder.append(getObsAnswerName(PMTCT_PLATFORM_USED, "platform_used"));
		
		sqlBuilder.append("and et.uuid= '").append(PMTCT_CHILD_FOLLOW_UP_ENCOUNTER_TYPE).append("' ");
		sqlBuilder.append(" where ob.concept_id = ").append(conceptQuery(PMTCT_SAMPLE_COLLECTION_DATE));
		
		if (start != null) sqlBuilder.append(" and ob.value_datetime >= :start ");
		
		if (end != null) sqlBuilder.append(" and ob.value_datetime <= :end ");
		
		Query q = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		
		if (start != null) q.setDate("start", start);
		
		if (end != null) q.setDate("end", end);
		
		List list = q.list();
		HashMap<Integer, PMTCTPatient> patientEncounters = new HashMap<>();
		Object[] objects;
		int personId = 0;
		int encounterId = 0;
		int age = 0;
		Date birthDate;
		Date sampleDate, followUpDate, dbsResultDate, dateSampleReceivedDate, sampleCollectionDate, dateTestPerformed, referralDate;
		String testType, mrn, fullName, gender, indication, arv, maternalStatus, specimenType, dnaPcr, regionalName, quality, rejection, platform;
		for (Object object : list) {
			objects = (Object[]) object;
			birthDate = (Date) objects[5];
			sampleDate = (Date) objects[6];
			
			age = EthiOhriUtil.getAgeInMonth(birthDate, sampleDate);
			
			if (age <= MONTHS_IN_A_YEAR) {
				personId = (Integer) objects[0];
				followUpDate = (Date) objects[7];
				dbsResultDate = (Date) objects[13];
				dateSampleReceivedDate = (Date) objects[16];
				dateTestPerformed = (Date) objects[19];
				mrn = (String) objects[1];
				fullName = (String) objects[2];
				gender = (String) objects[3];
				indication = (String) objects[8];
				arv = (String) objects[9];
				maternalStatus = (String) objects[10];
				specimenType = (String) objects[11];
				dnaPcr = (String) objects[12];
				referralDate = (Date) objects[14];
				regionalName = (String) objects[15];
				quality = (String) objects[17];
				rejection = (String) objects[18];
				platform = (String) objects[20];
				sampleCollectionDate = (Date) objects[21];
				testType = (String) objects[4];
				encounterId = (int) objects[1];
				
				PMTCTPatient patientEncounter = patientEncounters.get(personId);
				
				if (Objects.isNull(patientEncounter)) {
					patientEncounter = new PMTCTPatient(
							fullName,
							gender,
							"heiCode",
							mrn,
							new Date(),
							personId,
							birthDate
					);
				}
				patientEncounter.addEncounter(new PMTCTEncounter(
						sampleDate,
						followUpDate,
						encounterId,
						testType,
						arv,
						maternalStatus,
						indication,
						specimenType,
						sampleCollectionDate,
						dnaPcr,
						dbsResultDate,
						referralDate,
						regionalName,
						dateSampleReceivedDate,
						quality,
						rejection,
						dateTestPerformed,
						platform,
						age
				));
				
			}
			
		}
		
		return patientEncounters;
	}
	
	
	private StringBuilder getObsAnswerName(String conceptUUid, String alias) {
		StringBuilder sqlBuilder = new StringBuilder(
				" left join (select tob.encounter_id,tob.person_id, c.name,c.uuid,tob.value_text,tob.value_numeric from obs as tob inner join concept_name as c on c.concept_id =tob.value_coded  where tob.concept_id = ");
					if(!followUpEncounter.isEmpty()){
						sqlBuilder.append(" and tob.encounter_id in (:encounterId) ");
					}
				                           sqlBuilder.append(conceptQuery(conceptUUid)).append(" ) as ").append(alias).append(" on ");
		sqlBuilder.append(alias).append(".encounter_id = ob.encounter_id and ").append(alias)
				.append(".person_id = ob.person_id ");
		
		return sqlBuilder;
	}
	
	private StringBuilder getQuery(String conceptUUid, String alias, String encounterUUID) {
		StringBuilder sqlBuilder = new StringBuilder(
				" left join (select tob.encounter_id,tob.person_id, c.name,c.uuid,tob.value_datetime,tob.value_text,tob.value_numeric from obs as tob inner join concept_name as c on c.concept_id =tob.value_coded");
		sqlBuilder
				.append(" inner join encounter as e on e.encounter_id = tob.encounter_id inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sqlBuilder.append(" and et.uuid ='").append(encounterUUID).append("' ");
		sqlBuilder.append(" where tob.concept_id = ").append(conceptQuery(conceptUUid));
		if(!followUpEncounter.isEmpty()){
			sqlBuilder.append("tob.encounter_id in (:encounterId) ");
		}
				sqlBuilder.append(" ) as ").append(alias)
				.append(" on ");
		sqlBuilder.append(alias).append(".encounter_id = ob.encounter_id and ").append(alias)
				.append(".person_id = ob.person_id ");
		return sqlBuilder;
	}
	
	private StringBuilder getObsFromFinalOutComeAnswerName(String conceptUUid, String alias) {
		StringBuilder sqlBuilder = new StringBuilder(
				" left join (select tob.encounter_id,tob.person_id, c.name,c.uuid from obs as tob inner join concept_name as c on c.concept_id =tob.value_coded");
		sqlBuilder
				.append(" inner join encounter as e on e.encounter_id = tob.encounter_id inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		sqlBuilder.append(" and et.uuid ='").append(PMTCT_FINAL_OUT_COME_ENCOUNTER_TYPE).append("' ");
		sqlBuilder.append(" where tob.concept_id = ").append(conceptQuery(conceptUUid));
		
		if(!followUpEncounter.isEmpty()){
			sqlBuilder.append(" and tob.encounter_id in (:encounterId) ");
		}
				sqlBuilder.append(" ) as ").append(alias)
				.append(" on ");
		sqlBuilder.append(alias).append(".encounter_id = ob.encounter_id and ").append(alias)
				.append(".person_id = ob.person_id ");
		return sqlBuilder;
	}
	
	private StringBuilder getObsAnswerDate(String conceptUUid, String alias) {
		StringBuilder sqlBuilder = new StringBuilder(
				"left join (select tob.encounter_id,tob.person_id, tob.value_datetime from obs as tob  where tob.concept_id = ")
				                           .append(conceptQuery(conceptUUid));
		if(!followUpEncounter.isEmpty()){
			sqlBuilder.append(" and tob.encounter_id in (:encounterId) ");
		}
				                           sqlBuilder.append(") as ").append(alias).append(" on ");
		sqlBuilder.append(alias).append(".encounter_id = ob.encounter_id and ").append(alias)
				.append(".person_id = ob.person_id ");
		return sqlBuilder;
	}
	
}
