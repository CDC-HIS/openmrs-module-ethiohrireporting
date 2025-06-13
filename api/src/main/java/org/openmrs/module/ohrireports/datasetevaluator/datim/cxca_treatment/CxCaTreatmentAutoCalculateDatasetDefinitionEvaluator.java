package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_treatment;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerTreatmentQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_treatment.CxCaTreatmentAutoCalculateDatasetDefinition;
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

import static org.openmrs.module.ohrireports.constants.ConceptAnswer.CXCA_TREATMENT_TYPE_THERMOCOAGULATION;

@Handler(supports = { CxCaTreatmentAutoCalculateDatasetDefinition.class })
public class CxCaTreatmentAutoCalculateDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private CervicalCancerTreatmentQuery cervicalCancerTreatmentQuery;
	
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		CxCaTreatmentAutoCalculateDatasetDefinition cxCaTreatmentAutoCalculateDatasetDefinition = (CxCaTreatmentAutoCalculateDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(
		    cxCaTreatmentAutoCalculateDatasetDefinition.getStartDate(),
		    cxCaTreatmentAutoCalculateDatasetDefinition.getEndDate(), dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		if (!cxCaTreatmentAutoCalculateDatasetDefinition.getHeader()) {
			
			cervicalCancerTreatmentQuery.setStartDate(cxCaTreatmentAutoCalculateDatasetDefinition.getStartDate());
			cervicalCancerTreatmentQuery.setEndDate(cxCaTreatmentAutoCalculateDatasetDefinition.getEndDate());
			
			cervicalCancerTreatmentQuery.generateBaseReport();
			
			loadGetCxCaTreatmentByScreeningType(ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE);
			loadGetCxCaTreatmentByScreeningType(ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN);
			loadGetCxCaTreatmentByScreeningType(ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
			
			DataSetRow dataSetRow = new DataSetRow();
			dataSetRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class),
			    cervicalCancerTreatmentQuery.getTotalCohortCount());
			dataSet.addRow(dataSetRow);
		}
		return dataSet;
	}
	
	private void loadGetCxCaTreatmentByScreeningType(String conceptUuId) {
		Cohort cohort = cervicalCancerTreatmentQuery.getByScreeningType(conceptUuId);
		Cohort cryotherapyCohort = cervicalCancerTreatmentQuery.getCohortByTreatmentType(
		    FollowUpConceptQuestions.CXCA_TREATMENT_TYPE_CRYOTHERAPY, cohort);
		Cohort leepCohort = cervicalCancerTreatmentQuery.getCohortByTreatmentType(ConceptAnswer.CXCA_TREATMENT_TYPE_LEEP,
		    cohort);
		Cohort thermocoagulationCohort = cervicalCancerTreatmentQuery.getCohortByTreatmentType(
		    CXCA_TREATMENT_TYPE_THERMOCOAGULATION, cohort);
		cervicalCancerTreatmentQuery.updateCountedCohort(cryotherapyCohort);
		cervicalCancerTreatmentQuery.updateCountedCohort(leepCohort);
		cervicalCancerTreatmentQuery.updateCountedCohort(thermocoagulationCohort);
		switch (conceptUuId) {
			case ConceptAnswer.CXCA_FIRST_TIME_SCREENING_TYPE:
				cervicalCancerTreatmentQuery.setFirstScreening(new CxCaTreatment(conceptUuId, cryotherapyCohort, leepCohort,
				        thermocoagulationCohort));
				break;
			case ConceptAnswer.CXCA_TYPE_OF_SCREENING_RESCREEN:
				cervicalCancerTreatmentQuery.setReScreening(new CxCaTreatment(conceptUuId, cryotherapyCohort, leepCohort,
				        thermocoagulationCohort));
				break;
			case ConceptAnswer.CXCA_TYPE_OF_SCREENING_POST_TREATMENT:
				cervicalCancerTreatmentQuery.setPostScreening(new CxCaTreatment(conceptUuId, cryotherapyCohort, leepCohort,
				        thermocoagulationCohort));
				break;
			default:
				break;
		}
	}
	
}
