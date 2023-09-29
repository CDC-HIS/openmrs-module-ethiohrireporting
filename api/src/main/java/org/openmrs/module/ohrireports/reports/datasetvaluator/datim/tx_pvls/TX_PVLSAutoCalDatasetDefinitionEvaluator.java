package org.openmrs.module.ohrireports.reports.datasetvaluator.datim.tx_pvls;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_ROUTINE_VIRAL_LOAD_COUNT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_UNSUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.FOLLOW_UP_STATUS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.HIV_VIRAL_LOAD_SUPPRESSED;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.ALIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.RESTART;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_pvls.TX_PVLSAutoCalcDatasetDefinition;
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

@Handler(supports = { TX_PVLSAutoCalcDatasetDefinition.class })
public class TX_PVLSAutoCalDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private static final int _VALID_MONTHS_OF_VIRAL_LOAD_TEST = 12;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	
	private Concept artConcept, hivViralLoadStatusConcept, hivViralLoadSuppressedConcept, hivViralLoadUnSuppressedConcept,
	        aliveConcept, restartConcept, followUpConcept;
	
	private EvaluationContext context;
	
	private TX_PVLSAutoCalcDatasetDefinition txDatasetDefinition;
	
	private LocalDate startDate;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		context = evalContext;
		txDatasetDefinition = (TX_PVLSAutoCalcDatasetDefinition) dataSetDefinition;
		
		loadConcepts();
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		DataSetRow setRow = new DataSetRow();
		setRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", String.class), txDatasetDefinition
		        .getIncludeUnSuppressed() ? getAllPatientWithViralLoadCount() : getAllPatientListWithSuppressedViral());
		dataSet.addRow(setRow);
		return dataSet;
	}
	
	private void loadConcepts() {
		artConcept = conceptService.getConceptByUuid(ART_START_DATE);
		hivViralLoadStatusConcept = conceptService.getConceptByUuid(HIV_VIRAL_LOAD_STATUS);
		hivViralLoadSuppressedConcept = conceptService.getConceptByUuid(HIV_VIRAL_LOAD_SUPPRESSED);
		hivViralLoadUnSuppressedConcept = conceptService.getConceptByUuid(HIV_VIRAL_LOAD_UNSUPPRESSED);
		aliveConcept = conceptService.getConceptByUuid(ALIVE);
		restartConcept = conceptService.getConceptByUuid(RESTART);
		followUpConcept = conceptService.getConceptByUuid(FOLLOW_UP_STATUS);
	}
	
	/*
	 * Load all patient with viral load count has done in the last 12 month
	 * and must be on art for the last three month
	 */
	private int getAllPatientWithViralLoadCount() {
                // TODO: update date query for performance optimization
                List<Integer> patientIdList = getListOfALiveORRestartPatientObs();
                List<Integer> refinedPatientIdList = new ArrayList<>();
                HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

                queryBuilder.select("obs")
                                .from(Obs.class, "obs")
                                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                                .and()
                                .whereEqual("obs.concept", hivViralLoadStatusConcept)
                                .and()
                                .whereIn("obs.valueCoded",
                                                Arrays.asList(hivViralLoadSuppressedConcept,
                                                                hivViralLoadUnSuppressedConcept))

                                .and()
                                .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate())
                                .and()
                                .whereIn("obs.personId", patientIdList)
                                .orderDesc("obs.obsDatetime");

                List<Obs> obses = evaluationService.evaluateToList(queryBuilder, Obs.class, context);
                LocalDate endDate = new LocalDate(txDatasetDefinition.getEndDate());
                for (Obs obs : obses) {
                        startDate = new LocalDate(obs.getObsDatetime());
                        if (Months.monthsBetween(startDate, endDate)
                                        .getMonths() > _VALID_MONTHS_OF_VIRAL_LOAD_TEST)
                                continue;
                        if (!refinedPatientIdList.contains(obs.getPersonId())) {
                                refinedPatientIdList.add(obs.getPersonId());
                        }
                }

                return refinedPatientIdList.size();
        }
	
	private int getAllPatientListWithSuppressedViral() {
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
                                .whereIn("obs.personId", patientIdList)
                                .orderDesc("obs.obsDatetime");

                List<Obs> obses = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

                LocalDate endDate = new LocalDate(txDatasetDefinition.getEndDate());

                for (Obs obs : obses) {
                        startDate = new LocalDate(obs.getObsDatetime());
                        if (Months.monthsBetween(startDate, endDate)
                                        .getMonths() > _VALID_MONTHS_OF_VIRAL_LOAD_TEST)
                                continue;
                        if (!refinedPatientIdList.contains(obs.getPersonId())) {
                                refinedPatientIdList.add(obs.getPersonId());
                        }
                }
                return refinedPatientIdList.size();
        }
	
	private List<Integer> getListOfALiveORRestartPatientObs() {

                List<Integer> personIdList = new ArrayList<>();
                Cohort personIdOnArt = Context.getService(PatientQueryService.class).getOnArtCohorts(null, null,
                                txDatasetDefinition.getEndDate(), null);
                // getPatientsOnArt();
                HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

                queryBuilder.select("distinct obs.personId")
                                .from(Obs.class, "obs")
                                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                                .and()
                                .whereEqual("obs.concept", followUpConcept)
                                .and()
                                .whereIn("obs.valueCoded",
                                                Arrays.asList(aliveConcept,
                                                                restartConcept))
                                .and()
                                .whereLess("obs.obsDatetime", txDatasetDefinition.getEndDate())
                                .and()
                                .whereIdIn("obs.personId", personIdOnArt.getMemberIds());

                personIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);

                return personIdList;
        }
	
	private List<Integer> getPatientsOnArt() {
                List<Integer> patientIdList = new ArrayList<>();

                HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
                queryBuilder.select("distinct obs.personId")
                                .from(Obs.class, "obs")
                                .whereEqual("obs.encounter.encounterType", txDatasetDefinition.getEncounterType())
                                .and()
                                .whereEqual("obs.concept", artConcept)
                                .and()
                                .whereLess("obs.valueDatetime", txDatasetDefinition.getEndDate());
                patientIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
                String query = queryBuilder.toString();
                return patientIdList;

        }
}
