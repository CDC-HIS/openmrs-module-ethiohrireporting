package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_plhiv;

import java.text.DecimalFormat;
import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.ohrireports.constants.FollowUpConceptQuestions;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.Gender;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

@Component
@Scope("prototype")
public class HivPlHivEvaluator {

    @Autowired
    private HivPlvHivQuery hivPlvHivQuery;
	
	List<Person> personList = new ArrayList<>();
    List<Person> personPregnantList = new ArrayList<>();
    List<Person> personNonPregnantList = new ArrayList<>();

    @Autowired
    private EncounterQuery encounterQuery;
	private Date end;
    private int underNourished, supplementaryFood =0;

    public void buildDataset(Date start, Date end, SimpleDataSet dataSet) {
	    this.end = end;
      

        hivPlvHivQuery.setStartDate(start);
        hivPlvHivQuery.setEndDate(end, FollowUpConceptQuestions.FOLLOW_UP_DATE);

        Cohort plhivCohort = hivPlvHivQuery.getAllCohortPLHIVMalnutrition(hivPlvHivQuery.getBaseEncounter());
        Cohort plhivMAMCohort = hivPlvHivQuery.getAllNUTMAMForAdult(hivPlvHivQuery.getBaseEncounter(), plhivCohort,
                Arrays.asList(ConceptAnswer.MILD_MAL_NUTRITION, ConceptAnswer.MODERATE_MAL_NUTRITION));

        Cohort plhivSAMCohort = hivPlvHivQuery.getAllNUTSAMForAdult(hivPlvHivQuery.getBaseEncounter(),
                plhivCohort, Collections.singletonList(ConceptAnswer.SEVERE_MAL_NUTRITION));

        Cohort plhivMAMSUPCohort = hivPlvHivQuery.getAllSUP(hivPlvHivQuery.getBaseEncounter(), plhivMAMCohort);
	    int totalMAM = hivPlvHivQuery.getPersons(plhivMAMCohort).size();
        Cohort plhivSAMSUPCohort = hivPlvHivQuery.getAllSUP(hivPlvHivQuery.getBaseEncounter(), plhivSAMCohort);
	    int totalSAM = hivPlvHivQuery.getPersons(plhivSAMCohort).size();



        personList = hivPlvHivQuery.getPersons(plhivCohort);
        dataSet.addRow(buildColumn("HIV_PLHIV_TSP", "Proportion of clinically undernourished People Living with HIV (PLHIV)" +
                " who received therapeutic or supplementary food", personList.size()));
        int headerIndex = dataSet.getRows().size();
        
        dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1", "Number of PLHIV who were assessed/screened for malnutrition", personList.size()));

        dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.1", "< 15 years, Male", getCohortSizeByAgeAndGender(0, 15, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.2", "< 15 years, Female", getCohortSizeByAgeAndGender(0, 15, Gender.Female)));
        dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.3", ">= 15 years, Male", getCohortSizeByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_TSP.1.4", ">= 15 years, Female", getCohortSizeByAgeAndGender(15, 150, Gender.Female)));

        personList = hivPlvHivQuery.getPersons(plhivMAMCohort);
        //totalMAM = personList.size();
        underNourished =hivPlvHivQuery.getPersons(plhivMAMCohort).size() + hivPlvHivQuery.getPersons(plhivSAMCohort).size();
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT", "Number of PLHIV who were nutritionally assessed" +
                "and found to be clinically undernourished (disaggregated by Age, Sex and Pregnancy)",underNourished));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM", "Total MAM", personList.size()));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.1", "< 15 years, Male", getCohortSizeByAgeAndGender(0, 15, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.2", "< 15 years, Female", getCohortSizeByAgeAndGender(0, 15, Gender.Female)));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.3", ">= 15 years, Male", getCohortSizeByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_MAM.4", ">= 15 years, Female", getCohortSizeByAgeAndGender(15, 150, Gender.Female)));

        personList = hivPlvHivQuery.getPersons(plhivSAMCohort);
        //totalSAM = personList.size();
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_SAM", "Total SAM", personList.size()));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_SAM.1", "< 15 years, Male", getCohortSizeByAgeAndGender(0, 15, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_SAM.2", "< 15 years, Female", getCohortSizeByAgeAndGender(0, 15, Gender.Female)));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_SAM.3", ">= 15 years, Male", getCohortSizeByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_NUT_SAM.4", ">= 15 years, Female", getCohortSizeByAgeAndGender(15, 150, Gender.Female)));


        personList = hivPlvHivQuery.getPersons(plhivMAMSUPCohort);
        personPregnantList = hivPlvHivQuery.getPersons(hivPlvHivQuery.getPatientByPregnantStatus(plhivMAMSUPCohort, ConceptAnswer.YES,
                hivPlvHivQuery.getBaseEncounter()));
        personNonPregnantList = hivPlvHivQuery.getPersons(hivPlvHivQuery.getPatientByPregnantStatus(plhivMAMSUPCohort, ConceptAnswer.NO,
                hivPlvHivQuery.getBaseEncounter()));
        supplementaryFood =hivPlvHivQuery.getPersons(plhivMAMSUPCohort).size() + hivPlvHivQuery.getPersons(plhivSAMSUPCohort).size();
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP", "Clinically undernourished PLHIV who received therapeutic or supplementary food (disaggregated by age, sex and pregnancy status)",
               supplementaryFood ));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.1", "Total MAM who received therapeutic or supplementary food", personList.size()));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.1.1", "< 15 years, Male",
                getCohortSizeByAgeGenderAndPregnancyStatus(0, 15, Gender.Male, personList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.1.2", "< 15 years, Female - pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(0, 15, Gender.Female, personPregnantList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.1.3", "< 15 years, Female - non-pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(0, 15, Gender.Female, personNonPregnantList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.1.4", ">= 15 years, Male",
                getCohortSizeByAgeGenderAndPregnancyStatus(15, 150, Gender.Male, personList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.1.5", ">= 15 years, Female - pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(15, 150, Gender.Female, personPregnantList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.1.6", ">= 15 years, Female - non-pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(15, 150, Gender.Female, personNonPregnantList)));


        personList = hivPlvHivQuery.getPersons(plhivSAMSUPCohort);
        personPregnantList = hivPlvHivQuery.getPersons(hivPlvHivQuery.getPatientByPregnantStatus(plhivSAMSUPCohort, ConceptAnswer.YES,
                hivPlvHivQuery.getBaseEncounter()));
        personNonPregnantList = hivPlvHivQuery.getPersons(hivPlvHivQuery.getPatientByPregnantStatus(plhivSAMSUPCohort, ConceptAnswer.NO,
                hivPlvHivQuery.getBaseEncounter()));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.2", "Total SAM who received therapeutic or supplementary food", personList.size()));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.2.1", "< 15 years, Male",
                getCohortSizeByAgeAndGender(0, 15, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.2.2", "< 15 years, Female - pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(0, 15, Gender.Female, personPregnantList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.2.3", "< 15 years, Female - non-pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(0, 15, Gender.Female, personNonPregnantList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.2.4", ">= 15 years, Male",
                getCohortSizeByAgeAndGender(15, 150, Gender.Male)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.2.5", ">= 15 years, Female - pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(15, 150, Gender.Female, personPregnantList)));
        dataSet.addRow(buildColumn("HIV_PLHIV_SUP.2.6", ">= 15 years, Female - non-pregnant",
                getCohortSizeByAgeGenderAndPregnancyStatus(15, 150, Gender.Female, personNonPregnantList)));
        
        //update proportion value
        dataSet.addRow(headerIndex,buildColumn("HIV_PLHIV_TSP", "Proportion of clinically undernourished People Living with HIV (PLHIV)" +
                " who received therapeutic or supplementary food", getProportion()));

    }

    private String getProportion() {
        if(underNourished <=0)
            return "0 %";
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        String output = String.valueOf(Double.parseDouble(decimalFormat.format((supplementaryFood/Double.valueOf(underNourished))*100)));
        return output +" %";

    }

    private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
        DataSetRow prepDataSetRow = new DataSetRow();
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                col_1_value);
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
	    
	    String COLUMN_3_NAME = "Number";
	    prepDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
                col_3_value);

        return prepDataSetRow;
    }

    private DataSetRow buildColumn(String col_1_value, String col_2_value, String col_3_value) {
        DataSetRow prepDataSetRow = new DataSetRow();
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
                col_1_value);
        prepDataSetRow.addColumnValue(
                new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);

        String COLUMN_3_NAME = "Number";
        prepDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, String.class),
                col_3_value);

        return prepDataSetRow;
    }

    private Integer getCohortSizeByAgeAndGender(int minAge, int maxAge, Gender gender) {
        int _age = 0;
        List<Integer> patients = new ArrayList<>();
        String _gender = gender.equals(Gender.Female) ? "f" : "m";
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

    private Integer getCohortSizeByAgeGenderAndPregnancyStatus(int minAge, int maxAge, Gender gender, List<Person> personList1) {
        int _age = 0;
        List<Integer> patients = new ArrayList<>();
        String _gender = gender.equals(Gender.Female) ? "f" : "m";
        if (maxAge > 1) {
            maxAge = maxAge + 1;
        }
        for (Person person : personList1) {

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
