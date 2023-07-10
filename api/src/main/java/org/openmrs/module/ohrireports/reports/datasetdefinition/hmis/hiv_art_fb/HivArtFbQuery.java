package org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_fb;

import java.util.Date;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivArtFbQuery extends PatientQueryImpDao {
   
    private DbSessionFactory sessionFactory;

    private Date startDate,endDate;
    @Autowired
    public HivArtFbQuery(DbSessionFactory sessionFactory) {

        setSessionFactory(sessionFactory);

        this.sessionFactory = sessionFactory;
    }

    public void setDate(Date start,Date end){
        startDate = start;
        endDate = end;
    }

    public Set<Integer> GetPatientsOnFamilyPlanning(){
        Cohort cohort  = getOnArtCohorts("",startDate,endDate,null);

    }
}
