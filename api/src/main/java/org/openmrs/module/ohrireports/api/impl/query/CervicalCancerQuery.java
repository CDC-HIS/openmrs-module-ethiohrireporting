package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn.CxcaScreening;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class CervicalCancerQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	private CxcaScreening firstScreening;
	
	private CxcaScreening reScreening;
	
	private CxcaScreening postScreening;
	
	public CxcaScreening getFirstScreening() {
		return firstScreening;
	}
	
	public void setFirstScreening(CxcaScreening firstScreening) {
		this.firstScreening = firstScreening;
	}
	
	public CxcaScreening getReScreening() {
		return reScreening;
	}
	
	public int getTotalCohortCount() {
		return reScreening.getTotal() + firstScreening.getTotal() + postScreening.getTotal();
		
	}
	
	public void setReScreening(CxcaScreening reScreening) {
		this.reScreening = reScreening;
	}
	
	public CxcaScreening getPostScreening() {
		return postScreening;
	}
	
	public void setPostScreening(CxcaScreening postScreening) {
		this.postScreening = postScreening;
	}
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	private List<Integer> baseEncounter;
	
	private List<Integer> currentEncounter;
	
	private Date startDate;
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(DATE_COUNSELING_GIVEN), startDate, endDate);
		currentEncounter = baseEncounter;
	}
	
	private Date endDate;
	
	@Autowired
	public CervicalCancerQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public Cohort loadScreenedCohort() {
		//baseEncounter = encounterQuery.getEncounters(Arrays.asList(CXC_SCREENING_DATE),getStartDate(),getEndDate());
		//List<Integer> onArtEncounters =  encounterQuery.getAliveFollowUpEncounters(getEndDate());
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(DATE_COUNSELING_GIVEN), getStartDate(), getEndDate());
		
		StringBuilder stringBuilder = baseQuery(DATE_COUNSELING_GIVEN);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseEncounter);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
	public Cohort loadCxCaScreeningForDatim(Date startDate, Date endDate) {
		String stringQuery = "\n" + "select ps.person_id from\n" + "\n"
		        + "(select distinct  ob.person_id from obs as ob inner join  \n" + "\n"
		        + "(select max(ib.value_datetime) as value_datetime,ib.person_id \n"
		        + " from obs as ib  where ib.concept_id = "
		        + conceptQuery(DATE_COUNSELING_GIVEN)
		        + " and ib.value_datetime>= :joinStartDate1 and ib.value_datetime<= :joinEndDate1 \n"
		        + "  group by ib.person_id \n "
		        + "  \n"
		        + " ) as o\n"
		        + "on o.person_id = ob.person_id and  ob.value_datetime = o.value_datetime and ob.concept_id = "
		        + conceptQuery(DATE_COUNSELING_GIVEN)
		        + " ) as ps\n"
		        + "\n"
		        + "inner join \n"
		        + "\n"
		        + "(select ob.encounter_id,ob.person_id from obs as ob \n"
		        + "\n"
		        + "inner join  \n"
		        + "\n"
		        + "(select max(ib.value_datetime) as value_datetime,ib.person_id \n"
		        + " from obs as ib  where ib.concept_id = "
		        + conceptQuery(FOLLOW_UP_DATE)
		        + " and ib.value_datetime>= :joinStartDate2 and ib.value_datetime<= :joinEndDate2 \n"
		        + "  group by ib.person_id \n"
		        + "  \n"
		        + " ) as o\n"
		        + "on o.person_id = ob.person_id and  ob.value_datetime = o.value_datetime and ob.concept_id = "
		        + conceptQuery(FOLLOW_UP_DATE)
		        + ") as d \n"
		        + "on ps.person_id = d.person_id\n"
		        + "\n"
		        + "inner join \n"
		        + "\n"
		        + "(select ob.person_id,ob.value_coded,ob.encounter_id from obs as ob\n"
		        + " where ob.concept_id = "
		        + conceptQuery(FOLLOW_UP_STATUS)
		        + " and ob.value_coded in "
		        + conceptQuery(Arrays.asList(ALIVE, RESTART))
		        + " ) as fs \n"
		        + "on fs.person_id = d.person_id and d.encounter_id = fs.encounter_id "
		        + "inner join\n"
		        + "\n"
		        + "(select person_id \n"
		        + "from obs where concept_id = "
		        + conceptQuery(CXCA_SCREENING_DONE)
		        + "and value_coded = "
		        + conceptQuery(CXCA_SCREENING_DONE_YES)
		        + ") as cxcasd on cxcasd.person_id = d.person_id";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		
		query.setDate("joinEndDate1", endDate);
		query.setDate("joinStartDate1", startDate);
		query.setDate("joinEndDate2", endDate);
		query.setDate("joinStartDate2", startDate);
		
		return new Cohort(query.list());
		
	}
	
	public Cohort getByScreeningType(String typConceptUUiD) {
		String stringQuery = "SELECT ps.person_id\n" + "FROM\n" + "  (SELECT DISTINCT ob.person_id\n"
		        + "   FROM obs AS ob\n" + "     INNER JOIN\n"
		        + "     (SELECT MAX(ib.value_datetime) AS value_datetime, ib.person_id\n" + "      FROM obs AS ib\n"
		        + "      WHERE ib.concept_id = " + conceptQuery(DATE_COUNSELING_GIVEN)
		        + "        AND ib.value_datetime >= :joinEndDate1\n" + "        AND ib.value_datetime <= :joinEndDate2\n"
		        + "      GROUP BY ib.person_id\n" + "     ) AS o\n" + "     ON o.person_id = ob.person_id\n"
		        + "        AND ob.value_datetime = o.value_datetime\n" + "        AND ob.concept_id = "
		        + conceptQuery(DATE_COUNSELING_GIVEN) + "INNER JOIN\n" + "    (select distinct person_id \n"
		        + "		from obs \n" + "        where concept_id = " + conceptQuery(CXCA_TYPE_OF_SCREENING)
		        + "and value_coded = " + conceptQuery(typConceptUUiD) + ") AS tos\n" + "ON tos.person_id = ob.person_id"
		        + "  ) AS ps";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setDate("joinEndDate1", startDate);
		query.setDate("joinEndDate2", endDate);
		return new Cohort(query.list());
	}
	
	public Cohort getNegativeResult(Cohort cohort) {
		String stringQuery = "SELECT cxcaN.person_id FROM (" + "SELECT distinct hpvn.person_id\n" + "FROM obs as hpvn \n"
		        + "where hpvn.concept_id ="
		        + conceptQuery(HPV_DNA_SCREENING_RESULT)
		        + " and "
		        + "hpvn.value_coded = "
		        + conceptQuery(NEGATIVE)
		        + " and hpvn.encounter_id in (:baseEncounter)"
		        + " UNION "
		        + "SELECT distinct person_id\n"
		        + "FROM obs\n"
		        + "WHERE \n"
		        + "  (concept_id = "
		        + conceptQuery(HPV_DNA_SCREENING_RESULT)
		        + "AND value_coded = "
		        + conceptQuery(POSITIVE)
		        + ") AND \n"
		        + "  (\n"
		        + "    (concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT)
		        + " AND value_coded = "
		        + conceptQuery(VIA_NEGATIVE)
		        + ") OR\n"
		        + "    (concept_id = "
		        + conceptQuery(CYTOLOGY_RESULT)
		        + " AND (value_coded = "
		        + conceptQuery(CYTOLOGY_NEGATIVE)
		        + " OR "
		        + "value_coded = "
		        + conceptQuery(CYTOLOGY_ASCUS)
		        + ")) OR\n"
		        + "    (concept_id = "
		        + conceptQuery(COLPOSCOPY_EXAM_FINDING)
		        + " AND value_coded = "
		        + conceptQuery(NORMAL)
		        + ")\n"
		        + "  )  and encounter_id in ( :baseEncounter) and person_id in (:personIdList)\n"
		        + "  \n"
		        + "UNION\n"
		        + "\n"
		        + "SELECT distinct person_id\n"
		        + "FROM obs\n"
		        + "WHERE concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT)
		        + " AND value_coded = "
		        + conceptQuery(VIA_NEGATIVE)
		        + "and encounter_id in ( :baseEncounter) and person_id in (:personIdList)\n"
		        + "\n"
		        + "UNION\n"
		        + "\n"
		        + "SELECT distinct person_id \n"
		        + "FROM obs\n"
		        + "WHERE concept_id ="
		        + conceptQuery(CYTOLOGY_RESULT)
		        + " AND \n"
		        + "\t(\t\n"
		        + "\t\tvalue_coded = "
		        + conceptQuery(CYTOLOGY_NEGATIVE)
		        + " OR \n"
		        + "        value_coded = "
		        + conceptQuery(CYTOLOGY_ASCUS)
		        + " OR \n"
		        + "        (\n"
		        + "\t\t\t-- Cytology Result = >Ascus and Colposcopy = Normal\n"
		        + "    value_coded = "
		        + conceptQuery(CYTOLOGY_GREATER_ASCUS_SUSPICIOUS)
		        + " AND \n"
		        + "    (concept_id = "
		        + conceptQuery(COLPOSCOPY_EXAM_FINDING)
		        + " AND value_coded = "
		        + conceptQuery(NORMAL)
		        + " )\n"
		        + "\t\t)\n"
		        + "    ) and encounter_id in ( :baseEncounter) and person_id in (:personIdList)\n" + ") as cxcaN\n ";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getPositiveResult(Cohort cohort) {
		String stringQuery = "SELECT distinct person_id\n" + "FROM (\n" + "SELECT distinct person_id\n" + "FROM obs\n"
		        + "WHERE \n" + "  (concept_id = "
		        + conceptQuery(HPV_DNA_SCREENING_RESULT)
		        + " AND "
		        + " value_coded = "
		        + conceptQuery(POSITIVE)
		        + ") AND \n"
		        + "    (concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT)
		        + " AND "
		        + " (value_coded = "
		        + conceptQuery(VIA_POSITIVE_ELIGIBLE_FOR_CRYO)
		        + " OR value_coded = "
		        + conceptQuery(VIA_POSITIVE_NON_ELIGIBLE_FOR_CRYO)
		        + "))  OR\n"
		        + "    ((concept_id = "
		        + conceptQuery(CYTOLOGY_RESULT)
		        + " AND value_coded = "
		        + conceptQuery(CYTOLOGY_ASCUS)
		        + ") "
		        + " AND (concept_id = "
		        + conceptQuery(COLPOSCOPY_EXAM_FINDING)
		        + " "
		        + " AND (value_coded = "
		        + conceptQuery(COLPOSCOPY_LOW_GRADE_SIL)
		        + " OR "
		        + " value_coded = "
		        + conceptQuery(COLPOSCOPY_HIGH_GRADE_SIL)
		        + " ))) \n"
		        + "   and encounter_id in ( :baseEncounter) and person_id in (:personIdList)\n"
		        + "  \n"
		        + "UNION\n"
		        + "\n"
		        + "SELECT distinct person_id\n"
		        + "FROM obs\n"
		        + "WHERE (concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT)
		        + " "
		        + " AND (value_coded = "
		        + conceptQuery(VIA_POSITIVE_ELIGIBLE_FOR_CRYO)
		        + " "
		        + " OR value_coded = "
		        + conceptQuery(VIA_POSITIVE_NON_ELIGIBLE_FOR_CRYO)
		        + ")) \n"
		        + "\tand encounter_id in ( :baseEncounter) and person_id in (:personIdList)\n"
		        + "\n"
		        + "UNION\n"
		        + "\n"
		        + "SELECT distinct person_id\n"
		        + "FROM obs\n"
		        + "WHERE (concept_id = "
		        + conceptQuery(CYTOLOGY_RESULT)
		        + " "
		        + "AND value_coded = "
		        + conceptQuery(CYTOLOGY_ASCUS)
		        + ") "
		        + "AND (concept_id = "
		        + conceptQuery(COLPOSCOPY_EXAM_FINDING)
		        + " "
		        + "AND (value_coded = "
		        + conceptQuery(COLPOSCOPY_LOW_GRADE_SIL)
		        + " OR "
		        + "value_coded = "
		        + conceptQuery(COLPOSCOPY_LOW_GRADE_SIL)
		        + " ))\n"
		        + "\t\tand encounter_id in ( :baseEncounter) and person_id in (:personIdList)) cxcaP;\n";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getSuspectedResult(Cohort cohort) {
		String stringQuery = "Select distinct person_id\n" + "from obs\n" + "WHERE concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT) + " AND\n" + "value_coded = " + conceptQuery(VIA_SUSPICIOUS_RESULT)
		        + " and encounter_id in ( :baseEncounter) and person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public List<Integer> getCurrentEncounter() {
		return currentEncounter;
	}
	
}
