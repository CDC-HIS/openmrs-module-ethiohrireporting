package org.openmrs.module.ohrireports.api.dao;

import java.util.Date;

import org.openmrs.Patient;
import org.openmrs.Person;

public class EthiOhriPatient extends Person {
	
	private Date artStartDate;
	
	private String artStartDateEth;
	
	private String currentRegiment;
	
	public EthiOhriPatient(Date artStartDate, String currentRegiment) {
		this.artStartDate = artStartDate;
		this.currentRegiment = currentRegiment;
	}
}
