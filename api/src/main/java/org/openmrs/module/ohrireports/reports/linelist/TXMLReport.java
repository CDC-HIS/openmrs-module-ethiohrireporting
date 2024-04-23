//package org.openmrs.module.ohrireports.reports.linelist;
//
//import org.openmrs.module.ohrireports.api.dao.EthiOhriPatient;
//import org.openmrs.module.ohrireports.cohorts.util.EthiOhriUtil;
//import org.openmrs.module.ohrireports.datasetdefinition.linelist.MLDataSetDefinition;
//import org.openmrs.module.reporting.evaluation.parameter.Parameter;
//import org.openmrs.module.reporting.report.ReportDesign;
//import org.openmrs.module.reporting.report.ReportRequest;
//import org.openmrs.module.reporting.report.definition.ReportDefinition;
//import org.openmrs.module.reporting.report.manager.ReportManager;
//import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.openmrs.module.ohrireports.OHRIReportsConstants.LINE_LIST_REPORT;
//import static org.openmrs.module.ohrireports.OHRIReportsConstants.REPORT_VERSION;
//
//@Component
//public class TXMLReport implements ReportManager {
//
//	/**
//	 * @return the uuid of the Report
//	 */
//	@Override
//	public String getUuid() {
//		return "64ca279f-52bf-4582-bb21-dd8b0063e94a";
//	}
//
//	/**
//	 * @return the name of the Report
//	 */
//	@Override
//	public String getName() {
//		return LINE_LIST_REPORT.concat("-TX_ML");
//	}
//
//	/**
//	 * @return the description of the Report
//	 */
//	@Override
//	public String getDescription() {
//		return "TX_ML";
//	}
//
//	/**
//	 * @return the parameters of the Report
//	 */
//	@Override
//	public List<Parameter> getParameters() {
//		return EthiOhriUtil.getDateRangeParameters();
//	}
//
//	/**
//	 * @return a ReportDefinition that may be persisted or run
//	 */
//	@Override
//	public ReportDefinition constructReportDefinition() {
//		ReportDefinition reportDefinition = new ReportDefinition();
//		reportDefinition.setUuid(getUuid());
//		reportDefinition.setName(getName());
//		reportDefinition.setDescription(getDescription());
//
//		MLDataSetDefinition _datasetDefinition = new MLDataSetDefinition();
//		_datasetDefinition.setParameters(getParameters());
//
//		reportDefinition.addDataSetDefinition("TX_ML", EthiOhriUtil.map(_datasetDefinition));
//		return reportDefinition;
//	}
//
//	/**
//	 * @param reportDefinition this will be the same ReportDefinition returned by an earlier call to
//	 *            #constructReportDefinition
//	 * @return the ReportDesigns under which this report can be evaluated
//	 */
//	@Override
//	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
//		ReportDesign design = ReportManagerUtil.createExcelDesign("8af6e945-458b-4c9c-8a57-f097ef580e0a", reportDefinition);
//
//		return Collections.singletonList(design);
//	}
//
//	/**
//	 * @param reportDefinition this will be the same ReportDefinition returned by an earlier call to
//	 *            #constructReportDefinition
//	 * @return the ReportRequests that should be automatically scheduled for execution
//	 */
//	@Override
//	public List<ReportRequest> constructScheduledRequests(ReportDefinition reportDefinition) {
//		return null;
//	}
//
//	/**
//	 * This is used to determine whether to build/save the report definition on module startup.
//	 * Version should be something like "1.0" or "1.1-SNAPSHOT". (Any version with "-SNAPSHOT"
//	 * indicates it is under active development and will be built/saved every time the module is
//	 * started.)
//	 *
//	 * @return what version of this report we are at
//	 */
//	@Override
//	public String getVersion() {
//		return REPORT_VERSION;
//	}
//}
