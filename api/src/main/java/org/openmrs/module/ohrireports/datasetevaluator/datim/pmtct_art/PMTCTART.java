package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct_art;

import org.openmrs.Cohort;

public class PMTCTART {
	
	private final Cohort newOnARTCohort;
	
	private final Cohort alreadyOnARTCohort;
	
	public PMTCTART(Cohort newOnARTCohort, Cohort alreadyOnARTCohort) {
		this.newOnARTCohort = newOnARTCohort;
		this.alreadyOnARTCohort = alreadyOnARTCohort;
	}
	
	public Cohort getNewOnARTCohort() {
		return newOnARTCohort;
	}
	
	public Cohort getAlreadyOnARTCohort() {
		return alreadyOnARTCohort;
	}
}
