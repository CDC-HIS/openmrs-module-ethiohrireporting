package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.hiv_art_re_arv;

import static org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.HMISConstant.*;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.YES;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.NO;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.UNKNOWN;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_ret.HIVARTRETDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HIVARTRETDatasetDefinition.class })
public class HIVARTReArvDatasetDefinitionEvaluator implements DataSetEvaluator {

	private HIVARTRETDatasetDefinition _datasetDefinition;
	private String baseName = "HIV_ART_RE_ARV ";
	private String column_3_name = "Tir 15";
	private String description = "Number of ART clients restarted ARV treatment in the reporting period";

	@Autowired
	private HivArtReArvQuery hivArtRetQuery;

	List<Person> persons = new ArrayList<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {
		_datasetDefinition = (HIVARTRETDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		buildDataSet(dataSet);

		return dataSet;
	}

	public void buildDataSet(SimpleDataSet dataSet) {
			// lost to follow-up Lost after treatemnt < 3month
		// dataSet.addRow(buildColumn(".1", "Number of ART clients restarted ARV treatment in the reporting period", Range.NONE,
		// 		Range.LESS_THAN_THREE_MONTH, LOST_TO_FOLLOW_UP, ""));

		// dataSet.addRow(buildColumn(".1. 1", "< 15 years, Male", Range.LESS_THAN_FIFTY,
		// 		Range.NONE, LOST_TO_FOLLOW_UP, "M"));

		// dataSet.addRow(
		// 		buildColumn(".1. 2", "< 15 years, Female", Range.LESS_THAN_FIFTY, Range.NONE, LOST_TO_FOLLOW_UP, "F"));

		// dataSet.addRow(buildColumn(".1. 3", ">= 15 years, Male", Range.ABOVE_OR_EQUAL_TO_FIFTY,
		// 		Range.NONE, LOST_TO_FOLLOW_UP, "M"));

		// dataSet.addRow(
		// 		buildColumn(".1. 4", ">= 15 years, Female", Range.ABOVE_OR_EQUAL_TO_FIFTY, Range.NONE,
		// 				LOST_TO_FOLLOW_UP, "F"));
	}

}
