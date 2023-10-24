package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_prev_denominator;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_START_DATE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TbPrevQuery extends BaseEthiOhriQuery {
	
	private int MONTH_TO_BE_DEDUCT = -6;
	
	private Date _startDate;
	
	private Date _endDate;
	
	private PatientQueryService _patientQuery;
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	// start date it six month prior the reporting start date
	public void instantiate(Date startDate, Date endDate) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.MONTH, MONTH_TO_BE_DEDUCT);
		_startDate = calendar.getTime();
		_endDate = endDate;
		_patientQuery = Context.getService(PatientQueryService.class);
		
	}
	
	// We don't consider patients if wether they are on ART or not, for counting
	public Set<Integer> getOnTbPrevPatientId() {
        Set<Integer> _patientIds = new HashSet<>();

        StringBuilder qBuilder = baseQuery(TPT_START_DATE);
        qBuilder.append(" and obs_datetime >= :startOnOrAfter and obs_datetime <= :endOnOrBefore");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(qBuilder.toString());

        query.setParameter("startOnOrAfter", _startDate);
        query.setParameter("endOnOrBefore", _endDate);

        List list = query.list();

        _patientIds.addAll(list);

        return _patientIds;
    }
	
	public Set<Integer> getOnTbPrevPatientId(Cohort cohort) {
        Set<Integer> _patientIds = new HashSet<>();

        StringBuilder qBuilder = baseQuery(TPT_START_DATE);
        qBuilder.append(" and obs_datetime>= :startOnOrAfter and obs_datetime<= :endOnOrBefore  ");
        qBuilder.append(" and " + OBS_ALIAS + "person_id in (:personIds)");
        Query query = sessionFactory.getCurrentSession().createSQLQuery(qBuilder.toString());

        query.setParameter("startOnOrAfter", _startDate);
        query.setParameter("endOnOrBefore", _endDate);
        query.setParameter("personIds", cohort.getMemberIds());
        List list = query.list();

        _patientIds.addAll(list);

        return _patientIds;
    }
	
	public List<Person> getOnArtNewAndOnTbPrevPatient(String gender) {
        List<Person> _personList = new ArrayList<>();

        Cohort cohort = _patientQuery.getNewOnArtCohort(gender, _startDate, _endDate, null);
        Set<Integer> onTbPrevPatients = getOnTbPrevPatientId(cohort);

        _personList = _patientQuery.getPersons(new Cohort(onTbPrevPatients));

        return _personList;

    }
	
	public List<Person> getOnArtOldAndOnTbPrevPatient(String gender) {
        List<Person> _personList = new ArrayList<>();
        Calendar cl = Calendar.getInstance();
        cl.setTime(_startDate);
        cl.add(Calendar.DATE, -1);

        Cohort cohort = _patientQuery.getNewOnArtCohort(gender, cl.getTime(), null, null);
        Set<Integer> onTbPrevPatients = getOnTbPrevPatientId(cohort);

        _personList = _patientQuery.getPersons(new Cohort(onTbPrevPatients));

        return _personList;

    }
}
