package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_ret;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TRANSFERRED_UUID;

@Component
public class HivArtRetQuery extends PatientQueryImpDao {
	
	private static final int MONTHS_IN_YEAR = 12;
	private Date startDate, endDate = new Date();
	@Autowired
	private DbSessionFactory sessionFactory;
	@Autowired
	private EncounterQuery encounterQuery;
	List<Integer> retEncounter = new ArrayList<>();
	List<Integer> netRetEncounter = new ArrayList<>();
	public Cohort retCohort = new Cohort();
	public Cohort netRetCohort = new Cohort();

	public void initializeRetentionCohort(Date startOnOrAfter, Date endOnOrBefore) {
		if (getSessionFactory() == null)
			this.setSessionFactory(sessionFactory);
		setCalculatedDate(startOnOrAfter, endOnOrBefore);
		retEncounter = encounterQuery.getAliveFollowUpEncounters(null, endOnOrBefore);
		netRetEncounter = encounterQuery.getLatestDateByFollowUpDate(null, endOnOrBefore);
		setPatientRetentionCohortNet();
		setPatientRetentionCohort(endOnOrBefore);
	}

	private void setPatientRetentionCohort(Date endOnOrBefore) {
		retCohort = getActiveOnArtCohort("", null, endOnOrBefore, netRetCohort, retEncounter);
	}

	private void setPatientRetentionCohortNet() {
		netRetCohort =new Cohort(getArtStartedCohort(netRetEncounter, startDate, endDate, null));
	}

	public double getPercentage() {
		if(retCohort.size()==0)
			return 0;
		return (netRetCohort.size() / retCohort.size()) / 100;
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
