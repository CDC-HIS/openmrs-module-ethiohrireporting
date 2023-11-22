package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.CXC_SCREENING_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATE_COUNSELING_GIVEN;

@Component
public class CervicalCancerQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	private Cohort baseCohort;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	private List<Integer> baseEncounter;
	
	private List<Integer> currentEncounter;
	
	private Date startDate;
	
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
		currentEncounter = encounterQuery.getLatestDateByFollowUpDate(endDate);
	}
	
	private Date endDate;
	
	@Autowired
	public CervicalCancerQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public Cohort loadScreenedCohort() {
		//baseEncounter = encounterQuery.getEncounters(Arrays.asList(CXC_SCREENING_DATE),getStartDate(),getEndDate());
		//List<Integer> onArtEncounters =  encounterQuery.getAliveFollowUpEncounters(getEndDate());
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(DATE_COUNSELING_GIVEN), getStartDate(), getEndDate());
		
		StringBuilder stringBuilder = baseQuery(DATE_COUNSELING_GIVEN);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseEncounter);
		
		baseCohort = new Cohort(query.list());
		return baseCohort;
	}
	
	public List<Integer> getCurrentEncounter() {
		return currentEncounter;
	}
}
