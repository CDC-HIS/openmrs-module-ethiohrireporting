package org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.hiv_art_re_arv;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;

import org.hibernate.Query;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivArtReArvQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	public HivArtReArvQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		setSessionFactory(sessionFactory);
		
	}
	
	private Date startDate, endDate;
	
	public void setDates(Date _start, Date _date) {
		startDate = _start;
		endDate = _date;
	}
	
	public Set<Integer> getArvPatients() {
		String subQuery = "and ob.concept_id = (select concept_id from concept where uuid ='" + FOLLOW_UP_STATUS
		        + "' limit 1) and ob.value_coded = (select concept_id from concept where uuid = '" + RESTART + "' limit 1)";
		StringBuilder sql = personIdQuery(arvBaseQuery(), subQuery);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTime("startDate", startDate);
		query.setTime("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	private String arvBaseQuery() {
		return "obs.concept_id=" + conceptQuery(ART_START_DATE)
		        + " and obs.value_datetime >= :startDate and obs.value_datetime <= :endDate ";
	}
}
