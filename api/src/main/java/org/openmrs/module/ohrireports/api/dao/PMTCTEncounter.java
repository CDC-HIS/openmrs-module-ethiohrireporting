package org.openmrs.module.ohrireports.api.dao;

import java.util.Date;

public class PMTCTEncounter {
	
	private Date sampleDate;
	
	private Date followUpDate;
	
	private Integer encounterId;
	
	private String testType;
	
	private String arvProphylaxis;
	
	private String maternalArtStatus;
	
	private String testIndication;
	
	private String specimenType;
	
	private Date dateOfSampleCollection;
	
	private String dnaPcrResult;
	
	private Date dateOfResultByHf;
	
	private Date dateOfDbsReferralRegionalLab;
	
	private String nameOfTestingLab;
	
	private Date dateOfSampleReceived;
	
	private String sampleQuality;
	
	private String reasonForSampleRejection;
	
	private Date dateTestPerformedByLab;
	
	private String platformUsed;
	
	private int age;
	
	public PMTCTEncounter(Date sampleDate, Integer encounterId, String testType, int age) {
		this.sampleDate = sampleDate;
		this.encounterId = encounterId;
		this.testType = testType;
		this.age = age;
	}
	
	public PMTCTEncounter(Date sampleDate, Date followUpDate, Integer encounterId, String testType, String arvProphylaxis,
	    String maternalArtStatus, String testIndication, String specimenType, Date dateOfSampleCollection,
	    String dnaPcrResult, Date dateOfResultByHf, Date dateOfDbsReferralRegionalLab, String nameOfTestingLab,
	    Date dateOfSampleReceived, String sampleQuality, String reasonForSampleRejection, Date dateTestPerformedByLab,
	    String platformUsed, int age) {
		this.sampleDate = sampleDate;
		this.followUpDate = followUpDate;
		this.encounterId = encounterId;
		this.testType = testType;
		this.arvProphylaxis = arvProphylaxis;
		this.maternalArtStatus = maternalArtStatus;
		this.testIndication = testIndication;
		this.specimenType = specimenType;
		this.dateOfSampleCollection = dateOfSampleCollection;
		this.dnaPcrResult = dnaPcrResult;
		this.dateOfResultByHf = dateOfResultByHf;
		this.dateOfDbsReferralRegionalLab = dateOfDbsReferralRegionalLab;
		this.nameOfTestingLab = nameOfTestingLab;
		this.dateOfSampleReceived = dateOfSampleReceived;
		this.sampleQuality = sampleQuality;
		this.reasonForSampleRejection = reasonForSampleRejection;
		this.dateTestPerformedByLab = dateTestPerformedByLab;
		this.platformUsed = platformUsed;
		this.age = age;
	}
	
	public Date getFollowUpDate() {
		return followUpDate;
	}
	
	public String getArvProphylaxis() {
		return arvProphylaxis;
	}
	
	public String getMaternalArtStatus() {
		return maternalArtStatus;
	}
	
	public String getTestIndication() {
		return testIndication;
	}
	
	public String getSpecimenType() {
		return specimenType;
	}
	
	public Date getDateOfSampleCollection() {
		return dateOfSampleCollection;
	}
	
	public String getDnaPcrResult() {
		return dnaPcrResult;
	}
	
	public Date getDateOfResultByHf() {
		return dateOfResultByHf;
	}
	
	public Date getDateOfDbsReferralRegionalLab() {
		return dateOfDbsReferralRegionalLab;
	}
	
	public String getNameOfTestingLab() {
		return nameOfTestingLab;
	}
	
	public Date getDateOfSampleReceived() {
		return dateOfSampleReceived;
	}
	
	public String getSampleQuality() {
		return sampleQuality;
	}
	
	public String getReasonForSampleRejection() {
		return reasonForSampleRejection;
	}
	
	public Date getDateTestPerformedByLab() {
		return dateTestPerformedByLab;
	}
	
	public String getPlatformUsed() {
		return platformUsed;
	}
	
	public String getTestType() {
		return testType;
	}
	
	public Date getSampleDate() {
		return sampleDate;
	}
	
	public Integer getEncounterId() {
		return encounterId;
	}
	
	public int getAge() {
		return age;
	}
}
