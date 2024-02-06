package org.openmrs.module.ohrireports.api.impl.query.pmtct;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.*;

import static java.util.Arrays.asList;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class FOQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	private List<Integer> baseEncounter;
	
	private Date startDate;
	
	private Date endDate;
	
	public FOQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		baseCohort = getPmtctFODenominatorCohort();
	}
	
	public Cohort getPmtctFODenominatorCohort() {
		Date dateFrom = Date.from(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusMonths(24)
		        .atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date dateTo = Date.from(endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusMonths(24)
		        .atStartOfDay(ZoneId.systemDefault()).toInstant());
		String stringQuery = "select distinct ob.person_id\n" + "from obs ob "
		        + " INNER JOIN person p where p.person_id = ob.person_id " + "and ob.concept_id = "
		        + conceptQuery(HEI_ENROLLMENT_DATE) + " and value_datetime IS NOT NULL"
		        + " and p.birthdate between :start and :end";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringQuery);
		query.setDate("start", dateFrom);
		query.setDate("end", dateTo);
		
		return new Cohort(query.list());
	}
	
	public Cohort getPmtctByHivInfectedStatus() {
		Cohort positiveFinalOutcome = getByOutcomeOrConclusion(
		    baseCohort,
		    PMTCT_FINAL_OUTCOME,
		    asList(PMTCT_POSITIVE_LINKED_OUTSIDE_FACILITY, PMTCT_POSITIVE_LINKED_WITHIN_FACILITY,
		        PMTCT_POSITIVE_LINKED_UNKNOWN));
		Cohort discontinueFinalOutcome = getByOutcomeOrConclusion(baseCohort, PMTCT_FINAL_OUTCOME,
		    asList(DIED, PMTCT_TO, PMTCT_LOST_TO_FOLLOWUP));
		Cohort evidenceConclusion = getByOutcomeOrConclusion(baseCohort, PMTCT_CONCLUSION,
		    asList(PMTCT_CLINICAL_EVIDENCE_HIV, PMTCT_LAB_EVIDENCE_HIV));
		Cohort hivPcrTestResult = getByOutcomeOrConclusion(baseCohort, PMTCT_DNA_PCR_RESULT,
		    Collections.singletonList(POSITIVE));
		
		return getCohortUnion(positiveFinalOutcome,
		    Cohort.intersect(discontinueFinalOutcome, getCohortUnion(evidenceConclusion, hivPcrTestResult)));
	}
	
	public Cohort getPmtctByHivUninfectedStatus() {
		return getByOutcomeOrConclusion(baseCohort, PMTCT_FINAL_OUTCOME, Collections.singletonList(NEGATIVE));
	}
	
	public Cohort getPmtctByHivStatusUnknown() {
		Cohort finalStatusUnknown = getByOutcomeOrConclusion(baseCohort, PMTCT_FINAL_OUTCOME,
		    asList(PMTCT_TO, PMTCT_LOST_TO_FOLLOWUP));
		Cohort finalStatusNull = getNullValue(baseCohort, PMTCT_FINAL_OUTCOME);
		Cohort conclusionCohort = getByOutcomeOrConclusion(baseCohort, PMTCT_CONCLUSION,
		    Collections.singletonList(PMTCT_CONCLUSION_NO_EVIDENCE));
		Cohort conclusionNull = getNullValue(baseCohort, PMTCT_CONCLUSION);
		Cohort hivPcrTestResult = getByOutcomeOrConclusion(baseCohort, PMTCT_DNA_PCR_RESULT,
		    Collections.singletonList(POSITIVE));
		
		return getCohortUnion(
		    Cohort.intersect(finalStatusUnknown,
		        getCohortUnion(conclusionCohort, getCohortUnion(conclusionNull, hivPcrTestResult))), finalStatusNull);
	}
	
	public Cohort getPmtctDiedWithoutStatusKnown() {
		Cohort finalOutComeDied = getByOutcomeOrConclusion(baseCohort, PMTCT_FINAL_OUTCOME, Collections.singletonList(DIED));
		Cohort conclusionCohort = getByOutcomeOrConclusion(baseCohort, PMTCT_CONCLUSION,
		    Collections.singletonList(PMTCT_CONCLUSION_NO_EVIDENCE));
		Cohort conclusionNull = getNullValue(baseCohort, PMTCT_CONCLUSION);
		Cohort hivPcrTestResult = getByOutcomeOrConclusion(baseCohort, PMTCT_DNA_PCR_RESULT,
		    Collections.singletonList(POSITIVE));
		
		return Cohort.intersect(finalOutComeDied,
		    getCohortUnion(conclusionCohort, getCohortUnion(conclusionNull, hivPcrTestResult)));
	}
	
	private Cohort getByOutcomeOrConclusion(Cohort cohort, String questionConcept, List<String> answers) {
		StringBuilder sql = new StringBuilder("select distinct  person_id from obs where concept_id = ");
		sql.append(conceptQuery(questionConcept)).append(" and value_coded in ").append(conceptQuery(answers))
		        .append(" and person_id in (:personIdList)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("personIdList", cohort.getMemberIds());
		return new Cohort(query.list());
	}
	
	private Cohort getNullValue(Cohort cohort, String questionConcept) {
		StringBuilder sql = new StringBuilder("select distinct  person_id from obs where concept_id = ");
		sql.append(conceptQuery(questionConcept)).append(" and value_coded = NULL ")
		        .append(" and person_id in (:personIdList)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameterList("personIdList", cohort.getMemberIds());
		return new Cohort(query.list());
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
