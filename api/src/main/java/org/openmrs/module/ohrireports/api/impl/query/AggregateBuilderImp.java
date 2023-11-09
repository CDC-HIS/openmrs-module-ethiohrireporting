package org.openmrs.module.ohrireports.api.impl.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.openmrs.Person;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.stereotype.Service;

@Service
public class AggregateBuilderImp extends BaseOpenmrsService implements AggregateBuilder {

    protected int subTotalCount = 0;
    protected Set<Integer> countedPatientId = new HashSet<>();
    protected List<Person> persons = new ArrayList<>();
    protected int lowerBoundAge = 0;
    protected int upperBoundAge = 65;
    protected int ageInterval = 4;

    private String _below = "below";
    private String _above = "above";

    private Date calculateAgeFrom;

    public Date getCalculateAgeFrom() {
        if (calculateAgeFrom == null)
            return new Date();

        return calculateAgeFrom;
    }

    public void setCalculateAgeFrom(Date calculateAgeFrom) {

        this.calculateAgeFrom = calculateAgeFrom;
    }

    @Override
    public void setLowerBoundAge(int lowerBoundAge) {
        this.lowerBoundAge = lowerBoundAge;
    }

    @Override
    public void setUpperBoundAge(int upperBoundAge) {
        this.upperBoundAge = upperBoundAge;
    }

    @Override
    public void setAgeInterval(int ageInterval) {
        this.ageInterval = ageInterval;
    }

    @Override
    public void setPersonList(List<Person> person) {
        countedPatientId.clear();
        persons = person;
    }

    @Override
    public void buildDataSetColumn(DataSetRow dataSet, String gender) {
        subTotalCount = 0;
        int minCount = lowerBoundAge + 1;
        int maxCount = lowerBoundAge + ageInterval;

        dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", String.class),
                gender.equals("F") ? "Female"
                        : "Male");
        dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                getEnrolledByUnknownAge(gender));

        while (minCount <= upperBoundAge) {
            if (minCount == upperBoundAge) {
                dataSet.addColumnValue(new DataSetColumn(upperBoundAge + "+", upperBoundAge + "+", Integer.class),
                        getEnrolledByAgeAndGender(upperBoundAge, 200, gender));
            } else {
                dataSet.addColumnValue(
                        new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                        getEnrolledByAgeAndGender(minCount, maxCount, gender));
            }
            minCount = maxCount + 1;
            maxCount = minCount + ageInterval;
        }
        dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), subTotalCount);
    }

    @Override
    public void buildDataSetColumn(DataSetRow row, String gender, int middleAge) {
        row.addColumnValue(new DataSetColumn("gender", "Gender", String.class),
                gender.equals("F") ? "Female"
                        : "Male");

        row.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                getEnrolledByUnknownAge(gender));
        row.addColumnValue(new DataSetColumn("<" + middleAge, "<" + middleAge, Integer.class),
                getCountByMiddleAge(gender, middleAge, _below));

        row.addColumnValue(new DataSetColumn(middleAge + "+", middleAge + "+", Integer.class),
                getCountByMiddleAge(gender, middleAge, _above));
  
    }

    protected int getEnrolledByAgeAndGender(int min, int max, String gender) {
        int count = 0;
        int age = 0;
        List<Person> countedPersons = new ArrayList<>();

        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;

            age = person.getAge(calculateAgeFrom);
            if (person.getGender().equals(gender) && age >= min && age <= max) {
                countedPersons.add(person);
                countedPatientId.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearProcessedPersons(countedPersons);
        return count;
    }

    protected int getEnrolledByUnknownAge(String gender) {
        int count = 0;
        int age = 0;

        List<Person> countedPersons = new ArrayList<>();

        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;

            age = person.getAge(calculateAgeFrom);
            if (person.getGender().equals(gender) && (Objects.isNull(person.getAge()) ||
                    age <= 0)) {
                countedPersons.add(person);
                countedPatientId.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearProcessedPersons(countedPersons);
        return count;
    }

    protected void incrementTotalCount(int count) {
        if (count > 0)
            subTotalCount = subTotalCount + count;
    }

    protected void clearProcessedPersons(List<Person> processedPersons) {
        for (Person person : processedPersons) {
            persons.removeIf(p -> p.getPersonId().equals(person.getPersonId()));
        }
    }

    private int getCountByMiddleAge(String gender, int middleAge, String range) {
        int count = 0;
        int age = 0;
        List<Person> countedPersons = new ArrayList<>();
        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;
            age = person.getAge(calculateAgeFrom);
            if (person.getGender().equals(gender)) {
                if (range == _below && age < middleAge) {
                    countedPersons.add(person);
                    countedPatientId.add(person.getPersonId());
                    count++;
                } else if (range == _above && age >= middleAge) {
                    countedPersons.add(person);
                    countedPatientId.add(person.getPersonId());
                    count++;
                }

            }
        }
        incrementTotalCount(count);
        clearProcessedPersons(countedPersons);
        return count;
    }

}
