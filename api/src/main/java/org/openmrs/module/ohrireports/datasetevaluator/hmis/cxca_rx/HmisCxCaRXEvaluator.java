package org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_rx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Component
@Scope("prototype")
public class HmisCxCaRXEvaluator  {
	
	List<Person> personList = new ArrayList<>();

    @Autowired
    private CxCaTreatmentHMISQuery cxCaTreatmentHMISQuery;

    private Date end;
    
    public void buildDataset(Date start,Date end , SimpleDataSet dataset)  {

            this.end = end;
        cxCaTreatmentHMISQuery.setStartDate(start);
        cxCaTreatmentHMISQuery.setEndDate(end);


        Cohort cryotherapyCohort = cxCaTreatmentHMISQuery
                .getCohortByConceptAndBaseEncounter(FollowUpConceptQuestions.CXCA_TREATMENT_PRECANCEROUS_LESIONS, FollowUpConceptQuestions.CXCA_TREATMENT_TYPE_CRYOTHERAPY);
        Cohort leepCohort = cxCaTreatmentHMISQuery
                .getCohortByConceptAndBaseEncounter(FollowUpConceptQuestions.CXCA_TREATMENT_PRECANCEROUS_LESIONS, ConceptAnswer.CXCA_TREATMENT_TYPE_LEEP);
        Cohort thermocoagulationCohort = cxCaTreatmentHMISQuery
                .getCohortByConceptAndBaseEncounter(FollowUpConceptQuestions.CXCA_TREATMENT_PRECANCEROUS_LESIONS, ConceptAnswer.CXCA_TREATMENT_TYPE_THERMOCOAGULATION);
        int  totalPrecancerousLesion = cryotherapyCohort.size() + leepCohort.size() + thermocoagulationCohort.size();



        dataset.addRow(buildColumn("", "Treatment of precancerous cervical lesion", totalPrecancerousLesion));
        dataset.addRow(buildColumn("1", "Treatment with Cryotherapy", cryotherapyCohort.size()));

        personList = cxCaTreatmentHMISQuery.getPersons(cryotherapyCohort);
        dataset.addRow(buildColumn("1. 1", "15 - 19 years", getCohortSizeByAgeAndGender(15, 19)));
        dataset.addRow(buildColumn("1. 2", "20 - 24 years", getCohortSizeByAgeAndGender(20, 24)));
        dataset.addRow(buildColumn("1. 3", "25 - 29 years", getCohortSizeByAgeAndGender(25, 29)));
        dataset.addRow(buildColumn("1. 4", "30 - 49 years", getCohortSizeByAgeAndGender(30, 49)));
        dataset.addRow(buildColumn("1. 5", ">= 50 years", getCohortSizeByAgeAndGender(50, 150)));
        
        dataset.addRow(buildColumn("2", "Treatment with LEEP", leepCohort.size()));

        personList = cxCaTreatmentHMISQuery.getPersons(leepCohort);
        dataset.addRow(buildColumn("2. 1", "15 - 19 years", getCohortSizeByAgeAndGender(15, 19)));
        dataset.addRow(buildColumn("2. 2", "20 - 24 years", getCohortSizeByAgeAndGender(20, 24)));
        dataset.addRow(buildColumn("2. 3", "25 - 29 years", getCohortSizeByAgeAndGender(25, 29)));
        dataset.addRow(buildColumn("2. 4", "30 - 49 years", getCohortSizeByAgeAndGender(30, 49)));
        dataset.addRow(buildColumn("2. 5", ">= 50 years", getCohortSizeByAgeAndGender(50, 150)));
        
        dataset.addRow(buildColumn("3", "Treatment with Thermal Ablation/Thermocoagulation", thermocoagulationCohort.size()));
        personList = cxCaTreatmentHMISQuery.getPersons(thermocoagulationCohort);
        dataset.addRow(buildColumn("3. 1", "15 - 19 years", getCohortSizeByAgeAndGender(15, 19)));
        dataset.addRow(buildColumn("3. 2", "20 - 24 years", getCohortSizeByAgeAndGender(20, 24)));
        dataset.addRow(buildColumn("3. 3", "25 - 29 years", getCohortSizeByAgeAndGender(25, 29)));
        dataset.addRow(buildColumn("3. 4", "30 - 49 years", getCohortSizeByAgeAndGender(30, 49)));
        dataset.addRow(buildColumn("3. 5", ">= 50 years", getCohortSizeByAgeAndGender(50, 150)));

    }

    private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
        DataSetRow hivCxcarxDataSetRow = new DataSetRow();
	    String baseName = "HIV_CXCA_RX. ";
	    hivCxcarxDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                baseName + "" + col_1_value);
        hivCxcarxDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
	    
	    String COLUMN_3_NAME = "Number";
	    hivCxcarxDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
                col_3_value);

        return hivCxcarxDataSetRow;
    }

    private Integer getCohortSizeByAgeAndGender(int minAge, int maxAge) {
        int _age = 0;
        List<Integer> patients = new ArrayList<>();
        String _gender =  "f";
        if (maxAge > 1) {
            maxAge = maxAge + 1;
        }
        for (Person person : personList) {

            _age = person.getAge(end);

            if (!patients.contains(person.getPersonId())
                    && (_age >= minAge && _age < maxAge)
                    && (person.getGender().toLowerCase().equals(_gender))) {

                patients.add(person.getPersonId());

            }
        }
        return patients.size();
    }


}

