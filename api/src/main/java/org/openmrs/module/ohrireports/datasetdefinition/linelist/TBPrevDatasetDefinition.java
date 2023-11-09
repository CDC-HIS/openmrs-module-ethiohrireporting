package org.openmrs.module.ohrireports.datasetdefinition.linelist;

import java.util.Date;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
public class TBPrevDatasetDefinition extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Date startDate;
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private String tptStatus;
	
	public String getTptStatus() {
		return tptStatus;
	}
	
	public void setTptStatus(String tptStatus) {
		this.tptStatus = tptStatus;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		
		return endDate;
		
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
