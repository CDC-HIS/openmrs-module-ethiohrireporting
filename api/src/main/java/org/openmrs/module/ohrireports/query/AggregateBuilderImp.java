package org.openmrs.module.ohrireports.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.openmrs.Person;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.springframework.stereotype.Service;

@Service
public class AggregateBuilderImp extends BaseOpenmrsService implements AggregateBuilder {

    protected int subTotalCount = 0;
    protected Set<Integer> countedPatientId = new HashSet<>();
    protected Set<Person> persons = new HashSet<>();
    protected int lowerBoundAge =1;
    protected int upperBoundAge = 65;
    protected int ageInterval = 4;

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
    public void setPersonList(Collection<Person> person)
    {
        persons.clear();
        persons.addAll(person);
    }

    @Override
    public  void buildDataSetColumn(DataSetRow dataSet, String gender) {
        subTotalCount = 0;
        int minCount = lowerBoundAge;
        int maxCount = lowerBoundAge+ageInterval;

        dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", Integer.class),
                gender.equals("F") ? "Female"
                        : "Male");
        dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                getEnrolledByUnknownAge());

        while (minCount <= upperBoundAge) {
            if (minCount == upperBoundAge) {
                dataSet.addColumnValue(new DataSetColumn(upperBoundAge+"+", upperBoundAge+"+", Integer.class),
                        getEnrolledByAgeAndGender(upperBoundAge, 200));
            } else {
                dataSet.addColumnValue(
                        new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                        getEnrolledByAgeAndGender(minCount, maxCount));
            }
            minCount = maxCount + 1;
            maxCount = minCount + ageInterval;
        }
        dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), subTotalCount);
    }

    protected int getEnrolledByAgeAndGender(int min, int max) {
        int count = 0;
        Set<Person> countedPersons = new HashSet<>();
        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;
                
                if (person.getAge() >= min && person.getAge() <= max) {
                countedPersons.add(person);
                countedPatientId.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearProcessedPersons(countedPersons);
        return count;
    }

    protected int getEnrolledByUnknownAge() {
        int count = 0;
        Set<Person> countedPersons = new HashSet<>();
        
        for (Person person : persons) {
            
            if (countedPatientId.contains(person.getPersonId()))
                continue;

            if ((Objects.isNull(person.getAge()) ||
                    person.getAge() <= 0)) {
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

    protected void clearProcessedPersons(Set<Person> processedPersons) {
        for (Person person : processedPersons) {
            persons.removeIf(p -> p.getPersonId().equals(person.getPersonId()));
        }
    }

}
