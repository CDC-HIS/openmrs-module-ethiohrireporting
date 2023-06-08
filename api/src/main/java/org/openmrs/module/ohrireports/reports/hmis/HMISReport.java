package org.openmrs.module.ohrireports.reports.hmis;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.Parameterizable;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

@Component
public class HMISReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "4bf142d2-f92c-4a9e-9368-55801e9c025d";
	}
	
	@Override
	public String getName() {
		return "06 - HIV | Hospital, Health center, Clinic | Monthly (Federal Ministry Of Health)";
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
	}
	
	@Override
	public List<Parameter> getParameters() {
		Parameter startDate = new Parameter("startDate", "Start Date", Date.class);
		startDate.setRequired(true);
		Parameter startDateGC = new Parameter("startDateGC", " ", Date.class);
		startDateGC.setRequired(false);
		Parameter endDate = new Parameter("endDate", "End Date", Date.class);
		endDate.setRequired(true);
		Parameter endDateGC = new Parameter("endDateGC", " ", Date.class);
		endDateGC.setRequired(false);
		return Arrays.asList(startDate, startDateGC, endDate, endDateGC);
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'constructReportDefinition'");
	}
	
	public static <T extends Parameterizable> Mapped<T> map(T parameterizable, String mappings) {
		if (parameterizable == null) {
			throw new IllegalArgumentException("Parameterizable cannot be null");
		}
		if (mappings == null) {
			mappings = ""; // probably not necessary, just to be safe
		}
		return new Mapped<T>(parameterizable, ParameterizableUtil.createParameterMappings(mappings));
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign("d15829f9-ad58-4421-8d82-11dd80ffaeb2", reportDefinition);
		
		return Arrays.asList(design);
	}
	
	@Override
	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
		return null;
	}
	
	@Override
	public String getVersion() {
		return REPORT_VERSION;
	}
	
}
