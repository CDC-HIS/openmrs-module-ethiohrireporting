package org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
public class TX_PVLSDisaggregationByPopulationDatasetDefinition extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Date startDate;
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private EncounterType encounterType;
	
	@ConfigurationProperty
	private Concept viralLoadTypeConcept;
	
	@ConfigurationProperty
	private Boolean includeUnSuppressed;
	
	public Boolean getIncludeUnSuppressed() {
		return includeUnSuppressed;
	}
	
	public void setIncludeUnSuppressed(Boolean includeSuppressed) {
		this.includeUnSuppressed = includeSuppressed;
	}
	
	public Concept getViralLoadTypeConcept() {
		return viralLoadTypeConcept;
	}
	
	public void setViralLoadTypeConcept(Concept viralLoadTypeConcept) {
		this.viralLoadTypeConcept = viralLoadTypeConcept;
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
	
	public EncounterType getEncounterType() {
		return encounterType;
	}
	
	public void setEncounterType(EncounterType encounterType) {
		this.encounterType = encounterType;
	}
}
