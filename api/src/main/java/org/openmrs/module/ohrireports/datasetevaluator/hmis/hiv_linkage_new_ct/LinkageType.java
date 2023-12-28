package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct;

public class LinkageType {
	
	private Integer conceptId = 0;
	
	private String Name = "";
	
	private String UUID = "";
	
	public LinkageType() {
	}
	
	public LinkageType(Integer conceptId, String name, String UUID) {
		this.conceptId = conceptId;
		Name = name;
		this.UUID = UUID;
	}
	
	public Boolean isEqual(Integer conceptId) {
		return getConceptId().equals(conceptId);
	}
	
	public Integer getConceptId() {
		return conceptId;
	}
	
	public String getName() {
		return Name;
	}
	
	public String getUUID() {
		return UUID;
	}
	
}
