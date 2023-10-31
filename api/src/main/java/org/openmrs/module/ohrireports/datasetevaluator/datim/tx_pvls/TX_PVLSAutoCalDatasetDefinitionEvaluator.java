package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.DATE_VIRAL_TEST_RESULT_RECEIVED;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.VlQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_pvls.TX_PVLSAutoCalcDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TX_PVLSAutoCalcDatasetDefinition.class })
public class TX_PVLSAutoCalDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private static final int _VALID_MONTHS_OF_VIRAL_LOAD_TEST = 12;
	
	@Autowired
	private VlQuery vlQuery;
	
	private PatientQueryService patientQueryService;
	
	private TX_PVLSAutoCalcDatasetDefinition txDatasetDefinition;
	
	private Cohort cohort;
	
	private Date start, end;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		txDatasetDefinition = (TX_PVLSAutoCalcDatasetDefinition) dataSetDefinition;
		
		patientQueryService = Context.getService(PatientQueryService.class);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(txDatasetDefinition.getEndDate());
		calendar.add(Calendar.MONTH, -_VALID_MONTHS_OF_VIRAL_LOAD_TEST);
		start = calendar.getTime();
		end = txDatasetDefinition.getEndDate();
		
		if (vlQuery.start != start || vlQuery.end != end || vlQuery.getVlTakenEncounters().isEmpty()) {
			List<Integer> laIntegers = patientQueryService.getBaseEncounters(DATE_VIRAL_TEST_RESULT_RECEIVED, start, end);
			
			vlQuery.loadInitialCohort(start, end, laIntegers);
		}
		cohort = txDatasetDefinition.getIncludeUnSuppressed() ? vlQuery.cohort : vlQuery.getViralLoadSuppressed();
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow setRow = new DataSetRow();
		setRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", String.class), cohort.getMemberIds().size());
		dataSet.addRow(setRow);
		return dataSet;
	}
	
}
