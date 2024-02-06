package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct_fo;

import org.openmrs.Cohort;

public class PMTCTFO {
	
	private final Cohort hivInfected;
	
	private final Cohort hivUninfected;
	
	private final Cohort hivFinalStatusUnknown;
	
	private final Cohort diedWithOutStatusknown;
	
	public PMTCTFO(Cohort hivInfected, Cohort hivUninfected, Cohort hivFinalStatusUnknown, Cohort diedWithOutStatusknown) {
		this.hivInfected = hivInfected;
		this.hivUninfected = hivUninfected;
		this.hivFinalStatusUnknown = hivFinalStatusUnknown;
		this.diedWithOutStatusknown = diedWithOutStatusknown;
	}
	
	public Cohort getHivInfected() {
		return hivInfected;
	}
	
	public Cohort getHivUninfected() {
		return hivUninfected;
	}
	
	public Cohort getHivFinalStatusUnknown() {
		return hivFinalStatusUnknown;
	}
	
	public Cohort getDiedWithOutStatusknown() {
		return diedWithOutStatusknown;
	}
	
	public int getTotal() {
		return hivInfected.size() + hivUninfected.size() + hivFinalStatusUnknown.size() + diedWithOutStatusknown.size();
	}
}
