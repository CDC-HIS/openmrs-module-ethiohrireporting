package org.openmrs.module.ohrireports.api.impl.query.cohort;

public enum PMTCTCalculationType {
	//Number of HIV-INFECTED women enrolled in PMTCT in this facility during the month
	IN_FACILITY_ENROLLED,
	//Total number of Transfer in (TI) since Month 0
	ALL_TI,
	//Total number of Transfer out (TO) since Month 0
	ALL_TO,
	//Number of mothers in the current cohort = Net current cohort (IN_FACILITY_ENROLLED+ALL_TI -All_TO)
	NET_CURRENT_COHORT,
	//Mother Alive and on ART
	MOTHER_ALIVE_AND_ON_ART,
	//Lost to F/U (not seen>1 month after scheduled appointment)
	LOST_TO_FOLLOW_UP,
	//Known dead
	KNOWN_DEAD,
	//% of mothers in net current cohort Alive and on ART [(MOTHER_ALIVE_AND_ON_ART/NET_CURRENT_COHORT)*100
	PERCENTAGE_NET_CURRENT_COHORT_ALIVE_AND_ON_ART,
	//% of mothers in net current cohort Alive and on ART [(LOST_TO_FOLL_UP/NET_CURRENT_COHORT)*100
	PERCENTAGE_NET_CURRENT_COHORT_LOST_TO_FOLLOW_UP,
}
