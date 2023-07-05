package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_plhiv;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.UNDERNOURISHED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.THERAPEUTIC_SUPPLEMENTARY_FOOD;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NUTRITIONAL_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.MILD_MAL_NUTRITION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.MODERATE_MAL_NUTRITION;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SEVERE_MAL_NUTRITION;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class HivPlvHivQuery extends PatientQueryImpDao  {
 
  
    private Date startDate,endDate;
    private DbSessionFactory sessionFactory;


    @Autowired
    public HivPlvHivQuery(DbSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        setSessionFactory(sessionFactory);
    }

    public void setDates(Date _start,Date _date){
        startDate = _start;
        endDate = _date;
    }

    public Set<Integer> getAssessedPatients(){
          StringBuilder sql = new StringBuilder();
          Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
          
         sql.append(" select person_id from obs where obs_id in ");
         sql.append(" (select Max(obs_id) from obs where concept_id = ");
         sql.append(" (select distinct concept_id from concept where uuid = '"+NUTRITIONAL_STATUS+"' limit 1) ");
         sql.append(" and value_coded = is not null ");
         sql.append(" and person_id in (:person_id) ");
         sql.append(" and obs_datetime >= :startDate and obs_datetime <= :endDate group by person_id) ");

         Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            query.setParameterList("person_id", cohort.getMemberIds());
            query.setTime("startDate", startDate);
            query.setTime("endDate", endDate);

        return new HashSet<>(query.list());
    }

    public Set<Integer> getPatientUndernourished(){
         StringBuilder sql = new StringBuilder();
         Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
         sql.append(" select person_id from obs where obs_id in ");
         sql.append(" (select Max(obs_id) from obs where concept_id = ");
         sql.append(" (select distinct concept_id from concept where uuid = '"+NUTRITIONAL_STATUS+"' limit 1) ");
         sql.append(" and value_coded = (select distinct concept_id from concept where uuid = '"+UNDERNOURISHED+"' limit 1) ");
         sql.append(" and person_id in (:person_id) ");
         sql.append(" and obs_datetime >= :startDate and obs_datetime <= :endDate group by person_id) ");

         Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            query.setParameterList("person_id", cohort.getMemberIds());
            query.setTime("startDate", startDate);
            query.setTime("endDate", endDate);

        return new HashSet<>(query.list());
    }

    public Set<Integer> getPatientModerateMalNutrition(){
         StringBuilder sql = new StringBuilder();
         Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
         sql.append(" select person_id from obs where obs_id in ");
         sql.append(" (select Max(obs_id) from obs where concept_id = ");
         sql.append(" (select distinct concept_id from concept where uuid = '"+NUTRITIONAL_STATUS+"' limit 1) ");
         sql.append(" and value_coded in (select distinct concept_id from concept where uuid in ('"+MILD_MAL_NUTRITION+"','"+MODERATE_MAL_NUTRITION+"' ) ");
         sql.append(" and person_id in (:person_id) ");
         sql.append(" and obs_datetime >= :startDate and obs_datetime <= :endDate group by person_id) ");

         Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            query.setParameterList("person_id", cohort.getMemberIds());
            query.setTime("startDate", startDate);
            query.setTime("endDate", endDate);

        return new HashSet<>(query.list());
    }

      public Set<Integer> getPatientSevereMalNutrition(){
         StringBuilder sql = new StringBuilder();
         Cohort cohort = getActiveOnArtCohort("", startDate, endDate, null);
         sql.append(" select person_id from obs where obs_id in ");
         sql.append(" (select Max(obs_id) from obs where concept_id = ");
         sql.append(" (select distinct concept_id from concept where uuid = '"+NUTRITIONAL_STATUS+"' limit 1) ");
         sql.append(" and value_coded in (select distinct concept_id from concept where uuid = '"+SEVERE_MAL_NUTRITION+"' ) ");
         sql.append(" and person_id in (:person_id) ");
         sql.append(" and obs_datetime >= :startDate and obs_datetime <= :endDate group by person_id) ");

         Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            query.setParameterList("person_id", cohort.getMemberIds());
            query.setTime("startDate", startDate);
            query.setTime("endDate", endDate);

        return new HashSet<>(query.list());
    }

     public Set<Integer> getPatientTookSupplement(Set<Integer> memberIds){
         StringBuilder sql = new StringBuilder();
         sql.append(" select person_id from obs where obs_id in ");
         sql.append(" (select Max(obs_id) from obs where concept_id = ");
         sql.append(" (select distinct concept_id from concept where uuid = '"+THERAPEUTIC_SUPPLEMENTARY_FOOD+"' limit 1) ");
         sql.append(" and value_coded = (select distinct concept_id from concept where uuid = '"+YES+"' limit 1) ");
         sql.append(" and person_id in (:person_id) ");
         sql.append(" and obs_datetime >= :startDate and obs_datetime <= :endDate group by person_id) ");

         Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
            query.setParameterList("person_id", memberIds);
            query.setTime("startDate", startDate);
            query.setTime("endDate", endDate);

        return new HashSet<>(query.list());
    }
}
