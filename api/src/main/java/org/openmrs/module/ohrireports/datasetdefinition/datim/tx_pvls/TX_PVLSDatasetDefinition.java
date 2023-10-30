package org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
public class TX_PVLSDatasetDefinition extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	@ConfigurationProperty
	private Boolean includeUnSuppressed;
	
	public Boolean getIncludeUnSuppressed() {
		return includeUnSuppressed;
	}
	
	public void setIncludeUnSuppressed(Boolean includeSuppressed) {
		this.includeUnSuppressed = includeSuppressed;
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
}
