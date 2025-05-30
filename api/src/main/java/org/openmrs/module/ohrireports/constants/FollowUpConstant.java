package org.openmrs.module.ohrireports.constants;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public class FollowUpConstant {
	
	private static final String ALL = "All";
	
	private static final String ALIVE = "Alive";
	
	private static final String TO = "Transfer out";
	
	private static final String STOP = "Stop";
	
	private static final String LOST = "Lost";
	
	private static final String RESTART = "Restart";
	
	private static final String DROP = "Drop";
	
	private static final String DEAD = "Dead";
	
	public static String getListOfStatus() {
		return StringUtils.collectionToDelimitedString(Arrays.asList(ALL, ALIVE, TO, STOP, LOST, RESTART, DROP, DEAD), ",");
	}
	
	public static String getUuidRepresentation(String followupStatus) {
		switch (followupStatus) {
			case ALIVE:
				return ConceptAnswer.ALIVE;
			case TO:
				return ConceptAnswer.TRANSFERRED_OUT_UUID;
			case STOP:
				return ConceptAnswer.STOP;
			case LOST:
				return ConceptAnswer.LOST_TO_FOLLOW_UP;
			case RESTART:
				return ConceptAnswer.RESTART;
			case DEAD:
				return ConceptAnswer.DEAD;
			case DROP:
				return ConceptAnswer.DROP;
			default:
				return "all";
		}
	}
}
