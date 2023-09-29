package org.openmrs.module.ohrireports.reports.datasetevaluator;

import java.util.List;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REGIMEN;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.openmrs.module.ohrireports.helper.EthiopianDate;
import org.openmrs.module.ohrireports.helper.EthiopianDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtQuery extends BaseEthiOhriQuery {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	public Dictionary<Integer, Object> getRegiment(Cohort cohort, Date startDate, Date endDate) {

        List list = getObs(cohort, startDate, endDate, REGIMEN, baseConceptQuery(REGIMEN));

        Dictionary<Integer, Object> dictionary = new Hashtable<>();
        int personId = 0;
        Object[] objects;
        for (Object object : list) {
            objects = (Object[]) object;
            personId = (Integer) objects[0];

            if (dictionary.get((Integer) personId) == null) {
                dictionary.put(personId, objects[1]);
            }

        }

        return dictionary;

    }
	
	public Dictionary<Integer, Object> getArtStartDate(Cohort cohort, Date startDate, Date endDate) {

        StringBuilder sql = baseValueDateQuery(ART_START_DATE);
        sql.append("and  ob.value_datetime >= :start and ob.value_datetime <= :end and ob.person_id in (:patient_ids)");
        sql.append("and ob.obs_id in ");
        sql.append(baseSubQuery(
                " ob.value_datetime >= :startOnOrAfter and ob.value_datetime <= :endOnOrBefore and ob.person_id in (:patientIds) and ob.concept_id = "
                        + conceptQuery(ART_START_DATE))
                .toString());

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
        
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        query.setParameterList("patient_ids", cohort.getMemberIds());
        query.setParameter("startOnOrAfter", startDate);
        query.setParameter("endOnOrBefore", endDate);
        query.setParameterList("patientIds", cohort.getMemberIds());

        List list = query.list();
        Dictionary<Integer, Object> dictionary = new Hashtable<>();
        int personId = 0;
        Object[] objects;
        for (Object object : list) {
            objects = (Object[]) object;
            personId = (Integer) objects[0];

            if (dictionary.get((Integer) personId) == null) {
                dictionary.put(personId, objects[1]);
            }

        }

        return dictionary;

    }
	
	private List getObs(Cohort cohort, Date startDate, Date endDate, String concept, StringBuilder baseSql) {
		StringBuilder sql = baseSql;
		sql.append("and ob.obs_id in ");
		sql.append(baseSubQuery(
		    " ob.obs_datetime >= :startOnOrAfter and ob.obs_datetime <= :endOnOrBefore and ob.person_id in (:patientIds) and ob.concept_id = "
		            + conceptQuery(concept)).toString());
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setParameter("startOnOrAfter", startDate);
		query.setParameter("endOnOrBefore", endDate);
		query.setParameterList("patientIds", cohort.getMemberIds());
		
		return query.list();
	}
	
	public Date getDate(Object object) {
		if (object instanceof Date) {
			Date date = (Date) object;
			return date;
		}
		
		return null;
	}
	
	@Override
	protected StringBuilder personIdQuery(java.lang.String conditions, java.lang.String outerQuery) {
		// TODO Auto-generated method stub
		return super.personIdQuery(conditions, outerQuery);
	}
	
	String getEthiopianDate(Date date) {
		if (date == null) {
			return "--";
		}
		LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		EthiopianDate ethiopianDate = null;
		try {
			ethiopianDate = EthiopianDateConverter.ToEthiopianDate(lDate);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ethiopianDate == null ? "" : ethiopianDate.getDay() + "/" + ethiopianDate.getMonth() + "/"
		        + ethiopianDate.getYear();
	}
}
