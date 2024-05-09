package org.openmrs.module.ohrireports.datasetevaluator.linelist;

import org.openmrs.PersonAddress;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LineListUtilities {
	
	public static DataSetRow buildEmptyRow(List<String> columns) {
		DataSetRow row = new DataSetRow();
		
		for (String column : columns) {
			row.addColumnValue(new DataSetColumn(column, column, String.class), "");
		}
		row.addColumnValue(new DataSetColumn("#", "#", String.class), "TOTAL");
		row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), "0");
		return row;
	}
	
	public static PersonAddress getPersonAddress(Set<PersonAddress> personAddresses) {
		Optional<PersonAddress> personAddress = Optional.ofNullable(personAddresses.stream().filter(PersonAddress::getPreferred).findFirst().orElse(null));;

		return personAddress.orElse(null);
	}
	
	public static long getMonthDifference(Date from, Date to) {
		
		if (from == null || to == null) {
			return 0;
		}
		
		// Convert java.util.Date to Instant
		Instant instantFrom = from.toInstant();
		Instant instantTo = to.toInstant();
		
		// Define date format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		// Convert Instant to LocalDateTime
		LocalDateTime start = LocalDateTime.ofInstant(instantFrom, ZoneId.systemDefault());
		LocalDateTime end = LocalDateTime.ofInstant(instantTo, ZoneId.systemDefault());
		
		// Calculate the difference in days
		return ChronoUnit.MONTHS.between(start, end);
	}
}
