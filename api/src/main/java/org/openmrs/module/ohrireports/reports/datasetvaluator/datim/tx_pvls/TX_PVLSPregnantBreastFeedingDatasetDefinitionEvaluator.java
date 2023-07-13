package org.openmrs.module.ohrireports.reports.datasetvaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_ROUTINE_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_TARGET_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_pvls.TX_PVLSPregnantBreastfeedingDatasetDefinition;
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

@Handler(supports = { TX_PVLSPregnantBreastfeedingDatasetDefinition.class })
public class TX_PVLSPregnantBreastFeedingDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	
	private Concept artConcept, hivViralLoadStatusConcept, hivViralLoadSuppressedConcept, hivRoutineVLConcept,
	        hivTargetVLconcept, aliveConcept, restartConcept, patientStatusConcept;
	
	private EvaluationContext context;
	
	private TX_PVLSPregnantBreastfeedingDatasetDefinition txDatasetDefinition;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		context = evalContext;
		txDatasetDefinition = (TX_PVLSPregnantBreastfeedingDatasetDefinition) dataSetDefinition;
		
		loadConcepts();
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		//Pregnant
		DataSetRow pregnantRow = new DataSetRow();
		pregnantRow.addColumnValue(new DataSetColumn("type", "", String.class), "Pregnant");
		pregnantRow.addColumnValue(new DataSetColumn("routine", "Routine", Integer.class), 0);
		pregnantRow.addColumnValue(new DataSetColumn("targeted", "Targeted", Integer.class), 0);
		dataSet.addRow(pregnantRow);
		
		//Breastfeeding
		DataSetRow breastfeedingRow = new DataSetRow();
		breastfeedingRow.addColumnValue(new DataSetColumn("type", "", String.class), "Breastfeeding");
		breastfeedingRow.addColumnValue(new DataSetColumn("routine", "Routine", Integer.class), 0);
		breastfeedingRow.addColumnValue(new DataSetColumn("targeted", "Targeted", Integer.class), 0);
		dataSet.addRow(breastfeedingRow);
		return dataSet;
	}
	
	private void loadConcepts() {
		artConcept = conceptService.getConceptByUuid(ART_START_DATE);
		hivViralLoadStatusConcept = conceptService.getConceptByUuid(HIV_VIRAL_LOAD_STATUS);
		hivRoutineVLConcept = conceptService.getConceptByUuid(HIV_ROUTINE_VIRAL_LOAD_COUNT);
		hivTargetVLconcept = conceptService.getConceptByUuid(HIV_TARGET_VIRAL_LOAD_COUNT);
		aliveConcept = conceptService.getConceptByUuid(ALIVE);
		restartConcept = conceptService.getConceptByUuid(RESTART);
		patientStatusConcept = conceptService.getConceptByUuid(FOLLOW_UP_STATUS);
	}
	
	/*
	 * Load all patient with viral load count has done in the last 12 month
	 * and must be on art for the last three month
	 */
	
	private int getSuppressedPatientList() {
        List<Integer> patientIdList = getListOfALiveORRestartPatientObs();
        List<Integer> refinedPatientIdList = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

        queryBuilder.select("obs")
                .from(Obs.class, "obs")
                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", hivViralLoadStatusConcept)
                .and()
                .whereEqual("obs.valueCoded", hivViralLoadSuppressedConcept)
                .and()
                .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate())
                .and()
                .whereIn("obs.personId", patientIdList);

        List<Obs> obses = evaluationService.evaluateToList(queryBuilder, Obs.class, context);
        LocalDate endDate = new LocalDate(txDatasetDefinition.getEndDate());
        for (Obs obs : obses) {
            if (Months.monthsBetween(new LocalDate(obs.getObsDatetime()), endDate).getMonths() > 12)
                continue;
            if (!refinedPatientIdList.contains(obs.getPersonId())) {
                refinedPatientIdList.add(obs.getPersonId());
            }
        }
        return refinedPatientIdList.size();
    }
	
	private List<Integer> getListOfALiveORRestartPatientObs() {

        List<Integer> uniqueObs = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

        queryBuilder.select("obs")
                .from(Obs.class, "obs")
                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", patientStatusConcept)
                .and()
                .whereIn("obs.valueCoded",
                        Arrays.asList(aliveConcept,
                                restartConcept))
                .and()
                .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate())
                .and()
                .whereIdIn("obs.personId", getPatientsOnArt());
        queryBuilder.orderDesc("obs.personId,obs.obsDatetime");

        List<Obs> aliveObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

        for (Obs obs : aliveObs) {
            if (!uniqueObs.contains(obs.getPersonId())) {
                uniqueObs.add(obs.getPersonId());
            }
        }

        return uniqueObs;
    }
	
	private List<Integer> getPatientsOnArt() {
        List<Integer> patientIdList = new ArrayList<>();

        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs.personId")
                .from(Obs.class, "obs")
                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.concept", artConcept)
                .and()
                .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate());

        List<Obs> obese = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

        LocalDate endDate = new LocalDate(txDatasetDefinition.getEndDate());

        for (Obs obs : obese) {

            if (Months.monthsBetween(new LocalDate(obs.getValueDate()), endDate).getMonths() < 3)
                continue;

            if (!patientIdList.contains(obs.getPersonId()))
                patientIdList.add(obs.getPersonId());
        }

        return patientIdList;

    }
}