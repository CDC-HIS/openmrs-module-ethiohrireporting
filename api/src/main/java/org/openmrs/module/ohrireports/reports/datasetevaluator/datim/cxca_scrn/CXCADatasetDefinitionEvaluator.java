package org.openmrs.module.ohrireports.reports.datasetevaluator.datim.cxca_scrn;


import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.CXCA_TYPE_OF_SCREENING;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.cxca_scrn.CXCADatasetDefinition;
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

@Handler(supports = { CXCADatasetDefinition.class })
public class CXCADatasetDefinitionEvaluator implements DataSetEvaluator {
    private EvaluationContext context;
    private int total = 0;
    private int minCount = 0;
    private int maxCount = 4;
    List<Obs> obses = new ArrayList<>();
    private CXCADatasetDefinition cxcaDatasetDefinition;

    private Concept artConcept, cxcaScreenedConcept;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {
        cxcaDatasetDefinition = (CXCADatasetDefinition) dataSetDefinition;
        loadConcepts();
        SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
        DataSetRow dataSetRow = new DataSetRow();
        dataSetRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), getTotalCXCAScreened());
        dataSet.addRow(dataSetRow);
        return dataSet;
    }

    private void loadConcepts() {
        artConcept = conceptService.getConceptByUuid(ART_START_DATE);
        cxcaScreenedConcept = conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING);
    }

    private int getTotalCXCAScreened() {
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("distinct obs.personId");
        queryBuilder.from(Obs.class, "obs")
                .whereEqual("obs.person.gender", "F")
                .and()
                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("Obs.concept", artConcept)
                .and()
                .whereIn("obs.personId", getOnArtFemalePatients());
        List<Integer> personId = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
        return personId.size();
    }

    private List<Integer> getOnArtFemalePatients() {
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("distinct obs.personId").from(Obs.class, "obs")
                .whereEqual("obs.concept", artConcept)
                .and()
                .whereEqual("obs.encounter.encounterType", cxcaDatasetDefinition.getEncounterType())
                .and()
                .whereEqual("obs.person.gender", "F")
                .and()
                .whereLessOrEqualTo("obs.valueDatetime", cxcaDatasetDefinition.getEndDate())
                .and()
                .orderDesc("obs.obsDatetime");

        List<Integer> patientsId = evaluationService.evaluateToList(queryBuilder, Integer.class, context);

        return patientsId;
    }
}
