package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_treatment.CxCaTreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.CXCA_TREATMENT_PRECANCEROUS_LESIONS;

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
	}
	
	public void generateBaseReport() {
		//reset already counted cohort
		alreadyCountedCohort = new Cohort();

		baseEncounter = encounterQuery.getEncountersByFollowUp(EncounterType.HTS_FOLLOW_UP_ENCOUNTER_TYPE,
				CXCA_TREATMENT_PRECANCEROUS_LESIONS, Arrays.asList(ConceptAnswer.CXCA_TREATMENT_TYPE_THERMOCOAGULATION,
						ConceptAnswer.CXCA_TREATMENT_TYPE_LEEP,FollowUpConceptQuestions.CXCA_TREATMENT_TYPE_CRYOTHERAPY,
						ConceptAnswer.OTHER));

		List<Integer> treatmentEncounter = encounterQuery.getEncounters(
		Arrays.asList(FollowUpConceptQuestions.CXCA_TREATMENT_STARTING_DATE),startDate, endDate,baseEncounter);
		baseCohort = getCohort(treatmentEncounter);

		List<Integer> treatmentEncounterFollowup =encounterQuery.getEncountersByExcludingCohort(Arrays.asList(FollowUpConceptQuestions.FOLLOW_UP_DATE),
				baseEncounter,startDate, endDate,baseCohort);
		baseEncounter.addAll(treatmentEncounterFollowup);


		//removing duplicates
		baseEncounter = new ArrayList<>(new HashSet<>(treatmentEncounter));

		baseCohort = getCohort(baseEncounter);

	}
	
	/*
		public Cohort getByScreeningType(String typConceptUUiD) {
			String stringQuery = "SELECT distinct ps.person_id FROM (SELECT DISTINCT ob.person_id\n" + "   FROM obs AS ob\n"
			        + "     INNER JOIN\n" + "     (SELECT MAX(ib.value_datetime) AS value_datetime, ib.person_id\n"
			        + "      FROM obs AS ib\n" + "      WHERE (\n" + "			CASE \n" + " 			WHEN (ib.concept_id ="
			        + conceptQuery(FollowUpConceptQuestions.CXCA_TREATMENT_STARTING_DATE)
			        + " AND\n"
			        + "				ib.value_datetime IS NOT NULL) THEN\n"
			        + "                            ib.concept_id ="
			        + conceptQuery(FollowUpConceptQuestions.CXCA_TREATMENT_STARTING_DATE)
			        + " AND\n"
			        + "                            ib.value_datetime >= :joinStartDate1 AND ib.value_datetime <= :joinEndDate1\n"
			        + "			WHEN (ib.concept_id ="
			        + conceptQuery(FollowUpConceptQuestions.CXCA_TREATMENT_STARTING_DATE)
			        + " AND\n"
			        + "				ib.value_datetime IS NULL) THEN\n"
			        + "                            ib.concept_id ="
			        + conceptQuery(FollowUpConceptQuestions.FOLLOW_UP_DATE)
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
			        + conceptQuery(FollowUpConceptQuestions.CXCA_TYPE_OF_SCREENING)
			        + "        and value_coded = "
			        + conceptQuery(typConceptUUiD) + ") AS tos\n" + "ON tos.person_id = ob.person_id ";
			
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
	*/
	
	public Cohort getCohortByTreatmentType(String typConceptUUiD, Cohort cohort) {
		String stringQuery = "select distinct  person_id\n" + "from obs\n" + "where concept_id = "
		        + conceptQuery(CXCA_TREATMENT_PRECANCEROUS_LESIONS) + "and value_coded = " + conceptQuery(typConceptUUiD)
		        + "and encounter_id in (:baseEncounter)" + "and person_id in (:personIdList)";
		
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
	
	public Cohort getByScreeningType(String conceptUuId) {
		String stringQuery = "SELECT distinct ob.person_id FROM obs as ob where ob.concept_id="
		        + conceptQuery(FollowUpConceptQuestions.CXCA_TYPE_OF_SCREENING) + " and ob.value_coded="
		        + conceptQuery(conceptUuId) + " and ob.encounter_id in (:encounter)  and  ob.person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		
		query.setParameterList("encounter", baseEncounter);
		query.setParameterList("personIdList", baseCohort.getMemberIds());
		
		return new Cohort(query.list());
	}
}
