package org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls;

import java.util.Date;

import org.openmrs.EncounterType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
public class TX_PVLSAutoCalcDatasetDefinition extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	@ConfigurationProperty
	private Boolean includeUnSuppressed;
	
	@ConfigurationProperty
	private Boolean header = false;
	
	public Boolean getIncludeUnSuppressed() {
		return includeUnSuppressed;
	}
	
	public void setIncludeUnSuppressed(Boolean includeUnSuppressed) {
		this.includeUnSuppressed = includeUnSuppressed;
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
