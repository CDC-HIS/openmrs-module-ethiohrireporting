package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.constants.EncounterType;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ARTPatientListQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Cohort baseCohort;
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Integer> baseEncounter;
	
	@Autowired
	public ARTPatientListQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public void setBaseCohort(Cohort baseCohort) {
		this.baseCohort = baseCohort;
	}
	
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
		baseEncounter = encounterQuery.getLatestDateByEnrollmentDate(null, endDate, EncounterType.INTAKE_A_ENCOUNTER_TYPE);
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	public Cohort getEverEnrolledCohort(Date endOrBefore) {
		StringBuilder sql = baseQuery(FollowUpConceptQuestions.ART_REGISTRATION_DATE, EncounterType.INTAKE_A_ENCOUNTER_TYPE);
		if (endOrBefore != null)
			sql.append("and ").append(OBS_ALIAS).append("value_datetime <= :end ");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		if (endOrBefore != null)
			query.setTimestamp("end", endOrBefore);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
}
