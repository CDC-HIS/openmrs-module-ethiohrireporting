package org.openmrs.module.ohrireports.datasetevaluator.linelist.pmtct;

import java.util.Date;

public class PMTCTPatientRapidAntiBody {
	
	private int personId;
	
	private String fullName;
	
	private String mrn;
	
	private String gender;
	
	private int age;
	
	private Date followUpDate;
	
	private String rabidAntiBodyResult;
	
	private String heiCode;
	
	public PMTCTPatientRapidAntiBody(int personId, String fullName, String mrn, String gender, int age, Date followUpDate,
	    String rabidAntiBodyResult) {
		this.personId = personId;
		this.fullName = fullName;
		this.mrn = mrn;
		this.gender = gender;
		this.age = age;
		this.followUpDate = followUpDate;
		this.rabidAntiBodyResult = rabidAntiBodyResult;
	}
	
	public void setHeiCode(String heiCode) {
		this.heiCode = heiCode;
	}
	
	public int getPersonId() {
		return personId;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getMrn() {
		return mrn;
	}
	
	public String getGender() {
		return gender;
	}
	
	public int getAge() {
		return age;
	}
	
	public Date getFollowUpDate() {
		return followUpDate;
	}
	
	public String getRabidAntiBodyResult() {
		return rabidAntiBodyResult;
	}
	
	public String getHeiCode() {
		return heiCode;
	}
}
