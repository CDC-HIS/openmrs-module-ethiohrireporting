package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TransferInOutQuery extends PatientQueryImpDao {
	
	private final DbSessionFactory sessionFactory;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Cohort baseCohort;
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Integer> baseEncounter;
	
	public List<Integer> getBeforeLastEncounter() {
		return beforeLastEncounter;
	}
	
	public void setBeforeLastEncounter(List<Integer> beforeLastEncounter) {
		this.beforeLastEncounter = beforeLastEncounter;
	}
	
	private List<Integer> beforeLastEncounter;
	
	public List<Integer> getLastEncounter() {
		return LastEncounter;
	}
	
	public void setLastEncounter(List<Integer> lastEncounter) {
		LastEncounter = lastEncounter;
	}
	
	private List<Integer> LastEncounter;
	
	private List<Integer> tiEncounter;
	
	private List<Integer> firstEncounter;
	
	private String status;
	
	@Autowired
	public TransferInOutQuery(DbSessionFactory _SessionFactory) {
		sessionFactory = _SessionFactory;
		setSessionFactory(sessionFactory);
	}
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public void setBaseCohort(Cohort baseCohort) {
		this.baseCohort = baseCohort;
	}
	
	public void setToCohort(Cohort toCohort) {
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
		baseEncounter = encounterQuery.getLatestDateByFollowUpDate(startDate, endDate);
		beforeLastEncounter = encounterQuery.getSecondLatestFollowUp(endDate);
		LastEncounter = encounterQuery.getLatestDateByFollowUpDate(null, new Date());
		firstEncounter = encounterQuery.getFirstEncounterByObsDate(startDate, endDate, FOLLOW_UP_DATE);
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	public List<Integer> getTiEncounter() {
		return tiEncounter;
	}
	
	public List<Integer> getFirstEncounter() {
		return firstEncounter;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Cohort getTOCohort() {
		StringBuilder stringBuilder = baseQuery(FollowUpConceptQuestions.FOLLOW_UP_STATUS);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ")
		        .append(conceptQuery(ConceptAnswer.TRANSFERRED_OUT_UUID));
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseEncounter);
		
		return new Cohort(query.list());
	}
	
	public Cohort getTICohort() {
		StringBuilder stringBuilder = baseQuery(FollowUpConceptQuestions.REASON_FOR_ART_ELIGIBILITY);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ")
		        .append(conceptQuery(ConceptAnswer.TRANSFERRED_IN));
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", firstEncounter);
		
		return new Cohort(query.list());
	}
	
}
