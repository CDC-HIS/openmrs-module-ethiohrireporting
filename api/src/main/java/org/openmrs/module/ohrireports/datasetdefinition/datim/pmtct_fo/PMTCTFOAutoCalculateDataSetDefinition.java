package org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct_fo;

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PMTCTFOAutoCalculateDataSetDefinition extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Date startDate;
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	public Boolean getDenominator() {
		return denominator;
	}
	
	public void setDenominator(Boolean denominator) {
		this.denominator = denominator;
	}
	
	@ConfigurationProperty
	private Boolean denominator = false;
	
	@ConfigurationProperty
	private Boolean header = false;
	
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
	
	public EncounterType getEncounterType() {
		return encounterType;
	}
	
	public void setEncounterType(EncounterType encounterType) {
		this.encounterType = encounterType;
	}
	
	public Boolean getHeader() {
		return header;
	}
	
	public void setHeader(Boolean header) {
		this.header = header;
	}
}
