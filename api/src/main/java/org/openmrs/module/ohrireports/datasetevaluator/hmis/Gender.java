package org.openmrs.module.ohrireports.datasetevaluator.hmis;

public enum Gender {
	Female("F"), Male("M");
	
	private final String value;
	
	Gender(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
