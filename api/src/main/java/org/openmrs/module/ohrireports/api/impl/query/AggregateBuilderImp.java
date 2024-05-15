package org.openmrs.module.ohrireports.api.impl.query;

import java.util.*;

import org.openmrs.Person;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.springframework.stereotype.Service;

@Service
public class AggregateBuilderImp extends BaseOpenmrsService implements AggregateBuilder {

    protected int subTotalCount = 0;


    protected int total = 0;
    protected Set<Integer> countedPatientId = new HashSet<>();
    protected List<Person> persons = new ArrayList<>();
    //this hash map is used to store the person id and last followup date
    protected Map<Integer, Object> followUpDate = new HashMap<>();

    public void setFollowUpDate(HashMap<Integer, Object> followUpDate) {
        this.followUpDate = followUpDate;
    }

    protected int lowerBoundAge = 0;
    protected int upperBoundAge = 65;
    protected int ageInterval = 4;

    private final String _below = "below";
    private final String _above = "above";

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int subTotalCount) {
        this.total = this.total + subTotalCount;
    }

    public void clearTotal() {
        this.total = 0;
    }

    @Override
    public void buildDataSetColumn(DataSetRow dataSet, String gender) {
        if (Objects.equals(gender, "F") || Objects.equals(gender, "M")) {

            subTotalCount = 0;
            int minCount = lowerBoundAge + 1;
            int maxCount = lowerBoundAge + ageInterval;

            dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "", String.class),
                    gender.equals("F") ? "Female"
                            : "Male");
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                    getEnrolledByUnknownAge(gender));
            dataSet.addColumnValue(new DataSetColumn("below 1", "<1", Integer.class),
                    getEnrolledByAgeAndGender(0, 1, gender));
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
            setTotal(subTotalCount);
            dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), subTotalCount);
        } else if (Objects.equals(gender, "T")) {
            dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "", String.class),
                    "Sub-Total");
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class), getTotal());
        }
    }

    public void buildDataSetColumnWithFollowUpDate(DataSetRow dataSet, String gender) {
        if (Objects.equals(gender, "F") || Objects.equals(gender, "M")) {

            subTotalCount = 0;
            int minCount = lowerBoundAge + 1;
            int maxCount = lowerBoundAge + ageInterval;

            dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "", String.class),
                    gender.equals("F") ? "Female"
                            : "Male");
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                    getEnrolledByUnknownAge(gender));
            dataSet.addColumnValue(new DataSetColumn("below 1", "<1", Integer.class),
                    getEnrolledByAgeAndGenderByFollowUpDate(0, 1, gender));
            while (minCount <= upperBoundAge) {
                if (minCount == upperBoundAge) {
                    dataSet.addColumnValue(new DataSetColumn(upperBoundAge + "+", upperBoundAge + "+", Integer.class),
                            getEnrolledByAgeAndGenderByFollowUpDate(upperBoundAge, 200, gender));
                } else {
                    dataSet.addColumnValue(
                            new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                            getEnrolledByAgeAndGenderByFollowUpDate(minCount, maxCount, gender));
                }
                minCount = maxCount + 1;
                maxCount = minCount + ageInterval;
            }
            setTotal(subTotalCount);
            dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), subTotalCount);
        } else if (Objects.equals(gender, "T")) {
            dataSet.addColumnValue(new DataSetColumn("ByAgeAndSexData", "", String.class),
                    "Sub-Total");
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class), getTotal());
        }
    }

    public void addTotalRow(DataSetRow dataSetRow) {
        dataSetRow.addColumnValue(new DataSetColumn("Total", "Total", Integer.class), getTotal());

    }

    public void buildDataSetColumnForScreening(DataSetRow dataSet, String screeningResult) {
        if (Objects.equals(screeningResult, "Negative") || Objects.equals(screeningResult, "Positive")
                || Objects.equals(screeningResult, "Suspicious")) {
            subTotalCount = 0;
            lowerBoundAge = 14;
            upperBoundAge = 50;
            int minCount = lowerBoundAge + 1;
            int maxCount = minCount + ageInterval;

            dataSet.addColumnValue(new DataSetColumn("CxCaScreeningResult", "", String.class),
                    screeningResult);
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                    getPersonCountByUnknownAge());

            while (minCount <= upperBoundAge) {
                if (minCount == upperBoundAge) {
                    dataSet.addColumnValue(new DataSetColumn(upperBoundAge + "+", upperBoundAge + "+", Integer.class),
                            getPersonByAge(upperBoundAge, 200));
                } else {
                    dataSet.addColumnValue(
                            new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                            getPersonByAge(minCount, maxCount));
                }
                minCount = maxCount + 1;
                maxCount = minCount + ageInterval;
            }
            setTotal(subTotalCount);
            dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), subTotalCount);
        } else if (Objects.equals(screeningResult, "T")) {
            dataSet.addColumnValue(new DataSetColumn("CxCaScreeningResult", "", String.class),
                    "Sub-Total");
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class), getTotal());
        }
    }

    public void buildDataSetColumnForTreatment(DataSetRow dataSet, String treatmentType) {
        if (Objects.equals(treatmentType, "Cryotherapy") || Objects.equals(treatmentType, "LEEP")
                || Objects.equals(treatmentType, "Thermocoagulation")) {
            subTotalCount = 0;
            lowerBoundAge = 14;
            upperBoundAge = 65;

            int minCount = lowerBoundAge + 1;
            int maxCount = minCount + ageInterval;

            dataSet.addColumnValue(new DataSetColumn("CxCaTreatmentType", "", String.class),
                    treatmentType);
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                    getPersonCountByUnknownAge());
            while (minCount <= upperBoundAge) {
                if (minCount == upperBoundAge) {
                    dataSet.addColumnValue(new DataSetColumn(upperBoundAge + "+", upperBoundAge + "+", Integer.class),
                            getPersonByAge(upperBoundAge, 200));
                } else {
                    dataSet.addColumnValue(
                            new DataSetColumn(minCount + "-" + maxCount, minCount + "-" + maxCount, Integer.class),
                            getPersonByAge(minCount, maxCount));
                }
                minCount = maxCount + 1;
                maxCount = minCount + ageInterval;
            }
            setTotal(subTotalCount);
            dataSet.addColumnValue(new DataSetColumn("Sub-total", "Subtotal", Integer.class), subTotalCount);
        } else if (Objects.equals(treatmentType, "T")) {
            dataSet.addColumnValue(new DataSetColumn("CxCaTreatmentType", "", String.class),
                    "Sub-Total");
            dataSet.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class), getTotal());
        }
    }

    @Override
    public void buildDataSetColumn(DataSetRow row, String gender, int middleAge) {
        if (Objects.equals(gender, "F") || Objects.equals(gender, "M")) {
            row.addColumnValue(new DataSetColumn("ByAgeAndSexData", "", String.class),
                    gender.equals("F") ? "Female"
                            : "Male");

            row.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class),
                    getEnrolledByUnknownAge(gender));
            row.addColumnValue(new DataSetColumn(">" + middleAge, "<" + middleAge, Integer.class),
                    getCountByMiddleAge(gender, middleAge, _below));

            row.addColumnValue(new DataSetColumn(middleAge + "+", middleAge + "+", Integer.class),
                    getCountByMiddleAge(gender, middleAge, _above));
        } else if (Objects.equals(gender, "T")) {
            row.addColumnValue(new DataSetColumn("ByAgeAndSexData", "", String.class),
                    "Sub-Total");
            row.addColumnValue(new DataSetColumn("unknownAge", "Unknown Age", Integer.class), getTotal());
        }

    }

    protected int getEnrolledByAgeAndGender(int min, int max, String gender) {
        int count = 0;
        int age = 0;
        List<Person> countedPersons = new ArrayList<>();

        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;

            age = person.getAge(calculateAgeFrom);
            if (person.getGender().equals(gender) && min == 0 && age < max) {
                countedPersons.add(person);
                countedPatientId.add(person.getPersonId());
                count++;
            } else if (person.getGender().equals(gender) && age >= min && age <= max) {
                countedPersons.add(person);
                countedPatientId.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearProcessedPersons(countedPersons);
        return count;
    }

    protected int getEnrolledByAgeAndGenderByFollowUpDate(int min, int max, String gender) {
        int count = 0;
        int age = 0;
        List<Person> countedPersons = new ArrayList<>();

        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;

            age = person.getAge((java.sql.Timestamp) followUpDate.get(person.getPersonId()));
            if (person.getGender().equals(gender) && min == 0 && age < max) {
                countedPersons.add(person);
                countedPatientId.add(person.getPersonId());
                count++;
            } else if (person.getGender().equals(gender) && age >= min && age <= max) {
                countedPersons.add(person);
                countedPatientId.add(person.getPersonId());
                count++;
            }
        }
        incrementTotalCount(count);
        clearProcessedPersons(countedPersons);
        return count;
    }

    protected int getPersonByAge(int min, int max) {
        int count = 0;
        int age = 0;
        List<Person> countedPersons = new ArrayList<>();

        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;

            age = person.getAge(calculateAgeFrom);
            if (age >= min && age <= max) {
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

    protected int getPersonCountByUnknownAge() {
        int count = 0;
        int age = 0;

        List<Person> countedPersons = new ArrayList<>();

        for (Person person : persons) {

            if (countedPatientId.contains(person.getPersonId()))
                continue;

            age = person.getAge(calculateAgeFrom);
            if ((Objects.isNull(person.getAge()) ||
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
                if (Objects.equals(range, _below) && age < middleAge) {
                    countedPersons.add(person);
                    countedPatientId.add(person.getPersonId());
                    count++;
                } else if (Objects.equals(range, _above) && age >= middleAge) {
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
