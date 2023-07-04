package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.pr_ep;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.PR_EP_STARTED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TDF_TENOFOVIR_DRUG;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TDF_FTC_DRUG;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TDF_3TC_DRUG;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
import org.openmrs.Person;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivPrEpQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Date startDate, endDate;
	
	@Autowired
	public HivPrEpQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	public void initializeDate(Date start, Date end) {
		startDate = start;
		endDate = end;
	}
	
	public Set<Integer> getPatientsOnPrEp() {
		
		return getPatientOnPrEpCurr(getPatientStartedPrep());
		
	}
	
	public Set<Integer> getPatientOnPrEpCurr(Set<Integer> patientId) {
        StringBuilder sql = new StringBuilder("select distinct person_id from obs as ob where ob.ob_id in");
        sql.append("(select MAX(obs_id) from obs as obs   ");

        sql.append(" where  obs.voided = false ");
        if (patientId != null && patientId.size() > 0)
            sql.append("and obs.value_coded in (:drugs) ");
        sql.append(
                " and obs.ob_datetime >= :startOnOrBefore and obs.ob_datetime <= :endOnOrAfter group by person_id) ");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameter("startOnOrBefore", startDate);

        query.setParameter("endOnOrAfter", endDate);

        if (patientId != null && patientId.size() > 0)
            query.setParameterList("drugs", patientId);

        return new HashSet<>(query.list());
    }
	
	private Set<Integer> getPatientStartedPrep() {
        StringBuilder sql = baseQuery(PR_EP_STARTED);

        sql.append(
                "and obs_id in (select MAX(obs_id) from obs as obs   inner join concept as c on c.concept_id = ob.concept_id ");
        sql.append(" and c.uuid = '" + PR_EP_STARTED
                + "'  where  obs.voided = false and obs.value_datetime >= :startOnOrBefore and obs.value_datetime <= :endOnOrAfter group by person_id) ");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameter("startOnOrBefore", startDate);

        query.setParameter("endOnOrAfter", endDate);

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPrEpDrugs() {
        StringBuilder sql = new StringBuilder("select distinct concept_id from concept ");
        sql.append("where uuid in ('" + TDF_TENOFOVIR_DRUG + "','" + TDF_FTC_DRUG + "','" + TDF_3TC_DRUG + "')");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
         return new HashSet<>(query.list());
    }
}
