package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.pr_ep;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.PR_EP_STARTED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TDF_TENOFOVIR_DRUG;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TDF_FTC_DRUG;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TDF_3TC_DRUG;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FEMALE_SEX_WORKER;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DISCORDANT_COUPLE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.EXPOSURE_TYPE;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Query;
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
	
	public Set<Integer> getPatientOnPrEpCurr() {
        StringBuilder sql = personIdQuery(getCurrQueryClauses(), "");

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());

        query.setParameter("endOnOrAfter", endDate);

        query.setParameterList("drugs", getPrEpDrugs().toArray());

        return new HashSet<>(query.list());
    }
	
	public Set<Integer> getPatientStartedPrep() {

        String subQueryClauses = getSubQueryClauses();
        StringBuilder sql = personIdQuery(subQueryClauses, "");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());


        query.setParameter("endOnOrAfter", endDate);

        return new HashSet<>(query.list());
    }
	
	/*
	 * Newly enrolled patients to Prep
	 */
	private String getSubQueryClauses() {
		String subQueryClauses = "obs.concept_id =" + conceptQuery(PR_EP_STARTED)
		        + " and obs.voided = false and obs.value_datetime >= :endOnOrAfter  ";
		return subQueryClauses;
	}
	
	/*
	 * curr concerned more about the drug they are taking
	 */
	private String getCurrQueryClauses() {
		String subQueryClauses = " obs.value_coded in (:drugs) and obs.voided = false and  obs.obs_datetime >= :endOnOrAfter ";
		return subQueryClauses;
	}
	
	public Integer getFemaleSexWorkerOnPrep(Boolean isCurrent) {
		
		String condition = " and  ob.concept_id =" + conceptQuery(FEMALE_SEX_WORKER) + " and ob.value_coded = "
		        + conceptQuery(YES) + "";
		StringBuilder sql = personIdQuery(isCurrent ? getCurrQueryClauses() : getSubQueryClauses(), condition);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("endOnOrAfter", endDate);
		
		if (isCurrent)
			query.setParameterList("drugs", getPrEpDrugs());
		
		return query.list().size();
		
	}
	
	public Integer getDiscordantCoupleOnPrep(Boolean isCurrent) {
		
		String condition = " and  ob.value_coded = " + conceptQuery(DISCORDANT_COUPLE) + "";
		StringBuilder sql = personIdQuery(isCurrent ? getCurrQueryClauses() : getSubQueryClauses(), condition);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("endOnOrAfter", endDate);
		
		if (isCurrent)
			query.setParameterList("drugs", getPrEpDrugs());
		
		return query.list().size();
		
	}
	
	public Set<Integer> getPrEpDrugs() {
		StringBuilder sql = new StringBuilder("select distinct concept_id from concept ");
		sql.append("where uuid in ('" + TDF_TENOFOVIR_DRUG + "','" + TDF_FTC_DRUG + "','" + TDF_3TC_DRUG + "')");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		return new HashSet<Integer>(query.list());
	}
	
	public Integer getCountByExposureType(String uuid) {
		
		String condition = " and ob.concept_id =" + conceptQuery(EXPOSURE_TYPE) + " and ob.value_coded = "
		        + conceptQuery(uuid) + "";
		StringBuilder sql = personIdQuery(getCurrQueryClauses(), condition);
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("endOnOrAfter", endDate);
		
		query.setParameterList("drugs", getPrEpDrugs());
		
		return query.list().size();
		
	}
	
}
