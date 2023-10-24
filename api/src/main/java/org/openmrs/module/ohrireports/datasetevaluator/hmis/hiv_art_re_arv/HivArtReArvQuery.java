package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_re_arv;

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
		String outerQuery = "and " + PERSON_ID_ALIAS_OBS + "concept_id = (select concept_id from concept where uuid ='"
		        + FOLLOW_UP_STATUS + "' limit 1) and " + PERSON_ID_ALIAS_OBS
		        + "value_coded = (select concept_id from concept where uuid = '" + RESTART + "' limit 1)";
		
		StringBuilder sql = personIdQuery(arvBaseQuery(), outerQuery);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		
		return new HashSet<Integer>(query.list());
	}
	
	private String arvBaseQuery() {
		return "" + PERSON_ID_SUB_ALIAS_OBS + "concept_id=" + conceptQuery(ART_START_DATE) + " and "
		        + PERSON_ID_SUB_ALIAS_OBS + "value_datetime >= :startDate and " + PERSON_ID_SUB_ALIAS_OBS
		        + "value_datetime <= :endDate ";
	}
}
