package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class HivArtRetQuery extends PatientQueryImpDao {
	
	private static final int MONTHS_IN_YEAR = 12;
	private Date startDate, endDate = new Date();
	@Autowired
	private DbSessionFactory sessionFactory;
	@Autowired
	private EncounterQuery encounterQuery;
	private List<Integer> retEncounter = new ArrayList<>();
	private List<Integer> netRetEncounter = new ArrayList<>();
	private Cohort retCohort = new Cohort();
	private Cohort netRetCohort = new Cohort();
	
	public List<Integer> getRetEncounter() {
		return retEncounter;
	}
	
	public List<Integer> getNetRetEncounter() {
		return netRetEncounter;
	}
	
	public Cohort getRetCohort() {
		return retCohort;
	}
	
	public Cohort getNetRetCohort() {
		return netRetCohort;
	}
	
	public void initializeRetentionCohort(Date startOnOrAfter, Date endOnOrBefore) {
		if (getSessionFactory() == null)
			this.setSessionFactory(sessionFactory);
		setCalculatedDate(startOnOrAfter, endOnOrBefore);
		this.retEncounter = encounterQuery.getAliveFollowUpEncounters(null,endOnOrBefore);
		this.netRetEncounter = encounterQuery.getLatestDateByFollowUpDate(null,endOnOrBefore);
		setPatientRetentionCohortNet();
		setPatientRetentionCohort(endOnOrBefore);
	}

	private void setPatientRetentionCohort(Date endOnOrBefore) {
		retCohort = getActiveOnArtCohort("", null, endOnOrBefore, netRetCohort, retEncounter);
	}

	private void setPatientRetentionCohortNet() {
		netRetCohort = removeTransferredOutPatients();
		netRetCohort =new Cohort(getArtStartedCohort(netRetEncounter, startDate, endDate, netRetCohort));
	}
	
	private Cohort removeTransferredOutPatients() {
		StringBuilder sqlBuilder = new StringBuilder("select person_id from obs where ");
		sqlBuilder.append(" encounter_id in (:encounters) and concept_id = ").append(conceptQuery(FOLLOW_UP_STATUS));
		sqlBuilder.append(" and value_coded <> ").append(conceptQuery(TRANSFERRED_OUT_UUID));
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounters",netRetEncounter);
		return new Cohort(query.list());
	}
	
	public double getPercentage() {
		if(retCohort.isEmpty() || netRetCohort.isEmpty())
			return 0;
		double value = (retCohort.size()*100.00)/netRetCohort.size() ;
		DecimalFormat decimalFormat = new DecimalFormat("###.##");
		return  Double.parseDouble(decimalFormat.format(value));
	}

	public void setCalculatedDate(Date _startDate, Date _endDate) {
		Calendar startInstance = Calendar.getInstance();
		Calendar endInstance = Calendar.getInstance();

		startInstance.setTime(_startDate);
		endInstance.setTime(_endDate);

		startInstance.add(Calendar.MONTH, -MONTHS_IN_YEAR);
		endInstance.add(Calendar.MONTH, -MONTHS_IN_YEAR);
		startDate = startInstance.getTime();
		endDate = endInstance.getTime();

	}
}
