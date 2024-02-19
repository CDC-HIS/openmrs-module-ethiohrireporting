package org.openmrs.module.ohrireports.api.impl.query;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EncounterQuery extends BaseEthiOhriQuery {
	
	private final DbSessionFactory sessionFactory;
	
	@Autowired
	public EncounterQuery(DbSessionFactory _SessionFactory) {
		
		sessionFactory = _SessionFactory;
		
	}
	
	private DbSession getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public List<Integer> getLatestDateByFollowUpDate(Date start, Date end) {
		StringBuilder builder = new StringBuilder("select ob.encounter_id from obs as ob inner join ");
		builder.append("(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");
		
		builder.append(" where obs_enc.concept_id =").append(conceptQuery(FOLLOW_UP_DATE));
		
		if (start != null)
			builder.append(" and obs_enc.value_datetime >= :start ");
		if (end != null)
			builder.append(" and obs_enc.value_datetime <= :end ");
		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		builder.append(" and ob.concept_id =").append(conceptQuery(FOLLOW_UP_DATE));
		
		Query q = getCurrentSession().createSQLQuery(builder.toString());
		
		if (start != null)
			q.setDate("start", start);
		if (end != null)
			q.setDate("end", end);
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getFirstEncounterByFollowUpDate() {
		StringBuilder builder = new StringBuilder("select ob.encounter_id from obs as ob inner join ");
		builder.append("(select MIN(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");
		
		builder.append(" where obs_enc.concept_id =").append(conceptQuery(FOLLOW_UP_DATE));
		
		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		builder.append(" and ob.concept_id =").append(conceptQuery(FOLLOW_UP_DATE));
		
		Query q = getCurrentSession().createSQLQuery(builder.toString());
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getAliveFollowUpEncounters(Date start, Date end) {
		List<Integer> allEncounters = getLatestDateByFollowUpDate(start, end);
		
		String builder = "select ob.encounter_id from obs as ob" + " where ob.concept_id =" + conceptQuery(FOLLOW_UP_STATUS)
		        + " and ob.value_coded in " + conceptQuery(Arrays.asList(ALIVE, RESTART))
		        + " and ob.encounter_id in (:encounters)";
		
		Query q = getCurrentSession().createSQLQuery(builder);
		q.setParameterList("encounters", allEncounters);
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
		
	}
	
	public List<Integer> getAliveFollowUpEncounters(Cohort cohort, Date start, Date end) {
		List<Integer> allEncounters = getLatestDateByFollowUpDate(cohort, start, end);
		
		String builder = "select ob.encounter_id from obs as ob" + " where ob.concept_id =" + conceptQuery(FOLLOW_UP_STATUS)
		        + " and ob.value_coded in " + conceptQuery(Arrays.asList(ALIVE, RESTART))
		        + " and ob.encounter_id in (:encounters)";
		
		Query q = getCurrentSession().createSQLQuery(builder);
		q.setParameterList("encounters", allEncounters);
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
		
	}
	
	public List<Integer> getLatestDateByFollowUpDate(Cohort cohort, Date start, Date end) {
		StringBuilder builder = new StringBuilder("select ob.encounter_id from obs as ob inner join ");
		builder.append("(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");
		
		builder.append(" where obs_enc.concept_id =").append(conceptQuery(FOLLOW_UP_DATE));
		builder.append(" and obs_enc.person_id in (:cohort)");
		
		if (start != null)
			builder.append(" and obs_enc.value_datetime >= :start ");
		if (end != null)
			builder.append(" and obs_enc.value_datetime <= :end ");
		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		builder.append(" and ob.concept_id =").append(conceptQuery(FOLLOW_UP_DATE));
		
		Query q = getCurrentSession().createSQLQuery(builder.toString());
		
		if (start != null)
			q.setDate("start", start);
		if (end != null)
			q.setDate("end", end);
		
		q.setParameterList("cohort", cohort.getMemberIds());
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getEncounters(List<String> questionConcept, Date start, Date end, List<Integer> encounters) {

        if (encounters.isEmpty())
            return encounters;

        if (questionConcept == null || questionConcept.isEmpty())
            return new ArrayList<>();

        StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join ");
        builder.append("(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");

        builder.append(" where obs_enc.concept_id in ")
                .append(conceptQuery(questionConcept));

        if (start != null)
            builder.append(" and obs_enc.value_datetime >= :start ");

        if (end != null)
            builder.append(" and obs_enc.value_datetime <= :end ");
        builder.append(" and obs_enc.encounter_id in (:subLatestFollowUpDates)");

        builder.append(" GROUP BY obs_enc.person_id ) as sub ");
        builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
        builder.append(" and ob.concept_id in ").append(conceptQuery(questionConcept));
        builder.append(" and ob.encounter_id in (:latestFollowUpDates)");

        Query q = getCurrentSession().createSQLQuery(builder.toString());

        if (start != null)
            q.setDate("start", start);

        if (end != null)
            q.setDate("end", end);

        q.setParameterList("latestFollowUpDates", encounters);
        q.setParameterList("subLatestFollowUpDates", encounters);

        List list = q.list();

        if (list != null) {
            return (List<Integer>) list;
        } else {
            return new ArrayList<Integer>();
        }
    }
	
	public List<Integer> getEncounters(List<String> questionConcept, Date start, Date end) {

        if (questionConcept == null || questionConcept.isEmpty())
            return new ArrayList<>();

        StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join ");
        builder.append(
                "(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");

        builder.append(" where obs_enc.concept_id in " + conceptQuery(questionConcept));

        if (start != null)
            builder.append(" and obs_enc.value_datetime >= :start ");

        if (end != null)
            builder.append(" and obs_enc.value_datetime <= :end ");

        builder.append(" GROUP BY obs_enc.person_id ) as sub ");
        builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
        builder.append(" and ob.concept_id in " + conceptQuery(questionConcept));

        Query q = getCurrentSession().createSQLQuery(builder.toString());

        if (start != null)
            q.setDate("start", start);

        if (end != null)
            q.setDate("end", end);

        List list = q.list();

        if (list != null) {
            return (List<Integer>) list;
        } else {
            return new ArrayList<Integer>();
        }
    }
	
	public List<Integer> getAllEncounters(List<String> questionConcept, Date start, Date end,String encounterTypeUUId) {
		
		if (questionConcept == null || questionConcept.isEmpty())
			return new ArrayList<>();
		
		StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join  ");
		builder.append(" encounter as e on e.encounter_id = ob.encounter_id inner join ");
		builder.append(" encounter_type as et on et.encounter_type_id = e.encounter_type and et.uuid ='").append(encounterTypeUUId).append("' ");
		builder.append(" where ob.concept_id in " + conceptQuery(questionConcept));
		
		if (start != null)
			builder.append(" and ob.value_datetime >= :start ");
		
		if (end != null)
			builder.append(" and ob.value_datetime <= :end ");
		
		Query q = getCurrentSession().createSQLQuery(builder.toString());
		
		if (start != null)
			q.setDate("start", start);
		
		if (end != null)
			q.setDate("end", end);
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getEncounters(List<String> questionConcept, Date start, Date end, String encounterType) {

		if (questionConcept == null || questionConcept.isEmpty())
			return new ArrayList<>();

		StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join ");
		builder.append(
				"(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc ");
		builder.append("inner join encounter as e on e.encounter_id = obs_enc.encounter_id ");
		builder.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		builder.append("and et.uuid= '").append(encounterType).append("' ");
		builder.append(" where obs_enc.concept_id in ").append(conceptQuery(questionConcept));


		if (start != null)
			builder.append(" and obs_enc.value_datetime >= :start ");

		if (end != null)
			builder.append(" and obs_enc.value_datetime <= :end ");

		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		builder.append(" and ob.concept_id in " + conceptQuery(questionConcept));

		Query q = getCurrentSession().createSQLQuery(builder.toString());

		if (start != null)
			q.setDate("start", start);

		if (end != null)
			q.setDate("end", end);

		List list = q.list();

		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getEncountersWithDuplicated(List<String> questionConcept, Date start, Date end, String encounterType) {
		
		if (questionConcept == null || questionConcept.isEmpty())
			return new ArrayList<>();
		
		StringBuilder builder = new StringBuilder("select distinct obs_enc.encounter_id from obs as obs_enc ");
		builder.append("inner join encounter as e on e.encounter_id = obs_enc.encounter_id ");
		builder.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		builder.append("and et.uuid= '").append(encounterType).append("' ");
		builder.append(" where obs_enc.concept_id in ").append(conceptQuery(questionConcept));
		
		
		if (start != null)
			builder.append(" and obs_enc.value_datetime >= :start ");
		
		if (end != null)
			builder.append(" and obs_enc.value_datetime <= :end ");
		
		
		Query q = getCurrentSession().createSQLQuery(builder.toString());
		
		if (start != null)
			q.setDate("start", start);
		
		if (end != null)
			q.setDate("end", end);
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getEncounters(List<String> questionConcept, Date start, Date endDate, Cohort cohort) {
        if (questionConcept == null || questionConcept.isEmpty())
            return new ArrayList<>();

        StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join ");
        builder.append(
                "(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");

        builder.append(" where obs_enc.concept_id in " + conceptQuery(questionConcept));

        if (start != null)
            builder.append(" and obs_enc.value_datetime >= :start ");

        if (endDate != null)
            builder.append(" and obs_enc.value_datetime <= :end ");

        builder.append(" GROUP BY obs_enc.person_id ) as sub ");
        builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
        builder.append(" and ob.concept_id in " + conceptQuery(questionConcept));
        builder.append(" and ob.person_id in (:cohort) ");

        Query q = getCurrentSession().createSQLQuery(builder.toString());
        q.setParameterList("cohort", cohort.getMemberIds());
        if (start != null)
            q.setDate("start", start);

        if (endDate != null)
            q.setDate("end", endDate);

        List list = q.list();

        if (list != null) {
            return (List<Integer>) list;
        } else {
            return new ArrayList<Integer>();
        }
    }
}
