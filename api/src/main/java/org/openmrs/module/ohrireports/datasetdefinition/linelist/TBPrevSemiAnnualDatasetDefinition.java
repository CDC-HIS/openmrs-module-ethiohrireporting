package org.openmrs.module.ohrireports.datasetdefinition.linelist;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TBPrevSemiAnnualDatasetDefinition extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private Date startDate;
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@ConfigurationProperty
	private String tptStatus;
	
	public String getTptStatus() {
		return tptStatus;
	}
	
	public void setTptStatus(String tptStatus) {
		this.tptStatus = tptStatus;
	}
	
	public Date getEndDate() {
		
		return endDate;
		
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
