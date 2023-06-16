package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.hiv_linkage_new_ct;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.CONFIRMED_REFERRAL;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DIED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FINAL_OUT_COME;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_POSITIVE_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINKED_TO_CARE_TREATMENT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.LOST_TO_FOLLOW_UP;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.STARTED_ART_IN_OTHER_FACILITY;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.STARTED_ART_OR_kNOWN_POSITIVE_ON_ART;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HivLinkageNewCtQuery extends BaseEthiOhriQuery {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	private Date starDate, endDate;
	
	private int totalOnHivePatient, othersCohort;
	
	public int getOthersCohort() {
		return othersCohort;
	}
	
	private Cohort allCohort, linkedToCareCohort, knownCohort, lostToFollowUpCohort, referToOtherCohort, diedCohort;
	
	public void initialize(Date startOnOrAfter, Date endOnOrBefore) {
		
		starDate = startOnOrAfter;
		endDate = endOnOrBefore;
		
		allCohort = getTotalCohort();
		totalOnHivePatient = allCohort.getActiveMemberships().size();
		knownCohort = getKnownOnArt();
		linkedToCareCohort = getLinkedToCareTreatment();
		lostToFollowUpCohort = getLostToFollowUp();
		referToOtherCohort = getReferToOtherFacility();
		diedCohort = getDiedPatientCohort();
		othersCohort = allCohort.getMemberships().size();
	}
	
	public int getTotalOnHivePatient() {
		return totalOnHivePatient;
	}
	
	public Cohort getDiedCohort() {
		return diedCohort;
	}
	
	public Cohort getAllCohort() {
		return allCohort;
	}
	
	public Cohort getLinkedToCareCohort() {
		return linkedToCareCohort;
	}
	
	public Cohort getKnownCohort() {
		return knownCohort;
	}
	
	public Cohort getLostToFollowUpCohort() {
		return lostToFollowUpCohort;
	}
	
	public Cohort getReferToOtherCohort() {
		return referToOtherCohort;
	}
	
	private Cohort getTotalCohort() {
		
		StringBuilder sql = baseQuery(HIV_POSITIVE_DATE);
		
		Query query = addDateRange(sql);
		
		return new Cohort(query.list());
	}
	
	private Cohort getLinkedToCareTreatment() {
		
		Cohort linkedToCohort = new Cohort();
		// Linkage to care treatment:- yes
		fillCohort(linkedToCohort, LINKED_TO_CARE_TREATMENT, YES);
		// Started Art:- yes
		fillCohort(linkedToCohort, STARTED_ART_OR_kNOWN_POSITIVE_ON_ART, YES);
		// final out come:- Started Art
		fillCohort(linkedToCohort, FINAL_OUT_COME, STARTED_ART_OR_kNOWN_POSITIVE_ON_ART);
		
		return linkedToCohort;
	}
	
	private Cohort getKnownOnArt() {
		
		Cohort knownOnArtCohort = new Cohort();
		
		fillCohort(knownOnArtCohort, REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY, STARTED_ART_OR_kNOWN_POSITIVE_ON_ART);
		
		deduplicate(knownOnArtCohort, allCohort);
		
		return knownOnArtCohort;
	}
	
	private Cohort getLostToFollowUp() {
		
		Cohort lostToFollowUpCohort = new Cohort();
		
		fillCohort(lostToFollowUpCohort, FINAL_OUT_COME, LOST_TO_FOLLOW_UP);
		
		deduplicate(lostToFollowUpCohort, allCohort);
		
		return lostToFollowUpCohort;
	}
	
	private Cohort getReferToOtherFacility() {
		
		Cohort referToOtherCohort = new Cohort();
		// Refer to other facility:- confirmed referral,started art in other facility
		fillCohort(referToOtherCohort, FINAL_OUT_COME, Arrays.asList(CONFIRMED_REFERRAL, STARTED_ART_IN_OTHER_FACILITY));
		
		deduplicate(referToOtherCohort, allCohort);
		
		return referToOtherCohort;
	}
	
	private Cohort getDiedPatientCohort() {
		
		Cohort diedCohort = new Cohort();
		
		fillCohort(diedCohort, FINAL_OUT_COME, DIED);
		fillCohort(diedCohort, REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY, DIED);
		
		deduplicate(diedCohort, allCohort);
		
		return diedCohort;
	}
	
	private void deduplicate(Cohort firsCohort, Cohort secondCohort) {
		
		for (CohortMembership cohortMembership : firsCohort.getActiveMemberships()) {
			secondCohort.removeMembership(cohortMembership);
		}
	}
	
	private void fillCohort(Cohort cohort, String question, String answer) {
		
		StringBuilder sqlLinkedToCare = baseQuery(question);
		
		sqlLinkedToCare.append(" and " + OBS_ALIAS + "value_coded= (select  concept_id from concept where uuid= '" + answer
		        + "'  LIMIT 1) ");
		sqlLinkedToCare.append(" and " + OBS_ALIAS + "person_id in (:personIds)");
		
		Query query = addDateRange(sqlLinkedToCare);
		query.setParameter("personIds", allCohort.getMemberIds());
		
		List list = query.list();
		
		for (Object object : list) {
			cohort.addMember((Integer) object);
		}
	}
	
	private void fillCohort(Cohort cohort, String question, List<String> answers) {
		
		StringBuilder sqlLinkedToCare = baseQuery(question);
		
		sqlLinkedToCare.append(" and  " + OBS_ALIAS
		        + "value_coded in (select distinct concept_id from concept where uuid in (:answers)) ");
		sqlLinkedToCare.append(" and " + OBS_ALIAS + "person_id in (:personIds)");
		
		Query query = addDateRange(sqlLinkedToCare);
		
		query.setParameter("personIds", allCohort.getMemberIds());
		query.setParameter("answers", answers);
		
		List list = query.list();
		
		for (Object object : list) {
			cohort.addMember((Integer) object);
		}
	}
	
	private Query addDateRange(StringBuilder sql) {
		sql.append(" and " + OBS_ALIAS + "value_datetime >= :startOnOrAfter ");
		sql.append("and " + OBS_ALIAS + "value_datetime  <=  :endOnOrBefore ");
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString());
		
		query.setTimestamp("startOnOrAfter", starDate);
		query.setTimestamp("endOnOrBefore", endDate);
		
		return query;
	}
}
