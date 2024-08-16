package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn.CxcaScreening;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.*;

@Component
public class CervicalCancerQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	private CxcaScreening firstScreening;
	
	private CxcaScreening reScreening;
	
	private CxcaScreening postScreening;
	
	private void resetCohort() {
		countedCohort = new Cohort();
	}
	
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
	
	public void setCountedCohort(Cohort tobeCountedCohort) {
		this.countedCohort = this.getCohortUnion(countedCohort, tobeCountedCohort);
	}
	
	private Cohort countedCohort;
	
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
		resetCohort();
		this.endDate = endDate;
		baseEncounter = encounterQuery.getEncounters(
		    Arrays.asList(VIA_SCREENING_DATE, HPV_DNA_RESULT_RECEIVED_DATE, CYTOLOGY_RESULT_RECEIVED_DATE), startDate,
		    endDate);
		currentEncounter = baseEncounter;
	}
	
	private Date endDate;
	
	@Autowired
	public CervicalCancerQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
		countedCohort = new Cohort();
	}
	
	public Cohort loadScreenedCohort() {
		
		StringBuilder stringBuilder = baseQuery(FollowUpConceptQuestions.DATE_COUNSELING_GIVEN);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseEncounter);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
	public Cohort getByScreeningType(String typConceptUUiD, List<Integer> encounter) {
		String stringQuery = "SELECT distinct ob.person_id FROM obs as ob where ob.concept_id="
		        + conceptQuery(FollowUpConceptQuestions.CXCA_TYPE_OF_SCREENING) + " and ob.value_coded="
		        + conceptQuery(typConceptUUiD) + " and ob.encounter_id in (:encounter) ";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		
		query.setParameterList("encounter", encounter);
		return new Cohort(query.list());
		
		/*	String stringQuery = "SELECT distinct ps.person_id\n" + "FROM\n" + "  (SELECT DISTINCT ob.person_id\n"
			        + "   FROM obs AS ob\n" + "     INNER JOIN\n"
			        + "     (SELECT MAX(ib.value_datetime) AS value_datetime, ib.person_id\n" + "      FROM obs AS ib\n"
			        + "      WHERE ib.concept_id in "
			        + conceptQuery(Arrays.asList(VIA_SCREENING_DATE, HPV_DNA_RESULT_RECEIVED_DATE))
			        + "        AND ib.value_datetime >= :joinEndDate1\n" + "        AND ib.value_datetime <= :joinEndDate2\n"
			        + "      GROUP BY ib.person_id\n" + "     ) AS o\n" + "     ON o.person_id = ob.person_id\n"
			        + "        AND ob.value_datetime = o.value_datetime\n" + "        AND ob.concept_id in "
			        + conceptQuery(Arrays.asList(VIA_SCREENING_DATE, HPV_DNA_RESULT_RECEIVED_DATE)) + "INNER JOIN\n"
			        + "    (select distinct person_id \n" + "		from obs \n" + "        where concept_id = "
			        + conceptQuery(FollowUpConceptQuestions.CXCA_TYPE_OF_SCREENING) + "and value_coded = "
			        + conceptQuery(typConceptUUiD) + ") AS tos\n" + "ON tos.person_id = ob.person_id";
			if (!countedCohort.isEmpty()) {
				stringQuery = stringQuery + "  and tos.person_id NOT IN (:personIdList) ";
			}
			
			stringQuery = stringQuery + ") AS ps";
			Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
			query.setDate("joinEndDate1", startDate);
			query.setDate("joinEndDate2", endDate);
			if (!countedCohort.getMemberIds().isEmpty()) {
				query.setParameter("personIdList", countedCohort.getMemberIds());
			}
			return new Cohort(query.list());*/
	}
	
	public Cohort getNegativeResult(Cohort cohort) {
		String stringQuery = "SELECT distinct cxcaN.person_id FROM (" + "SELECT distinct hpvn.person_id\n"
		        + "FROM obs as hpvn \n" + "where hpvn.concept_id ="
		        + conceptQuery(FollowUpConceptQuestions.HPV_DNA_SCREENING_RESULT)
		        + " and "
		        + "hpvn.value_coded = "
		        + conceptQuery(ConceptAnswer.NEGATIVE)
		        + " and hpvn.encounter_id in (:baseEncounter)"
		        + " UNION "
		        + "SELECT distinct person_id\n"
		        + "FROM obs\n"
		        + "WHERE \n"
		        + "  (concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.HPV_DNA_SCREENING_RESULT)
		        + "AND value_coded = "
		        + conceptQuery(ConceptAnswer.POSITIVE)
		        + ") AND \n"
		        + "  (\n"
		        + "    (concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT)
		        + " AND value_coded = "
		        + conceptQuery(ConceptAnswer.VIA_NEGATIVE)
		        + ") OR\n"
		        + "    (concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.CYTOLOGY_RESULT)
		        + " AND (value_coded = "
		        + conceptQuery(ConceptAnswer.NON_REACTIVE)
		        + " OR "
		        + "value_coded = "
		        + conceptQuery(ConceptAnswer.CYTOLOGY_ASCUS)
		        + ")) OR\n"
		        + "    (concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.COLPOSCOPY_EXAM_FINDING)
		        + " AND value_coded = "
		        + conceptQuery(ConceptAnswer.NORMAL)
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
		        + conceptQuery(ConceptAnswer.VIA_NEGATIVE)
		        + "and encounter_id in ( :baseEncounter) and person_id in (:personIdList)\n"
		        + "\n"
		        + "UNION\n"
		        + "\n"
		        + "SELECT distinct person_id \n"
		        + "FROM obs\n"
		        + "WHERE concept_id ="
		        + conceptQuery(FollowUpConceptQuestions.CYTOLOGY_RESULT)
		        + " AND \n"
		        + "\t(\t\n"
		        + "\t\tvalue_coded = "
		        + conceptQuery(ConceptAnswer.NON_REACTIVE)
		        + " OR \n"
		        + "        value_coded = "
		        + conceptQuery(ConceptAnswer.CYTOLOGY_ASCUS)
		        + " OR \n"
		        + "        (\n"
		        + "\t\t\t-- Cytology Result = >Ascus and Colposcopy = Normal\n"
		        + "    value_coded = "
		        + conceptQuery(ConceptAnswer.CYTOLOGY_GREATER_ASCUS_SUSPICIOUS)
		        + " AND \n"
		        + "    (concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.COLPOSCOPY_EXAM_FINDING)
		        + " AND value_coded = "
		        + conceptQuery(ConceptAnswer.NORMAL)
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
		        + conceptQuery(FollowUpConceptQuestions.HPV_DNA_SCREENING_RESULT)
		        + " AND "
		        + " value_coded = "
		        + conceptQuery(ConceptAnswer.POSITIVE)
		        + ") AND \n"
		        + "    (concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT)
		        + " AND "
		        + " (value_coded = "
		        + conceptQuery(ConceptAnswer.VIA_POSITIVE_ELIGIBLE_FOR_CRYO)
		        + " OR value_coded = "
		        + conceptQuery(ConceptAnswer.VIA_POSITIVE_NON_ELIGIBLE_FOR_CRYO)
		        + "))  OR\n"
		        + "    ((concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.CYTOLOGY_RESULT)
		        + " AND value_coded = "
		        + conceptQuery(ConceptAnswer.CYTOLOGY_ASCUS)
		        + ") "
		        + " AND (concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.COLPOSCOPY_EXAM_FINDING)
		        + " "
		        + " AND (value_coded = "
		        + conceptQuery(ConceptAnswer.COLPOSCOPY_LOW_GRADE_SIL)
		        + " OR "
		        + " value_coded = "
		        + conceptQuery(ConceptAnswer.COLPOSCOPY_HIGH_GRADE_SIL)
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
		        + conceptQuery(ConceptAnswer.VIA_POSITIVE_ELIGIBLE_FOR_CRYO)
		        + " "
		        + " OR value_coded = "
		        + conceptQuery(ConceptAnswer.VIA_POSITIVE_NON_ELIGIBLE_FOR_CRYO)
		        + ")) \n"
		        + "\tand encounter_id in ( :baseEncounter) and person_id in (:personIdList)\n"
		        + "\n"
		        + "UNION\n"
		        + "\n"
		        + "SELECT distinct person_id\n"
		        + "FROM obs\n"
		        + "WHERE (concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.CYTOLOGY_RESULT)
		        + " "
		        + "AND value_coded = "
		        + conceptQuery(ConceptAnswer.CYTOLOGY_ASCUS)
		        + ") "
		        + "AND (concept_id = "
		        + conceptQuery(FollowUpConceptQuestions.COLPOSCOPY_EXAM_FINDING)
		        + " "
		        + "AND (value_coded = "
		        + conceptQuery(ConceptAnswer.COLPOSCOPY_LOW_GRADE_SIL)
		        + " OR "
		        + "value_coded = "
		        + conceptQuery(ConceptAnswer.COLPOSCOPY_LOW_GRADE_SIL)
		        + " ))\n"
		        + "\t\tand encounter_id in ( :baseEncounter) and person_id in (:personIdList)) cxcaP;\n";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public Cohort getSuspectedResult(Cohort cohort) {
		String stringQuery = "Select distinct person_id\n" + "from obs\n" + "WHERE concept_id = "
		        + conceptQuery(VIA_SCREENING_RESULT) + " AND\n" + "value_coded = "
		        + conceptQuery(ConceptAnswer.SUSPECTED_CERVICAL_CANCER)
		        + " and encounter_id in ( :baseEncounter) and person_id in (:personIdList)";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setParameterList("baseEncounter", baseEncounter);
		query.setParameterList("personIdList", cohort.getMemberIds());
		
		return new Cohort(query.list());
	}
	
	public List<Integer> getCurrentEncounter() {
		return currentEncounter;
	}
	
	public Cohort getCohortUnion(Cohort a, Cohort b) {
        if (a == null && b == null)
            return new Cohort();
        if (a == null || a.isEmpty()) {
            return b;
        } else if (b == null || b.isEmpty()) {
            return a;
        }

        List<Integer> list = new ArrayList<>();
        for (Integer integer : a.getMemberIds()) {
            if (!b.getMemberIds().contains(integer)) {
                list.add(integer);
            }
        }
        return Cohort.union(new Cohort(list), b);

    }
}
