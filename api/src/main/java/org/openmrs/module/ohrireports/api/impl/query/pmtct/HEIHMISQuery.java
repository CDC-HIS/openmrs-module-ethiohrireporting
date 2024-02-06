package org.openmrs.module.ohrireports.api.impl.query.pmtct;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.PatientQueryImpDao;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISUtilies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class HEIHMISQuery extends PatientQueryImpDao {
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	List<Integer> baseEncounter;
	
	private Cohort baseCohort;
	
	private HashMap<Integer, Object> hashMap;
	
	private DbSessionFactory sessionFactory;
	
	public HEIHMISQuery(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(sessionFactory);
	}
	
	public List<Integer> getBaseEncounter() {
		return baseEncounter;
	}
	
	public Cohort getBaseCohort() {
		return baseCohort;
	}
	
	public HashMap<Integer, Object> getHashMap() {
		return hashMap;
	}
	
	public void generateHMISCORT(Date start, Date end){
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(PMTCT_FOLLOW_UP_DATE),null,end,PMTCT_CHILD_FOLLOW_UP_ENCOUNTER_TYPE);
		baseCohort = new Cohort();
		hashMap = getHEIWithCotrimoxazole(baseEncounter);
		hashMap.forEach((k,i)->{
			if(Objects.nonNull(i)){
				int age = EthiOhriUtil.getAgeInMonth((Date) i,end);
				if(age<=2){
					baseCohort.addMembership(new CohortMembership(k));
					
				}
			}
			
		});
		
	}
	
	public void generateHMISCONFORMATORY(Date start, Date end){
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(PMTCT_CONFORMATORY_TEST_DONE),null,end,PMTCT_CHILD_FOLLOW_UP_ENCOUNTER_TYPE);
		baseCohort = new Cohort();
		hashMap = getHEIRapidConformatoryTest(baseEncounter,end);
		hashMap.forEach((k,i)->{
			baseCohort.addMembership(new CohortMembership(k));
		});
		
	}
	
	public int getCountForRapidAntiBodyTestByResult(String conceptUUID) {
		int count = 0;
		for (Map.Entry<Integer, Object> entry : hashMap.entrySet()) {
			if (Objects.nonNull(entry.getValue())) {
				if (((String) entry.getValue()).equals(conceptUUID)) {
					count++;
				}
			}
		}
		return count;
	}
	
	private HashMap<Integer, Object> getHEIRapidConformatoryTest(List<Integer> baseEncounter, Date reportEnd) {
		StringBuilder sqlBuilder = new StringBuilder("select distinct ob.person_id,c.uuid from obs as ob");
		sqlBuilder.append(" inner  join person as p on p.person_id = ob.person_id ");
		sqlBuilder.append(" inner  join concept_name as c on c.concept_id = ob.value_coded ");
		sqlBuilder.append(" where ob.encounter_id in (:encounterIds) and ob.concept_id = ").append(
		    conceptQuery(PMTCT_RAPID_ANTIBODY_RESULT));
		sqlBuilder.append(" and ob.value_coded is not null");
		sqlBuilder
		        .append(" and PERIOD_DIFF(EXTRACT(YEAR_MONTH FROM (:reportEnd)), EXTRACT(YEAR_MONTH FROM p.birthDate)) <=18");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", baseEncounter);
		query.setDate("reportEnd", reportEnd);
		return HMISUtilies.getDictionary(query);
	}
	
	public void generateHMISIARV(Date start, Date end){
		baseEncounter = encounterQuery.getEncounters(Collections.singletonList(PMTCT_CHILD_ENROLLMENT_DATE),null,end,PMTCT_CHILD_ENROLLMENT_ENCOUNTER_TYPE);
		baseCohort = new Cohort();
		hashMap = getHEIARVProphylaxis(baseEncounter);
		hashMap.forEach((k,i)->{
			baseCohort.addMembership(new CohortMembership(k));
		});
	}
	
	private HashMap<Integer, Object> getHEIWithCotrimoxazole(List<Integer> baseEncounter) {
		StringBuilder sqlBuilder = new StringBuilder("select distinct ob.person_id,p.birthdate from obs as ob");
		sqlBuilder.append(" inner  join person as p on p.person_id = ob.person_id ");
		sqlBuilder.append(" where ob.encounter_id in (:encounterIds) and ob.concept_id = ").append(conceptQuery(PMTCT_DOSE));
		sqlBuilder.append(" and ob.value_coded is not null");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", baseEncounter);
		return HMISUtilies.getDictionary(query);
	}
	
	private HashMap<Integer, Object> getHEIARVProphylaxis(List<Integer> baseEncounter) {
		StringBuilder sqlBuilder = new StringBuilder("select distinct ob.person_id,p.birthdate from obs as ob");
		sqlBuilder.append(" inner  join person as p on p.person_id = ob.person_id ");
		sqlBuilder.append(" where ob.encounter_id in (:encounterIds) and ob.concept_id = ").append(
		    conceptQuery(PMTCT_ARV_PROPHYLAXIS_STARTED));
		sqlBuilder.append(" and ob.value_coded is not null");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", baseEncounter);
		return HMISUtilies.getDictionary(query);
	}
	
}
