package org.openmrs.module.ohrireports.reports.hts;

import java.util.List;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINE_LIST_REPORT;

import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.ReportManager;
import org.springframework.stereotype.Component;

@Component
public class TX_TB_NumeratorReport implements ReportManager {
	
	@Override
	public String getUuid() {
		return "0b3f8468-29af-47b6-92d3-f9d3cc4d4405";
	}
	
	@Override
	public String getName() {
		return LINE_LIST_REPORT + "TX-TB";
	}
	
	@Override
	public String getDescription() {
		return "";
	}
	
	@Override
	public List<Parameter> getParameters() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getParameters'");
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'constructReportDefinition'");
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'constructReportDesigns'");
	}
	
	@Override
	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'constructScheduledRequests'");
	}
	
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getVersion'");
	}
	
}
