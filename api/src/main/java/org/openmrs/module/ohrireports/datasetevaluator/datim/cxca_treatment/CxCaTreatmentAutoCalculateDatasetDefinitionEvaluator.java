package org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_treatment;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.CervicalCancerTreatmentQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_scrn.CXCAAutoCalculateDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.datim.cxca_treatment.CxCaTreatmentAutoCalculateDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.datim.cxca_scrn.CxcaScreening;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

@Handler(supports = { CxCaTreatmentAutoCalculateDatasetDefinition.class })
public class CxCaTreatmentAutoCalculateDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private CervicalCancerTreatmentQuery cervicalCancerTreatmentQuery;
	
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		CxCaTreatmentAutoCalculateDatasetDefinition cxCaTreatmentAutoCalculateDatasetDefinition = (CxCaTreatmentAutoCalculateDatasetDefinition) dataSetDefinition;
		
		cervicalCancerTreatmentQuery.setStartDate(cxCaTreatmentAutoCalculateDatasetDefinition.getStartDate());
		cervicalCancerTreatmentQuery.setEndDate(cxCaTreatmentAutoCalculateDatasetDefinition.getEndDate());
		
		loadGetCxCaTreatmentByScreeningType(CXCA_FIRST_TIME_SCREENING_TYPE);
		loadGetCxCaTreatmentByScreeningType(CXCA_TYPE_OF_SCREENING_POST_TREATMENT);
		loadGetCxCaTreatmentByScreeningType(CXCA_TYPE_OF_SCREENING_RESCREEN);
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow dataSetRow = new DataSetRow();
		dataSetRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class),
		    cervicalCancerTreatmentQuery.getTotalCohortCount());
		dataSet.addRow(dataSetRow);
		return dataSet;
	}
	
	private void loadGetCxCaTreatmentByScreeningType(String conceptUuId) {
		Cohort cohort = cervicalCancerTreatmentQuery.getByScreeningType(conceptUuId);
		
		Cohort cryotherapyCohort = cervicalCancerTreatmentQuery.getTreatmentByCryotherapy(cohort);
		Cohort leepCohort = cervicalCancerTreatmentQuery.getTreatmentByLEEP(cohort);
		Cohort thermocoagulationCohort = cervicalCancerTreatmentQuery.getTreatmentByThermocoagulation(cohort);
		
		switch (conceptUuId) {
			case CXCA_FIRST_TIME_SCREENING_TYPE:
				cervicalCancerTreatmentQuery.setFirstScreening(new CxCaTreatment(conceptUuId, cryotherapyCohort, leepCohort,
				        thermocoagulationCohort));
				break;
			case CXCA_TYPE_OF_SCREENING_POST_TREATMENT:
				cervicalCancerTreatmentQuery.setReScreening(new CxCaTreatment(conceptUuId, cryotherapyCohort, leepCohort,
				        thermocoagulationCohort));
				break;
			case CXCA_TYPE_OF_SCREENING_RESCREEN:
				cervicalCancerTreatmentQuery.setPostScreening(new CxCaTreatment(conceptUuId, cryotherapyCohort, leepCohort,
				        thermocoagulationCohort));
				break;
			default:
				break;
		}
	}
	
}
