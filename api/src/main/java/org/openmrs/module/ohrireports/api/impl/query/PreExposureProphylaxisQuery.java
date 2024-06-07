package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.jetbrains.annotations.NotNull;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class PreExposureProphylaxisQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private List<Integer> baseFollowupEncounter;
	
	public List<Integer> getBaseFollowupEncounter() {
		return baseFollowupEncounter;
	}
	
	public void setBaseScreeningEncounter(List<Integer> baseScreeningEncounter) {
		this.baseScreeningEncounter = baseScreeningEncounter;
	}
	
	public List<Integer> getBaseScreeningEncounter() {
		return baseScreeningEncounter;
	}
	
	public List<Integer> baseScreeningEncounter;
	
	public List<Integer> latestFollowupEncounter;
	
	public List<Integer> getLatestFollowupEncounter() {
		return latestFollowupEncounter;
	}
	
	public void setLatestFollowupEncounter(List<Integer> latestFollowupEncounter) {
		this.latestFollowupEncounter = latestFollowupEncounter;
	}
	
	private List<Integer> basePrEPCurrEncounter;
	
	private List<Integer> basePrEPCTEncounter;
	
	private Date startDate;
	
	private Date endDate;
	
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
		baseFollowupEncounter = encounterQuery.getEncounters(Collections.singletonList(FOLLOW_UP_DATE), startDate, endDate,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		baseScreeningEncounter = encounterQuery.getEncounters(Collections.singletonList(PREP_STARTED_DATE), startDate,
		    endDate, PREP_SCREENING_ENCOUNTER_TYPE);
		latestFollowupEncounter = encounterQuery.getEncounters(Collections.singletonList(FOLLOW_UP_DATE), null, new Date(),
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
	}
	
	@Autowired
	public PreExposureProphylaxisQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public Cohort loadPrepCohort() {
		StringBuilder stringBuilder = baseQuery(PREP_STARTED_DATE, PREP_SCREENING_ENCOUNTER_TYPE);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseScreeningEncounter);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
	//	public List<Integer> loadPrepEncounter(){
	//		StringBuilder stringBuilder = baseQuery(FOLLOW_UP_DATE, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
	//
	//		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
	//		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
	//		query.setParameterList("encounters", baseFollowupEncounter);
	//	}
	
	public Cohort getAllNewPrEP() {
		StringBuilder stringBuilder = baseQuery(PREP_TYPE_OF_CLIENT, PREP_SCREENING_ENCOUNTER_TYPE);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(NEWLY_STARTED));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseScreeningEncounter);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
	public Cohort getAllPregnantPrep(Cohort cohort) {
		StringBuilder stringBuilder = baseQuery(PREGNANCY_STATUS, PREP_SCREENING_ENCOUNTER_TYPE);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("person_id in (:personIds)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(YES));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseScreeningEncounter);
		query.setParameterList("personIds", cohort.getMemberIds());
		return new Cohort(query.list());
	}
	
	public Cohort getAllBreastFeedingPrep(Cohort cohort) {
		StringBuilder stringBuilder = baseQuery(CURRENTLY_BREAST_FEEDING_CHILD, PREP_SCREENING_ENCOUNTER_TYPE);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("person_id in (:personIds)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(YES));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseScreeningEncounter);
		query.setParameterList("personIds", cohort.getMemberIds());
		return new Cohort(query.list());
	}
	
	public Cohort getAllPrEPCT() {
		Cohort currCohort = getPrepCurr();
		//Cohort previousCohort = getCohortByFollowupDateAndStatus();
		Cohort followupCohort = getCohortByStatus();
		baseCohort = getUnion(currCohort, followupCohort);
		
		return filterOutClientsWithArtStartDate(baseCohort, startDate);
	}
	
	private Cohort getPrepCurr() {
		basePrEPCTEncounter = encounterQuery.getEncounters(Arrays.asList(FOLLOW_UP_DATE), null, endDate,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		Cohort cohort = getCohort(baseScreeningEncounter);
		
		basePrEPCTEncounter = refineEncounter(cohort, basePrEPCTEncounter);
		
		// count clients whose dose end date greater than reporting end date
		basePrEPCurrEncounter = filterEncounterByPrePStatusForPrepCT(Arrays.asList(PREP_DOSE_END_DATE), null, endDate,
		    basePrEPCTEncounter);
		cohort = getCohort(basePrEPCurrEncounter);
		
		return cohort;
	}
	
	private Cohort getCohortByFollowupDateAndStatus() {
		// filter out clients whose followup date less than the reporting period
		List<Integer> filteredPreviouslyForPrepCT = filterEncounterByPrePStatusForPrepCT(
		    Collections.singletonList(FOLLOW_UP_DATE), startDate, null, basePrEPCurrEncounter);
		return getPrevouslyNewCohort(filteredPreviouslyForPrepCT);
	}
	
	private Cohort getCohortByStatus() {
		List<Integer> encounter = encounterQuery.getEncounters(Collections.singletonList(FOLLOW_UP_DATE), startDate,
		    endDate, basePrEPCTEncounter);
		return getCohortByNotInPrepStatus(encounter);
	}
	
	@NotNull
	private Cohort getCohortByPrepStatus(List<Integer> basePrEPCTEncounter) {
		Cohort cohort;
		StringBuilder stringBuilder = baseQuery(PREP_FOLLOWUP_STATUS, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded in ")
		        .append(conceptQuery(Arrays.asList(ON_PREP, RESTART, TRANSFERRED_IN)));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", basePrEPCTEncounter);
		cohort = new Cohort(query.list());
		return cohort;
	}
	
	private Cohort filterOutClientsWithArtStartDate(Cohort cohort, Date startDate) {
		Cohort artStartedCohort = getArtStartedCohort(null, startDate);
		
		return Cohort.subtract(cohort, artStartedCohort);
	}
	
	private Cohort getCohortByNotInPrepStatus(List<Integer> basePrEPCTEncounter) {
		Cohort cohort;
		StringBuilder stringBuilder = baseQuery(PREP_FOLLOWUP_STATUS, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded not in ")
		        .append(conceptQuery(Arrays.asList(LOST_TO_FOLLOW_UP, DEAD)));// TODO: check Concept id
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", basePrEPCTEncounter);
		cohort = new Cohort(query.list());
		return cohort;
	}
	
	private List<Integer> refineEncounter(Cohort cohort, List<Integer> encounter) {
        if (cohort.size() == 0) {
            return encounter;
        }
        String sqlBuilder = "select encounter_id from encounter " + "Where  patient_id not in (:personIds) and " +
                "encounter_id in (:encounter)";
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);
        query.setParameterList("personIds", cohort.getMemberIds());
        query.setParameterList("encounter", encounter);
        List list = query.list();

        if (Objects.isNull(list))
            return new ArrayList<>();

        return (List<Integer>) list;
    }
	
	private List<Integer> filterEncounterByPrePStatusForPrepCT(List<String> questionConcept, Date startDate, Date endDate,
                                                               List<Integer> encounters) {

        if (encounters.isEmpty())
            return encounters;

        if (questionConcept == null || questionConcept.isEmpty())
            return new ArrayList<>();

        StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join ");
        builder.append("(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");

        builder.append(" where obs_enc.concept_id in ")
                .append(conceptQuery(questionConcept));

        if (startDate != null)
            builder.append(" and obs_enc.value_datetime <= :start ");

        if (endDate != null)
            builder.append(" and obs_enc.value_datetime >= :end ");
        builder.append(" and obs_enc.encounter_id in (:subLatestFollowUpDates)");

        builder.append(" GROUP BY obs_enc.person_id ) as sub ");
        builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");

        builder.append(" and ob.concept_id in ").append(conceptQuery(questionConcept));
        builder.append(" and ob.encounter_id in (:latestFollowUpDates)");

        Query q = sessionFactory.getCurrentSession().createSQLQuery(builder.toString());

        if (startDate != null)
            q.setDate("start", startDate);

        if (endDate != null)
            q.setDate("end", endDate);

        q.setParameterList("latestFollowUpDates", encounters);
        q.setParameterList("subLatestFollowUpDates", encounters);

        List list = q.list();

        if (list != null) {
            return (List<Integer>) list;
        } else {
            return new ArrayList<Integer>();
        }
    }
	
	private Cohort getPrevouslyNewCohort(List<Integer> encounterIds) {
		if (encounterIds.isEmpty())
			return null;
		StringBuilder sqlBuilder = new StringBuilder("select person_id from obs where obs.concept_id =")
		        .append(conceptQuery(PREP_FOLLOWUP_STATUS));
		sqlBuilder.append(" and obs.value_coded =").append(conceptQuery(NEWLY_STARTED));
		sqlBuilder.append(" and obs.encounter_id in (:encounterIds)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", encounterIds);
		return new Cohort(query.list());
	}
	
	private Cohort getUnion(Cohort a, Cohort b) {
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
