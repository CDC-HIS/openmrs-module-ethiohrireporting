package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class TransferInOutQuery extends PatientQueryImpDao {
	
	private DbSessionFactory sessionFactory;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	private Cohort baseCohort;
	
	private Cohort toCohort;
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Integer> baseEncounter;
	
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
		this.toCohort = toCohort;
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
		firstEncounter = encounterQuery.getFirstEncounterByFollowUpDate(startDate, endDate);
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
		StringBuilder stringBuilder = baseQuery(FOLLOW_UP_STATUS);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(TRANSFERRED_OUT_UUID));
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters)");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", baseEncounter);
		
		return new Cohort(query.list());
	}
	
	public Cohort getTICohort() {
		StringBuilder stringBuilder = baseQuery(REASON_FOR_ART_ELIGIBILITY);
		
		stringBuilder.append(" and ").append(OBS_ALIAS).append("value_coded = ").append(conceptQuery(TRANSFERRED_IN));
		stringBuilder.append(" and ").append(OBS_ALIAS).append("encounter_id in (:encounters) ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(stringBuilder.toString());
		query.setParameterList("encounters", firstEncounter);
		
		return new Cohort(query.list());
	}
	
}
