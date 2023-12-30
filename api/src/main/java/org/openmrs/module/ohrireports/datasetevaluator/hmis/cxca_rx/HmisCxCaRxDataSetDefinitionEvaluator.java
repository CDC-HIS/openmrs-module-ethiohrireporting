package org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_rx;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.cxca_rx.HmisCxCaRxDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Handler(supports = {HmisCxCaRxDataSetDefinition.class})
public class HmisCxCaRxDataSetDefinitionEvaluator implements DataSetEvaluator {

    private EvaluationContext context;
    private String baseName = "HIV_CXCA_RX. ";
    private String COLUMN_3_NAME = "Number";
    private HmisCxCaRxDataSetDefinition hdsd;

    List<Person> personList = new ArrayList<>();

    @Autowired
    private CxCaTreatmentHMISQuery cxCaTreatmentHMISQuery;

    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EvaluationService evaluationService;
    List<Obs> obses = new ArrayList<>();

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {

        hdsd = (HmisCxCaRxDataSetDefinition) dataSetDefinition;
        context = evalContext;
        cxCaTreatmentHMISQuery.setStartDate(hdsd.getStartDate());
        cxCaTreatmentHMISQuery.setEndDate(hdsd.getEndDate());


        Cohort cryotherapyCohort = cxCaTreatmentHMISQuery
                .getCohortByConceptAndBaseEncounter(CXCA_TREATMENT_PRECANCEROUS_LESIONS, CXCA_TREATMENT_TYPE_CRYOTHERAPY);
        Cohort leepCohort = cxCaTreatmentHMISQuery
                .getCohortByConceptAndBaseEncounter(CXCA_TREATMENT_PRECANCEROUS_LESIONS, CXCA_TREATMENT_TYPE_LEEP);
        Cohort thermocoagulationCohort = cxCaTreatmentHMISQuery
                .getCohortByConceptAndBaseEncounter(CXCA_TREATMENT_PRECANCEROUS_LESIONS, CXCA_TREATMENT_TYPE_THERMOCOAGULATION);
        int  totalPrecancerousLesion = cryotherapyCohort.size() + leepCohort.size() + thermocoagulationCohort.size();

        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);



        data.addRow(buildColumn("", "Treatment of precancerous cervical lesion", totalPrecancerousLesion));
        data.addRow(buildColumn("1", "Treatment with Cryotherapy", cryotherapyCohort.size()));

        personList = cxCaTreatmentHMISQuery.getPersons(cryotherapyCohort);
        data.addRow(buildColumn("1. 1", "15 - 19 years", getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        data.addRow(buildColumn("1. 2", "20 - 24 years", getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        data.addRow(buildColumn("1. 3", "25 - 29 years", getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        data.addRow(buildColumn("1. 4", "30 - 49 years", getCohortSizeByAgeAndGender(30, 49, Gender.Female)));
        data.addRow(buildColumn("1. 5", ">= 50 years", getCohortSizeByAgeAndGender(50, 150, Gender.Female)));

        data.addRow(buildColumn("2", "Treatment with LEEP", leepCohort.size()));

        personList = cxCaTreatmentHMISQuery.getPersons(leepCohort);
        data.addRow(buildColumn("2. 1", "15 - 19 years", getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        data.addRow(buildColumn("2. 2", "20 - 24 years", getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        data.addRow(buildColumn("2. 3", "25 - 29 years", getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        data.addRow(buildColumn("2. 4", "30 - 49 years", getCohortSizeByAgeAndGender(30, 49, Gender.Female)));
        data.addRow(buildColumn("2. 5", ">= 50 years", getCohortSizeByAgeAndGender(50, 150, Gender.Female)));

        data.addRow(buildColumn("3", "Treatment with Thermal Ablation/Thermocoagulation", thermocoagulationCohort.size()));
        personList = cxCaTreatmentHMISQuery.getPersons(thermocoagulationCohort);
        data.addRow(buildColumn("3. 1", "15 - 19 years", getCohortSizeByAgeAndGender(15, 19, Gender.Female)));
        data.addRow(buildColumn("3. 2", "20 - 24 years", getCohortSizeByAgeAndGender(20, 24, Gender.Female)));
        data.addRow(buildColumn("3. 3", "25 - 29 years", getCohortSizeByAgeAndGender(25, 29, Gender.Female)));
        data.addRow(buildColumn("3. 4", "30 - 49 years", getCohortSizeByAgeAndGender(30, 49, Gender.Female)));
        data.addRow(buildColumn("3. 5", ">= 50 years", getCohortSizeByAgeAndGender(50, 150, Gender.Female)));

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
        String _gender = gender.equals(gender.Female) ? "f" : "m";
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

