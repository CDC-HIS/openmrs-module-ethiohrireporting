package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

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
			case LINKED_TO_CARE_TREATMENT:
				linkedToCareAndTreatment = new LinkageType(conceptId, name, conceptUUid);
				break;
			case STARTED_ART:
				startedArt = new LinkageType(conceptId, name, conceptUUid);
				break;
			case REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY:
				reasonForNotStartedArt = new LinkageType(conceptId, name, conceptUUid);
				break;
			case FINAL_OUT_COME:
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
