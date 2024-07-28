package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct;

import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.constants.PositiveCaseTrackingConceptQuestions;

public class Linkage {
	
	private int personId;
	
	private LinkageType linkedToCareAndTreatment = new LinkageType();
	
	private LinkageType reasonForNotStartedArt = new LinkageType();
	
	private LinkageType finalOutCome = new LinkageType();
	
	private LinkageType startedArt = new LinkageType();
	
	public int getPersonId() {
		return personId;
	}
	
	public LinkageType getLinkedToCareAndTreatment() {
		return linkedToCareAndTreatment;
	}
	
	public LinkageType getReasonForNotStartedArt() {
		return reasonForNotStartedArt;
	}
	
	public LinkageType getFinalOutCome() {
		return finalOutCome;
	}
	
	public LinkageType getStartedArt() {
		return startedArt;
	}
	
	public void setLinkage(String conceptUUid, Integer conceptId, String name) {
		switch (conceptUUid) {
			case PositiveCaseTrackingConceptQuestions.LINKED_TO_CARE_TREATMENT:
				linkedToCareAndTreatment = new LinkageType(conceptId, name, conceptUUid);
				break;
			case FollowUpConceptQuestions.STARTED_ART:
				startedArt = new LinkageType(conceptId, name, conceptUUid);
				break;
			case PositiveCaseTrackingConceptQuestions.REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY:
				reasonForNotStartedArt = new LinkageType(conceptId, name, conceptUUid);
				break;
			case PositiveCaseTrackingConceptQuestions.FINAL_OUT_COME:
				finalOutCome = new LinkageType(conceptId, name, conceptUUid);
				break;
			default:
				break;
		}
	}
	
	public Linkage(int _personId) {
		personId = _personId;
	}
	
}
