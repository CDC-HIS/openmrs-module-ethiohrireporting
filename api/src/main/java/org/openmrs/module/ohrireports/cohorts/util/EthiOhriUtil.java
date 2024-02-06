package org.openmrs.module.ohrireports.cohorts.util;

import java.util.*;

import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

public class EthiOhriUtil {
	
	private static final int MONTHS_IN_A_YEAR = 12;
	
	public static List<Parameter> getDateRangeParameters() {
		Parameter startDate = new Parameter("startDate", "Start Date", Date.class);
		startDate.setRequired(false);
		Parameter startDateGC = new Parameter("startDateGC", " ", Date.class);
		startDateGC.setRequired(false);
		Parameter endDate = new Parameter("endDate", "End Date", Date.class);
		endDate.setRequired(false);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(startDate, startDateGC, endDate, endDateGC);
	}
	
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		
		String mappings = "startDate=${startDateGC},endDate=${endDateGC}";
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
	
	public static <T extends Parameterizable> Mapped<T> mapEndDate(T parameterizable) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		
		String mappings = "endDate=${endDateGC}";
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
	
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable, String _map) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		
		String mappings = "startDate=${startDateGC},endDate=${endDateGC}";
		
		if (!Objects.isNull(_map) && !_map.isEmpty())
			mappings = _map + "," + mappings;
		
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
	
	public static int getAgeInMonth(Date birthDate, Date asOfDate) {
		Calendar birthCalendar = Calendar.getInstance();
		Calendar asOfCalendar = Calendar.getInstance();
		birthCalendar.setTime(birthDate);
		asOfCalendar.setTime(asOfDate);
		
		int ageInMonthOfYear = (asOfCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)) * MONTHS_IN_A_YEAR;
		
		return (asOfCalendar.get(Calendar.MONTH) - birthCalendar.get(Calendar.MONTH)) + ageInMonthOfYear;
		
	}
}
