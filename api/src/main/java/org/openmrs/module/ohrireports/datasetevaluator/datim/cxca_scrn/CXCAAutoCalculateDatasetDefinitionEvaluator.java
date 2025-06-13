package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCAAutoCalculateDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

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
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(cxcaDatasetDefinition.getStartDate(),
		    cxcaDatasetDefinition.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		if (!cxcaDatasetDefinition.getHeader()) {
			
			cervicalCancerQuery.setStartDate(cxcaDatasetDefinition.getStartDate());
			cervicalCancerQuery.setEndDate(cxcaDatasetDefinition.getEndDate());
			
			loadGetScreening(ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE);
			loadGetScreening(ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
			loadGetScreening(ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN);
			DataSetRow dataSetRow = new DataSetRow();
			dataSetRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class),
			    cervicalCancerQuery.getTotalCohortCount());
			dataSet.addRow(dataSetRow);
		}
		return dataSet;
	}
	
	private void loadGetScreening(String conceptUuId) {
		Cohort cohort = cervicalCancerQuery.getByScreeningType(conceptUuId, cervicalCancerQuery.getCurrentEncounter());
		cervicalCancerQuery.setCountedCohort(cohort);
		Cohort negativeCohort = cervicalCancerQuery.getNegativeResult(cohort);
		Cohort positiveCohort = cervicalCancerQuery.getPositiveResult(cohort);
		Cohort suspectedCohort = cervicalCancerQuery.getSuspectedResult(cohort);
		
		switch (conceptUuId) {
			case ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE:
				cervicalCancerQuery.setFirstScreening(new CxcaScreening(ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE,
				        negativeCohort, positiveCohort, suspectedCohort));
				break;
			case ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN:
				cervicalCancerQuery.setReScreening(new CxcaScreening(ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT,
				        negativeCohort, positiveCohort, suspectedCohort));
				break;
			case ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT:
				cervicalCancerQuery.setPostScreening(new CxcaScreening(ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN,
				        negativeCohort, positiveCohort, suspectedCohort));
				break;
			default:
				break;
		}
	}
	
}
