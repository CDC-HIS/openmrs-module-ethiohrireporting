package org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_plhiv;

import java.util.Date;

import org.openmrs.module.reporting.dataset.definition.BaseDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.springframework.stereotype.Component;

@Component
public class HivPlHivDatasetDefinition extends BaseDataSetDefinition {
	
	@ConfigurationProperty
	private Date startDate;
	
	@ConfigurationProperty
	private Date endDate;
	
	@ConfigurationProperty
	private HivPvlHivType hivPvlHivType;
	
	public HivPvlHivType getHivPvlHivType() {
		return hivPvlHivType;
	}
	
	public void setHivPvlHivType(HivPvlHivType hivPvlHivType) {
		this.hivPvlHivType = hivPvlHivType;
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
