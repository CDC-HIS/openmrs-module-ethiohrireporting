package org.openmrs.module.ohrireports.api.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PMTCTPatient {
	
	private String fullName;
	
	private String gender;
	
	private String heiCode;
	
	private String mrn;
	
	private Date enrolledDate;
	
	private final int patientId;
	
	private final Date birthDate;
	
	private final List<PMTCTEncounter> encounterList;
	
	public PMTCTPatient(String fullName, String gender, String heiCode, String mrn, Date enrolledDate, int patientId, Date birthDate) {
		this.fullName = fullName;
		this.gender = gender;
		this.heiCode = heiCode;
		this.mrn = mrn;
		this.enrolledDate = enrolledDate;
		this.patientId = patientId;
		this.birthDate = birthDate;
		this.encounterList = new ArrayList<>();
	}
	
	public PMTCTPatient(int patientId, Date birthDate) {
		this.patientId = patientId;
		this.birthDate = birthDate;
		this.encounterList = new ArrayList<>();
	}
	
	public int getPatientId() {
		return patientId;
	}
	
	public Date getBirthDate() {
		return birthDate;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public String getGender() {
		return gender;
	}
	
	public String getHeiCode() {
		return heiCode;
	}
	
	public String getMrn() {
		return mrn;
	}
	
	public Date getEnrolledDate() {
		return enrolledDate;
	}
	
	public List<PMTCTEncounter> getEncounterList() {
		return encounterList;
	}
	
	public void addEncounter(PMTCTEncounter newEncounter) {
		encounterList.add(newEncounter);
	}
	
}
