package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Component
public class HivLinkageNewCtQuery extends BaseEthiOhriQuery {

	@Autowired
	private DbSessionFactory sessionFactory;
	@Autowired
	private EncounterQuery encounterQuery;
	private List<Integer> baseEncounter;
	List<Linkage> linkageList = new ArrayList<>();

	Integer yesConceptId, knownPositiveConceptId, lostToFollowUpConceptId, conformedReferralConceptId,
			startArtinOtherHFConceptId, referredTxNotInitiatedConceptId,diedConceptId,declinedConceptId,OnAdherencePreparationConceptId,
			onOIManagementConceptId,otherSpecifyConceptId,noConceptId=0;
	private Cohort baseCohort;

	public void initialize(Date startOnOrAfter, Date endOnOrBefore) {
		baseEncounter = encounterQuery.getEncounters(Arrays.asList(HIV_CONFIRMED_DATE), startOnOrAfter, endOnOrBefore, POSITIVE_TRACKING);
		baseCohort = getCohort(baseEncounter);

		for (Integer patientId : baseCohort.getMemberIds()) {
			linkageList.add(new Linkage(patientId));
		}
		setLinkage();
		setConcepts();
	}

	private void setConcepts() {
		StringBuilder sqlBuilder = new StringBuilder("select concept_id,uuid from concept where uuid in (:uuids)");
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.
	}

	private void setLinkage() {
		setLinkage(LINKED_TO_CARE_TREATMENT);
		setLinkage(REASON_FOR_ART_ELIGIBILITY);
		setLinkage(STARTED_ART_OR_kNOWN_POSITIVE_ON_ART);

	}

	private void setLinkage(String conceptUUid) {
		StringBuilder sqlBuilder = new StringBuilder("select ob.person_id,ob.value_coded from obs as ob");
		sqlBuilder.append(" where ob.concept_id =").append(conceptQuery(conceptUUid));
		sqlBuilder.append(" and ob.encounter_id in (:encounters) ");
		sqlBuilder.append(" and ob.person_id in (:personId) ");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());

		query.setParameterList("encounters", baseEncounter);
		query.setParameterList("personId", baseCohort.getMemberIds());

		List list = query.list();
		Object[] objects;
		Integer personId = 0;
		for (Object object : list) {
			objects = (Object[]) object;
			personId = (Integer) objects[0];
			for (Linkage linkage : linkageList) {
				if (linkage.getPerson_id() == personId) {
					linkage.setLinkage(conceptUUid, (Integer) objects[1]);
				}
			}
		}
	}


	private Cohort getCohort(List<Integer> encounterIds) {
		StringBuilder sqlBuilder = new StringBuilder("select distinct (person_id) from obs where encounter_id in (:encounterIds) ");

		Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder.toString());
		query.setParameterList("encounterIds", encounterIds);

		return new Cohort(query.list());

	}

	public int countOfLinkedToCareAndTreatment() {
		int count = 0;
		for (Linkage linkage : linkageList) {
			linkage.getLinkedToCareAndTreatment().
		}
	}

}
