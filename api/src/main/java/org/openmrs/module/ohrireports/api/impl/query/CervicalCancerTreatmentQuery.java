package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_treatment.CxCaTreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class CervicalCancerTreatmentQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	private CxCaTreatment firstScreening;
	
	private CxCaTreatment reScreening;
	
	private CxCaTreatment postScreening;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	private List<Integer> baseEncounter;
	
	private Cohort alreadyCountedCohort;
	
	private Date startDate;
	
	private Date endDate;
	
	public CervicalCancerTreatmentQuery(DbSessionFactory _sessionFactory) {
		sessionFactory = _sessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public CxCaTreatment getFirstScreening() {
		return firstScreening;
	}
	
	public void setFirstScreening(CxCaTreatment firstScreening) {
		this.firstScreening = firstScreening;
	}
	
	public CxCaTreatment getReScreening() {
		return reScreening;
	}
	
	public void setReScreening(CxCaTreatment reScreening) {
		this.reScreening = reScreening;
	}
	
	public CxCaTreatment getPostScreening() {
		return postScreening;
	}
	
	public void setPostScreening(CxCaTreatment postScreening) {
		this.postScreening = postScreening;
	}
	
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
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(CXCA_TREATMENT_STARTING_DATE, FOLLOW_UP_DATE), startDate,
		    endDate);
		baseEncounter = refineBaseEncounter();
	}
	
	private List<Integer> refineBaseEncounter() {
		List<String> conceptUUIDs = Arrays.asList(CXCA_TREATMENT_TYPE_THERMOCOAGULATION, CXCA_TREATMENT_TYPE_LEEP,
		    CXCA_TREATMENT_TYPE_CRYOTHERAPY);
		StringBuilder stringQuery = new StringBuilder("select distinct ob.encounter_id from obs as ob ");
		stringQuery.append(" where ob.encounter_id in (:baseEncounter) ");
		stringQuery.append(" and ob.concept_id = ").append(conceptQuery(CXCA_TREATMENT_PRECANCEROUS_LESIONS));
		stringQuery.append(" and ob.value_coded in ").append(conceptQuery(conceptUUIDs));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery.toString());
		
		query.setParameterList("baseEncounter", baseEncounter);
		List<Integer> response = (List<Integer>) query.list();
		return response;
	}
	
	public Cohort getByScreeningType(String typConceptUUiD) {
		String stringQuery = "SELECT distinct ps.person_id FROM (SELECT DISTINCT ob.person_id\n" + "   FROM obs AS ob\n"
		        + "     INNER JOIN\n" + "     (SELECT MAX(ib.value_datetime) AS value_datetime, ib.person_id\n"
		        + "      FROM obs AS ib\n" + "      WHERE (\n" + "			CASE \n" + " 			WHEN (ib.concept_id ="
		        + conceptQuery(CXCA_TREATMENT_STARTING_DATE)
		        + " AND\n"
		        + "				ib.value_datetime IS NOT NULL) THEN\n"
		        + "                            ib.concept_id ="
		        + conceptQuery(CXCA_TREATMENT_STARTING_DATE)
		        + " AND\n"
		        + "                            ib.value_datetime >= :joinStartDate1 AND ib.value_datetime <= :joinEndDate1\n"
		        + "			WHEN (ib.concept_id ="
		        + conceptQuery(CXCA_TREATMENT_STARTING_DATE)
		        + " AND\n"
		        + "				ib.value_datetime IS NULL) THEN\n"
		        + "                            ib.concept_id ="
		        + conceptQuery(FOLLOW_UP_DATE)
		        + " AND\n"
		        + "                            ib.value_datetime >= :joinStartDate2 AND ib.value_datetime <= :joinEndDate2\n"
		        + "			END\n"
		        + "            )\n"
		        + "      GROUP BY ib.person_id\n"
		        + "     ) AS o\n"
		        + "     ON o.person_id = ob.person_id\n"
		        + "              \n"
		        + "        INNER JOIN\n"
		        + "    (select distinct person_id \n"
		        + "\t\tfrom obs \n"
		        + "        where concept_id ="
		        + conceptQuery(CXCA_TYPE_OF_SCREENING)
		        + "        and value_coded = "
		        + conceptQuery(typConceptUUiD)
		        + ") AS tos\n" + "ON tos.person_id = ob.person_id ";
		
		if (Objects.nonNull(alreadyCountedCohort) && !alreadyCountedCohort.isEmpty()) {
			stringQuery = stringQuery + " and ob.person_id not in (:alreadyCounted) ";
		}
		stringQuery = stringQuery + " ) AS ps ";
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setDate("joinStartDate1", startDate);
		query.setDate("joinStartDate2", startDate);
		query.setDate("joinEndDate1", endDate);
		query.setDate("joinEndDate2", endDate);
		
		if (Objects.nonNull(alreadyCountedCohort) && !alreadyCountedCohort.isEmpty()) {
			query.setParameterList("alreadyCounted", alreadyCountedCohort.getMemberIds());
		}
		
		return new Cohort(query.list());
	}
	
	public Cohort getTreatmentByCryotherapy(Cohort cohort) {
		String stringQuery = "select distinct  person_id\n" + "from obs\n" + "where concept_id = "
		        + conceptQuery(CXCA_TREATMENT_PRECANCEROUS_LESIONS) + "and value_coded = "
		        + conceptQuery(CXCA_TREATMENT_TYPE_CRYOTHERAPY) + "and encounter_id in (:baseEncounter)"
		        + "and person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getTreatmentByLEEP(Cohort cohort) {
		String stringQuery = "select distinct person_id\n" + "from obs\n" + "where concept_id = "
		        + conceptQuery(CXCA_TREATMENT_PRECANCEROUS_LESIONS) + "and value_coded = "
		        + conceptQuery(CXCA_TREATMENT_TYPE_LEEP) + "and encounter_id in (:baseEncounter)"
		        + "and person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getTreatmentByThermocoagulation(Cohort cohort) {
		String stringQuery = "select distinct person_id\n" + "from obs\n" + "where concept_id = "
		        + conceptQuery(CXCA_TREATMENT_PRECANCEROUS_LESIONS) + "and value_coded = "
		        + conceptQuery(CXCA_TREATMENT_TYPE_THERMOCOAGULATION) + "and encounter_id in (:baseEncounter)"
		        + "and person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public int getTotalCohortCount() {
		return reScreening.getTotal() + firstScreening.getTotal() + postScreening.getTotal();
		
	}
	
	public void updateCountedCohort(Cohort countedCohort) {
		if (Objects.isNull(alreadyCountedCohort)) {
			alreadyCountedCohort = new Cohort();
		}
		for (CohortMembership cohortMembership : countedCohort.getMemberships()) {
			alreadyCountedCohort.addMembership(cohortMembership);
		}
	}
}
