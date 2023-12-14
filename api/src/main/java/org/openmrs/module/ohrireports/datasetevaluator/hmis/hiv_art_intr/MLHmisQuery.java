package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_intr;

import java.math.BigDecimal;
import java.util.*;

import org.openmrs.Cohort;
import org.openmrs.CohortMembership;
import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.MLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MLHmisQuery {
    private HashMap<Integer, BigDecimal> interruptedArvWithMonth = new HashMap<>();
    @Autowired
    private MLQuery mlQuery;
    private Date endDate = Calendar.getInstance().getTime();
    public void loadInterruptedCohort(Date startDate, Date endDate) {
        mlQuery.getCohortML(startDate, endDate);
    }
    public Cohort getBaseCohort() {
        return mlQuery.cohort;
    }
    public HashMap<Integer, BigDecimal> getLostToFollowUp(Date _endDate) {
        if (!endDate.equals(_endDate) && !interruptedArvWithMonth.isEmpty()) {
            return interruptedArvWithMonth;
        }
        endDate = _endDate;
        return interruptedArvWithMonth = mlQuery.getInterruptionMonth(endDate);
    }
    public Cohort getDead(Cohort cohort) {
        return mlQuery.getDied(cohort);
    }
    public Cohort getRefusedOrStopped(Cohort cohort) {
        return mlQuery.getStopOrRefused(cohort);
    }
    public Cohort getTransferredOut(Cohort cohort) {
        return mlQuery.getTransferredOut(cohort);
    }
    public Cohort getBelowThreeMonthInterruption(HashMap<Integer, BigDecimal> interruptedArvWithMonth) {
        Cohort cohort = new Cohort();
        interruptedArvWithMonth.forEach((k, m) -> {
            if (m.intValue() < 3) {
                cohort.addMembership(new CohortMembership(k));
            }
        });
        return cohort;
    }
    public Cohort getAboveThreeMonthInterruption(HashMap<Integer, BigDecimal> interruptedArvWithMonth) {
        Cohort cohort = new Cohort();
        interruptedArvWithMonth.forEach((k, m) -> {
            if (m.intValue() >= 3) {
                cohort.addMembership(new CohortMembership(k));
            }
        });

        return cohort;
    }
    public Integer getByAgeAndGender(Range ageRange, String gender, List<Person> persons) {

        List<Integer> countedId = new ArrayList<>();
        int age = 0;
        for (Person person : persons) {
            age = person.getAge(endDate);
            if (ageRange == Range.LESS_THAN_FIFTY && age < 15 && person.getGender().equals(gender)) {
                countedId.add(person.getPersonId());
            } else if (ageRange == Range.ABOVE_OR_EQUAL_TO_FIFTY && age >= 15 && person.getGender().equals(gender)) {
                countedId.add(person.getPersonId());
            }
        }

        return countedId.size();
    }
    public List<Person> getPerson(Cohort cohort) {
        return mlQuery.getPersons(cohort);
    }
    enum Range {
        ABOVE_OR_EQUAL_TO_FIFTY, LESS_THAN_FIFTY
    }
}
