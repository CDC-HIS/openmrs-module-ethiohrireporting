package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn;

import org.openmrs.Cohort;

public class CxcaScreening {
	
	private String type;
	
	private Cohort negivetCohort;
	
	private Cohort positiveCohort;
	
	private Cohort suspectedCohort;
	
	public CxcaScreening(String type, Cohort negivetCohort, Cohort positiveCohort, Cohort suspectedCohort) {
		this.type = type;
		this.negivetCohort = negivetCohort;
		this.positiveCohort = positiveCohort;
		this.suspectedCohort = suspectedCohort;
		
	}
	
	public String getType() {
		return type;
	}
	
	public Cohort getNegivetCohort() {
		return negivetCohort;
	}
	
	public Cohort getPositiveCohort() {
		return positiveCohort;
	}
	
	public Cohort getSuspectedCohort() {
		return suspectedCohort;
	}
	
	public int getTotal() {
		return negivetCohort.size() + positiveCohort.size() + suspectedCohort.size();
	}
}
