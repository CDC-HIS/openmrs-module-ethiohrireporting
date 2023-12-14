package org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_scrn;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.cxca_scrn.HmisCxCaScrnDataSetDefinition;
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

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Handler(supports = {HmisCxCaScrnDataSetDefinition.class})
public class HmisCxCaScrnDataSetDefinitionEvaluator implements DataSetEvaluator {

    private EvaluationContext context;
    private String baseName = "HIV_CXCA_SCRN. ";
    private String COLUMN_3_NAME = "Number";

    private HmisCxCaScrnDataSetDefinition hdsd;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private CXCAScreeningHmisQuery cxcaScreeningHmisQuery;
    @Autowired
    private EvaluationService evaluationService;
    List<Person> personList = new ArrayList<>();
    List<Integer> cxcaScreenedPatientsID = new ArrayList<>();
    private Concept hpvAndDNAScreeningResultConcept, positiveConcept, viaScreeningResultConcept, viaNegativeConcept, viaPositiveConcept, viaSuspiciousConcept;

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {

        hdsd = (HmisCxCaScrnDataSetDefinition) dataSetDefinition;
        cxcaScreeningHmisQuery.setStartDate(hdsd.getStartDate());
        cxcaScreeningHmisQuery.setEndDate(hdsd.getEndDate());
        context = evalContext;

        Cohort viaScreenedCohort = cxcaScreeningHmisQuery.getCohortByConceptAndBaseEncounter(SCREENING_STRATEGY,VIA );
        Cohort hpvScreenedCohort = cxcaScreeningHmisQuery.getCohortByConceptAndBaseEncounter(SCREENING_STRATEGY, HPV_DNA_SCREENING_VIA_TRIAGE);
        int totalScreenedCohort = viaScreenedCohort.size() + hpvScreenedCohort.size();

        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);

        data.addRow(buildColumn("1", "Cervical Cancer screening by type of test", totalScreenedCohort));
        data.addRow(buildColumn("1. 1", "Screened by VIA", viaScreenedCohort.size()));
        data.addRow(buildColumn("1. 2", "Screened by HPV DNA", hpvScreenedCohort.size()));

        Cohort viaNormalCervixCohort = cxcaScreeningHmisQuery.getCohortByConceptAndBaseEncounter(VIA_SCREENING_RESULT, VIA_NEGATIVE);
        Cohort viaPositiveEligibleForCrypto = cxcaScreeningHmisQuery.getCohortByConceptAndBaseEncounter(VIA_SCREENING_RESULT, VIA_POSITIVE_ELIGIBLE_FOR_CRYO);
        Cohort viaPositiveNonEligibleForCrypto = cxcaScreeningHmisQuery.getCohortByConceptAndBaseEncounter(VIA_SCREENING_RESULT, VIA_POSITIVE_NON_ELIGIBLE_FOR_CRYO);
        Cohort viaPrecancerousLesion = Cohort.union(viaPositiveEligibleForCrypto, viaPositiveNonEligibleForCrypto);
        Cohort viaSuspiciousCxCa = cxcaScreeningHmisQuery.getCohortByConceptAndBaseEncounter(VIA_SCREENING_RESULT, VIA_SUSPICIOUS_RESULT);

        int totalViaScreeningResult = viaNormalCervixCohort.size()
                + viaPrecancerousLesion.size()
                + viaSuspiciousCxCa.size();

        data.addRow(buildColumn("2", "VIA Screening Result", totalViaScreeningResult));

        personList = cxcaScreeningHmisQuery.getPersons(viaNormalCervixCohort);
        data.addRow(buildColumn("2.1", "Normal cervix:"
                , viaNormalCervixCohort.size()));
        data.addRow(buildColumn("2.1. 1", "15 - 19 years"
                , getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        data.addRow(buildColumn("2.1. 2", "20 - 24 years"
                , getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        data.addRow(buildColumn("2.1. 3", "25 - 29 years"
                , getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        data.addRow(buildColumn("2.1. 4", "30 - 49 years"
                , getCohortSizeByAgeAndGender(30, 49, Gender.Female)));
        data.addRow(buildColumn("2.1. 5", ">= 50 years"
                , getCohortSizeByAgeAndGender(50, 150, Gender.Female)));

        personList = cxcaScreeningHmisQuery.getPersons(viaPrecancerousLesion);
        data.addRow(buildColumn("2.3", "Precancerous Lesion:"
                , viaPrecancerousLesion.size()));
        data.addRow(buildColumn("2.3. 1", "15 - 19 years"
                , getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        data.addRow(buildColumn("2.3. 2", "20 - 24 years"
                , getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        data.addRow(buildColumn("2.3. 3", "25 - 29 years"
                , getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        data.addRow(buildColumn("2.3. 4", "30 - 49 years"
                , getCohortSizeByAgeAndGender(30, 49, Gender.Female)));
        data.addRow(buildColumn("2.3. 5", ">= 50 years"
                , getCohortSizeByAgeAndGender(50, 150, Gender.Female)));

        personList = cxcaScreeningHmisQuery.getPersons(viaSuspiciousCxCa);
        data.addRow(buildColumn("2.4", "Suspicious cancerous Lesion:"
                , viaSuspiciousCxCa.size()));
        data.addRow(buildColumn("2.4. 1", "15 - 19 years"
                , getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        data.addRow(buildColumn("2.4. 2", "20 - 24 years"
                , getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        data.addRow(buildColumn("2.4. 3", "25 - 29 years"
                , getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        data.addRow(buildColumn("2.4. 4", "30 - 49 years"
                , getCohortSizeByAgeAndGender(30, 49, Gender.Female)));
        data.addRow(buildColumn("2.4. 5", ">= 50 years"
                , getCohortSizeByAgeAndGender(50, 150, Gender.Female)));


        Cohort hpvDNATestPositive = cxcaScreeningHmisQuery.getCohortByConceptAndBaseEncounter(HPV_DNA_SCREENING_RESULT, POSITIVE);

        personList = cxcaScreeningHmisQuery.getPersons(hpvDNATestPositive);

        data.addRow(buildColumn("2.5", "HPV DNA test positive:"
                , hpvDNATestPositive.size()));
        data.addRow(buildColumn("2.5. 1", "15 - 19 years"
                , getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        data.addRow(buildColumn("2.5. 2", "20 - 24 years"
                , getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        data.addRow(buildColumn("2.5. 3", "25 - 29 years"
                , getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        data.addRow(buildColumn("2.5. 4", "30 - 49 years"
                , getCohortSizeByAgeAndGender(30, 49, Gender.Female)));
        data.addRow(buildColumn("2.5. 5", ">= 50 years"
                , getCohortSizeByAgeAndGender(51, 150, Gender.Female)));


        return data;
    }

    private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
        DataSetRow hivCxcarxDataSetRow = new DataSetRow();
        hivCxcarxDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                baseName + "" + col_1_value);

        hivCxcarxDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

        hivCxcarxDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
                col_3_value);

        return hivCxcarxDataSetRow;
    }

    private Integer getCohortSizeByAgeAndGender(int minAge, int maxAge, Gender gender) {
        int _age = 0;
        List<Integer> patients = new ArrayList<>();
        String _gender = gender.equals(Gender.Female) ? "f" : "m";
        if (maxAge > 1) {
            maxAge = maxAge + 1;
        }
        for (Person person : personList) {

            _age = person.getAge(hdsd.getEndDate());

            if (!patients.contains(person.getPersonId())
                    && (_age >= minAge && _age < maxAge)
                    && (person.getGender().toLowerCase().equals(_gender))) {

                patients.add(person.getPersonId());

            }
        }
        return patients.size();
    }

}

enum Gender {
    Female,
    Male
}