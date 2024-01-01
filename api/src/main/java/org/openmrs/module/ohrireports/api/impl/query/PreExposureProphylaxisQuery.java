package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
	
	//	public List<Integer> getBaseScreeningEncounter() {
	//		return baseScreeningEncounter;
	//	}
	
	public void setBaseScreeningEncounter(List<Integer> baseScreeningEncounter) {
		this.baseScreeningEncounter = baseScreeningEncounter;
	}
	
	public List<Integer> baseScreeningEncounter;
	
	private List<Integer> currentEncounter;
	
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
		baseFollowupEncounter = encounterQuery.getEncounters(Arrays.asList(FOLLOW_UP_DATE), startDate, endDate,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		currentEncounter = baseFollowupEncounter;
	}
	
	@Autowired
	public PreExposureProphylaxisQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public Cohort loadPrepCohort() {
		StringBuilder stringBuilder = baseQuery(FOLLOW_UP_DATE, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseFollowupEncounter);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
	public Cohort getAllNewPrEP() {
		StringBuilder stringBuilder = baseQuery(PREP_FOLLOWUP_STATUS, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(NEWLY_STARTED));
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseFollowupEncounter);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
	public Cohort getAllPrEPCT() {
		List<Integer> basePrEPCTEncounter = encounterQuery.getEncounters(Arrays.asList(FOLLOW_UP_DATE), null, endDate,
		    PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		List<Integer> filteredEncounterForPrepCT = filterEncounterByPrePStatusForPrepCT(Arrays.asList(PREP_DOSE_END_DATE),
		    null, endDate, basePrEPCTEncounter);
		List<Integer> filteredPreviouslyForPrepCT = filterEncounterByPrePStatusForPrepCT(Arrays.asList(FOLLOW_UP_DATE),
		    startDate, null, filteredEncounterForPrepCT);
		StringBuilder stringBuilder = baseQuery(PREP_FOLLOWUP_STATUS, PREP_FOLLOW_UP_ENCOUNTER_TYPE);
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		stringBuilder.append(" and (").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(ON_PREP))
		        .append(" OR value_coded = ").append(conceptQuery(RESTART)).append(")").append(" OR value_coded = ")
		        .append(conceptQuery(TRANSFERRED_IN)).append(")");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", filteredEncounterForPrepCT);
		
		baseCohort = Cohort.union(new Cohort(query.list()), getPrevouslyNewCohort(filteredPreviouslyForPrepCT));
		
		return baseCohort;
	}
	
	private List<Integer> filterEncounterByPrePStatusForPrepCT(List<String> questionConcept, Date startDate, Date endDate, List<Integer> encounters) {

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
		StringBuilder sqlBuilder = new StringBuilder("select person_id from obs where obs.concept_id =")
		        .append(conceptQuery(PREP_FOLLOWUP_STATUS));
		sqlBuilder.append(" and obs.value_coded =").append(conceptQuery(NEWLY_STARTED));
		sqlBuilder.append(" and obs.encounter_id in (:encounterIds)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", encounterIds);
		return new Cohort(query.list());
	}
}
