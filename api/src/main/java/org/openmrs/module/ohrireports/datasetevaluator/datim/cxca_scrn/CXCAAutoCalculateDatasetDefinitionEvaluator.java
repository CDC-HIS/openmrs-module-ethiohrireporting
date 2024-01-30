package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCAAutoCalculateDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

/**
 * CXCAAutoCalculateDatasetDefinitionEvaluator
 */
// ==============================
// Report ceriatria
/*
 * 1, Only female patient.
 * 2, Must be on art.
 * 3, Must be on regiment which means regiment end date should be >= report end
 * date.
 * 4, Cervical cancer test date should fall between the reporting date range.
 * 5, Cervical cancer screening identifiers
 * - Must be cervical cancer screening type be selected.
 * - Must be screening strategy be selected.
 * - Must have result.
 */
@Handler(supports = { CXCAAutoCalculateDatasetDefinition.class })
public class CXCAAutoCalculateDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private CervicalCancerQuery cervicalCancerQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		CXCAAutoCalculateDatasetDefinition cxcaDatasetDefinition = (CXCAAutoCalculateDatasetDefinition) dataSetDefinition;
		
		cervicalCancerQuery.setStartDate(cxcaDatasetDefinition.getStartDate());
		cervicalCancerQuery.setEndDate(cxcaDatasetDefinition.getEndDate());
		
		loadGetScreening(CXCA_FIRST_TIME_SCREENING_TYPE);
		loadGetScreening(CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
		loadGetScreening(CXCA_TYPE_OF_SCREENING_RESCREEN);
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow dataSetRow = new DataSetRow();
		dataSetRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class),
		    cervicalCancerQuery.getTotalCohortCount());
		dataSet.addRow(dataSetRow);
		return dataSet;
	}
	
	private void loadGetScreening(String conceptUuId) {
		Cohort cohort = cervicalCancerQuery.getByScreeningType(conceptUuId);
		cervicalCancerQuery.setCountedCohort(cohort);
		Cohort negativeCohort = cervicalCancerQuery.getNegativeResult(cohort);
		Cohort positiveCohort = cervicalCancerQuery.getPositiveResult(cohort);
		Cohort suspectedCohort = cervicalCancerQuery.getSuspectedResult(cohort);
		
		switch (conceptUuId) {
			case CXCA_FIRST_TIME_SCREENING_TYPE:
				cervicalCancerQuery.setFirstScreening(new CxcaScreening(conceptUuId, negativeCohort, positiveCohort,
				        suspectedCohort));
				break;
			case CXCA_TYPE_OF_SCREENING_POST_TREATMENT:
				cervicalCancerQuery.setReScreening(new CxcaScreening(conceptUuId, negativeCohort, positiveCohort,
				        suspectedCohort));
				break;
			case CXCA_TYPE_OF_SCREENING_RESCREEN:
				cervicalCancerQuery.setPostScreening(new CxcaScreening(conceptUuId, negativeCohort, positiveCohort,
				        suspectedCohort));
				break;
			default:
				break;
		}
	}
	
}
