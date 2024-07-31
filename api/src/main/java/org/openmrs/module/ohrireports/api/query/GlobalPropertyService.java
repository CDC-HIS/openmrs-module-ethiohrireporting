package org.openmrs.module.ohrireports.api.query;

import org.openmrs.api.OpenmrsService;

public interface GlobalPropertyService extends OpenmrsService {
	
	String getGlobalProperty(String key);
}
