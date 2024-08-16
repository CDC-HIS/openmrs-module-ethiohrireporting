package org.openmrs.module.ohrireports.api.impl.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions.FOLLOW_UP_DATE;

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
	
	public List<Integer> getSecondLatestFollowUp(Date end) {
		StringBuilder builder = new StringBuilder("SELECT ob.encounter_id FROM obs AS ob INNER JOIN ");
		builder.append("(SELECT MAX(obs_enc.value_datetime) AS value_datetime, obs_enc.person_id ");
		builder.append("FROM obs AS obs_enc ");
		builder.append("WHERE obs_enc.concept_id = ").append(conceptQuery(FOLLOW_UP_DATE));
		
		if (end != null)
			builder.append(" AND obs_enc.value_datetime <= :end ");
		builder.append("GROUP BY obs_enc.person_id) AS sub ");
		builder.append("ON ob.person_id = sub.person_id ");
		builder.append("AND ob.value_datetime < sub.value_datetime ");
		builder.append("AND ob.concept_id = ").append(conceptQuery(FOLLOW_UP_DATE));
		
		Query q = getCurrentSession().createSQLQuery(builder.toString());
		
		if (end != null)
			q.setDate("end", end);
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getLatestDateByEnrollmentDate(Date start, Date end, String encounterTypeUUID) {
		StringBuilder builder = new StringBuilder("select ob.encounter_id from obs as ob inner join ");
		builder.append("(select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc ");
		builder.append("inner join encounter as e on e.encounter_id = obs_enc.encounter_id ");
		builder.append("inner join encounter_type as et on et.encounter_type_id = e.encounter_type ");
		builder.append("and et.uuid= '").append(encounterTypeUUID).append("' ");
		builder.append(" where obs_enc.concept_id =").append(conceptQuery(FollowUpConceptQuestions.ENROLLMENT_DATE_IN_CARE));
		if (start != null)
			builder.append(" and obs_enc.value_datetime >= :start ");
		if (end != null)
			builder.append(" and obs_enc.value_datetime <= :end ");
		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		builder.append(" and ob.concept_id =").append(conceptQuery(FollowUpConceptQuestions.ENROLLMENT_DATE_IN_CARE));
		
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
	
	public List<Integer> getFirstEncounterByObsDate(Date start, Date end, String concept) {
		StringBuilder builder = new StringBuilder("select ob.encounter_id from obs as ob inner join ");
		builder.append("(select MIN(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");
		
		builder.append(" where obs_enc.concept_id =").append(conceptQuery(concept));
		
		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		if (start != null)
			builder.append(" and ob.value_datetime >= :start ");
		if (end != null)
			builder.append(" and ob.value_datetime <= :end ");
		builder.append(" and ob.concept_id =").append(conceptQuery(concept));
		
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
	
	public List<Integer> getEncountersByFollowUp(String encounterFollowUp, String conceptQuestion,
	        List<String> conceptAnswers) {
		
		StringBuilder stringBuilder = new StringBuilder("select ob.encounter_id from obs as ob ");
		stringBuilder.append(" inner join encounter as e on e.encounter_id = ob.encounter_id   ");
		stringBuilder.append(" inner join encounter_type as et on et.encounter_type_id = e.encounter_type and ");
		stringBuilder.append(" et.uuid = '").append(encounterFollowUp).append("' ");
		stringBuilder.append(" where ");
		stringBuilder.append(" ob.concept_id = ").append(conceptQuery(conceptQuestion));
		stringBuilder.append(" and  ob.value_coded in ").append(conceptQuery(conceptAnswers));
		
		Query q = getCurrentSession().createSQLQuery(stringBuilder.toString());
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getFirstEncounterByObsDate(Date start, Date end, String concept, Cohort cohort) {
		StringBuilder builder = new StringBuilder("select ob.encounter_id from obs as ob inner join ");
		builder.append("(select MIN(obs_enc.value_datetime) as value_datetime, person_id as person_id from obs as obs_enc");
		
		builder.append(" where obs_enc.concept_id =").append(conceptQuery(concept));
		
		builder.append(" GROUP BY obs_enc.person_id ) as sub ");
		
		builder.append(" on ob.value_datetime = sub.value_datetime and ob.person_id = sub.person_id ");
		if (start != null)
			builder.append(" and ob.value_datetime >= :start ");
		if (end != null)
			builder.append(" and ob.value_datetime <= :end ");
		if (cohort != null)
			builder.append(" and ob.person_id in (:cohort) ");
		builder.append(" and ob.concept_id =").append(conceptQuery(concept));
		
		Query q = getCurrentSession().createSQLQuery(builder.toString());
		
		if (start != null)
			q.setDate("start", start);
		if (end != null)
			q.setDate("end", end);
		if (cohort != null)
			q.setParameterList("cohort", cohort.getMemberIds());
		
		List list = q.list();
		
		if (list != null) {
			return (List<Integer>) list;
		} else {
			return new ArrayList<Integer>();
		}
	}
	
	public List<Integer> getAliveFollowUpEncounters(Date start, Date end) {
		List<Integer> allEncounters = getLatestDateByFollowUpDate(start, end);
		
		String builder = "select ob.encounter_id from obs as ob" + " where ob.concept_id ="
		        + conceptQuery(FollowUpConceptQuestions.FOLLOW_UP_STATUS) + " and ob.value_coded in "
		        + conceptQuery(Arrays.asList(ConceptAnswer.ALIVE, ConceptAnswer.RESTART))
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
	
	public List<Integer> getAliveFirstFollowUpEncounters(Date start, Date end) {
		List<Integer> allEncounters = getFirstEncounterByObsDate(start, end, FOLLOW_UP_DATE);
		
		String builder = "select ob.encounter_id from obs as ob" + " where ob.concept_id ="
		        + conceptQuery(FollowUpConceptQuestions.FOLLOW_UP_STATUS) + " and ob.value_coded in "
		        + conceptQuery(Arrays.asList(ConceptAnswer.ALIVE, ConceptAnswer.RESTART))
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
		
		String builder = "select ob.encounter_id from obs as ob" + " where ob.concept_id ="
		        + conceptQuery(FollowUpConceptQuestions.FOLLOW_UP_STATUS) + " and ob.value_coded in "
		        + conceptQuery(Arrays.asList(ConceptAnswer.ALIVE, ConceptAnswer.RESTART))
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
	
	public List<Integer> getEncountersByMaxObsDate(List<String> questionConcept, Date start, Date end) {

        if (questionConcept == null || questionConcept.isEmpty())
            return new ArrayList<>();

//        StringBuilder builder = new StringBuilder("SET @concept_id_1 = (SELECT concept_id FROM concept WHERE uuid = '" + FOLLOW_UP_DATE + "');");
//        builder.append("SET @concept_id_2 = (select concept_id from concept where uuid in ('")
//                .append(String.join("','", questionConcept)).append("'));");//.append(questionConcept).append(");");
        StringBuilder builder = new StringBuilder("select u.encounter_id from obs as u inner join ( ");
        builder.append(
                "select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id ");
        builder.append(" from obs as obs_enc where obs_enc.concept_id = (SELECT concept_id FROM concept WHERE uuid = '" + FOLLOW_UP_DATE + "')");//.append(conceptQuery(FOLLOW_UP_DATE));
        if (start != null)
            builder.append(" and obs_enc.value_datetime >= :start ");

        if (end != null)
            builder.append(" and obs_enc.value_datetime <= :end ");

        builder.append("and obs_enc.voided =0 and obs_enc.encounter_id in ( select distinct ob.encounter_id from obs as ob ") ;

        builder.append("inner join ( select Max(obs_enc.value_datetime) as value_datetime, person_id as person_id ");
        builder.append("from obs as obs_enc where obs_enc.voided =0 and obs_enc.concept_id = (select concept_id from concept where uuid in ('")
                .append(String.join("','", questionConcept)).append("'))");//.append(conceptQuery(questionConcept));
        if (start != null)
            builder.append(" and obs_enc.value_datetime >= :start ");

        if (end != null)
            builder.append(" and obs_enc.value_datetime <= :end ");
        builder.append(" GROUP BY obs_enc.person_id ) as sub1 on ob.value_datetime = sub1.value_datetime ");
        builder.append(" and ob.voided =0 and ob.person_id = sub1.person_id and ob.concept_id = (select concept_id from concept where uuid in ('")
                .append(String.join("','", questionConcept)).append("'))");//.append(conceptQuery(questionConcept));
        builder.append(" ) GROUP BY obs_enc.person_id ORDER BY obs_enc.person_id ");
        builder.append(" ) as subd on subd.value_datetime = u.value_datetime and u.person_id = subd.person_id ");
        builder.append(" where u.concept_id = (SELECT concept_id FROM concept WHERE uuid = '" + FOLLOW_UP_DATE + "')");//.append(conceptQuery(FOLLOW_UP_DATE));


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
	
	public List<Integer> getAllEncounters(List<String> questionConcept, Date start, Date end, String encounterTypeUUId) {

        if (questionConcept == null || questionConcept.isEmpty())
            return new ArrayList<>();

        StringBuilder builder = new StringBuilder("select distinct ob.encounter_id from obs as ob inner join  ");
        builder.append(" encounter as e on e.encounter_id = ob.encounter_id inner join ");
        builder.append(" encounter_type as et on et.encounter_type_id = e.encounter_type and et.uuid ='").append(encounterTypeUUId).append("' ");
        builder.append(" where ob.voided =0 and ob.concept_id in " + conceptQuery(questionConcept));

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
        builder.append(" where obs_enc.voided =0 and obs_enc.concept_id in ").append(conceptQuery(questionConcept));


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
	
	public List<Integer> getEncountersByExcludingCohort(List<String> questionConcept,List<Integer> encounters, Date start, Date endDate, Cohort cohort) {
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
		builder.append(" and ob.voided =0 and ob.person_id not in (:cohort) ");
		builder.append(" and ob.encounter_id in (:encounter_id)");

		Query q = getCurrentSession().createSQLQuery(builder.toString());
		q.setParameterList("cohort", cohort.getMemberIds());
		q.setParameterList("encounter_id", encounters);

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
        builder.append(" and ob.voided =0 and ob.person_id in (:cohort) ");

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
