package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_treatment;

import org.openmrs.Cohort;

public class CxCaTreatment {
	
	private String type;
	
	private final Cohort cryotherapyCohort;
	
	private final Cohort leepCohort;
	
	private final Cohort thermocoagulationCohort;
	
	public CxCaTreatment(String type, Cohort cryotherapyCohort, Cohort leepCohort, Cohort thermocoagulationCohort) {
		this.type = type;
		this.cryotherapyCohort = cryotherapyCohort;
		this.leepCohort = leepCohort;
		this.thermocoagulationCohort = thermocoagulationCohort;
	}
	
	public String getType() {
		return type;
	}
	
	public Cohort getCryotherapyCohort() {
		return cryotherapyCohort;
	}
	
	public Cohort getLeepCohort() {
		return leepCohort;
	}
	
	public Cohort getThermocoagulationCohort() {
		return thermocoagulationCohort;
	}
	
	public int getTotal() {
		return cryotherapyCohort.size() + leepCohort.size() + thermocoagulationCohort.size();
	}
}
