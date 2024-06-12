package org.openmrs.module.ohrireports.datasetevaluator.linelist.linkageNew;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.LinkageNewLineListDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct.Linkage;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import static org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities.getDayDifference;

@Handler(supports = { LinkageNewLineListDataSetDefinition.class })
public class LinkageNewLineListDatasetEvaluator implements DataSetEvaluator {
	
	@Autowired
	LinkageNewLineListQuery linkageNewLineListQuery;
	
	/**
	 * Evaluate a DataSet for the given EvaluationContext
	 * 
	 * @return the evaluated <code>DataSet</code>
	 */
	@Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
        LinkageNewLineListDataSetDefinition linkageDataset = (LinkageNewLineListDataSetDefinition) dataSetDefinition;
        SimpleDataSet dataSet = new SimpleDataSet(linkageDataset, evalContext);

        // Check start date and end date are valid
        // If start date is greater than end date
        if (linkageDataset.getStartDate() != null && linkageDataset.getEndDate() != null
                && linkageDataset.getStartDate().compareTo(linkageDataset.getEndDate()) > 0) {
            //throw new EvaluationException("Start date cannot be greater than end date");
            DataSetRow row = new DataSetRow();
            row.addColumnValue(new DataSetColumn("Error", "Error", Integer.class),
                    "Report start date cannot be after report end date");
            dataSet.addRow(row);
            return dataSet;
        }

        linkageNewLineListQuery.initializeLinkage(linkageDataset.getStartDate(), linkageDataset.getEndDate());
        Cohort cohort = linkageNewLineListQuery.getBaseCohort();

        HashMap<Integer, Object> registrationDateHashMap = linkageNewLineListQuery.getObsValueDate(linkageNewLineListQuery.getBaseEncounters(),
                POSITIVE_TRACKING_REGISTRATION_DATE, cohort);
        HashMap<Integer, Object> confirmedDateHashMap = linkageNewLineListQuery.getObsValueDate(linkageNewLineListQuery.getBaseEncounters(),
                HIV_CONFIRMED_DATE, cohort);
        HashMap<Integer, Object> dateLinkedToCareHashMap = linkageNewLineListQuery.getObsValueDate(linkageNewLineListQuery.getBaseEncounters(),
                LINKED_TO_CARE_DATE, cohort);
        HashMap<Integer, Object> finalOutComeHashMap = linkageNewLineListQuery.getObsValueDate(linkageNewLineListQuery.getBaseEncounters(),
                FINAL_OUT_COME_DATE, cohort);
        HashMap<Integer,Object> planForNextHashMap = linkageNewLineListQuery.getConceptValue(PLAN_FOR_NEXT_STEP_POSITIVE_TRACKING,linkageNewLineListQuery.getBaseEncounters(),cohort);
        HashMap<Integer, Object> artStartedDateHashMap = linkageNewLineListQuery.getDate(cohort, ART_START_DATE);
        HashMap<Integer, Object> followHiveConfirmedDateHashMap = linkageNewLineListQuery.getDate(cohort, HIV_CONFIRMED_DATE);
        HashMap<Integer, Object> mrnIdentifierHashMap = linkageNewLineListQuery.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
        HashMap<Integer, Object> uanIdentifierHashMap = linkageNewLineListQuery.getIdentifier(cohort, UAN_PATIENT_IDENTIFIERS);
        HashMap<Integer, Object> entryPointHashMap = linkageNewLineListQuery.getConceptValue(ENTRE_POINT, linkageNewLineListQuery.getBaseEncounters(),
                cohort);


        DataSetRow row;

        List<Person> personList = LineListUtilities.sortPatientByName(linkageNewLineListQuery.getPersons());
        if (!personList.isEmpty()) {

            row = new DataSetRow();
            row.addColumnValue(new DataSetColumn("#", "#", Integer.class), "TOTAL");
            row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", Integer.class), personList.size());

            dataSet.addRow(row);
        } else {
            dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Patient Name", "MRN", "UAN", "Age", "Sex",
                    "Mobile No.", "Registration Date in E.C.", "Date Tested HIV +ve in E.C.", "Entry Point",
                    "HIV Confirmed Date in E.C.", "ART Start Date in E.C.", "Days Difference", "Linked to Care & Treatment?",
                    "Date Linked to Care & Treatment in E.C.", "Reason for not Starting ART the same day", "Plan for next Step",
                    "Final Outcome Known Date", "Final Outcome")));
        }
        int i = 1;
        for (Person person : personList) {

            Date registrationDate = linkageNewLineListQuery.getDate(registrationDateHashMap.get(person.getPersonId()));
            Date confirmedDate = linkageNewLineListQuery.getDate(confirmedDateHashMap.get(person.getPersonId()));
            Date followupConfirmedDate = linkageNewLineListQuery.getDate(followHiveConfirmedDateHashMap.get(person.getPersonId()));
            Date linkedToCareDate = linkageNewLineListQuery.getDate(dateLinkedToCareHashMap.get(person.getPersonId()));
            Date startArtDate = linkageNewLineListQuery.getDate(artStartedDateHashMap.get(person.getPersonId()));
            Date finalOutComeDate = linkageNewLineListQuery.getDate(finalOutComeHashMap.get(person.getPersonId()));
            Optional<Linkage> linkageOptional = linkageNewLineListQuery.getLinkages().stream().filter(l -> l.getPersonId() == person.getPersonId()).findFirst();
            if (!linkageOptional.isPresent())
                continue;
            Linkage linkage = linkageOptional.get();

            row = new DataSetRow();

            row.addColumnValue(new DataSetColumn("#", "#", Integer.class), i++);
            row.addColumnValue(new DataSetColumn("Patient Name", "Patient Name", String.class), person.getNames());
            row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), mrnIdentifierHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("UAN", "UAN", String.class), uanIdentifierHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge());
            row.addColumnValue(new DataSetColumn("Sex", "Sex", String.class), person.getGender());
            row.addColumnValue(new DataSetColumn("Mobile No.", "Mobile No.", String.class),
                    LineListUtilities.getPhone(person.getActiveAttributes()));
            row.addColumnValue(new DataSetColumn("Registration Date in E.C.", "Registration Date in E.C.", String.class),
                    linkageNewLineListQuery.getEthiopianDate(registrationDate));
            row.addColumnValue(
                    new DataSetColumn("Date Tested HIV +ve in E.C.", "Date Tested HIV +ve in E.C.", String.class),
                    linkageNewLineListQuery.getEthiopianDate(confirmedDate));
            row.addColumnValue(new DataSetColumn("Entry Point", "Entry Point", String.class),
                    entryPointHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("HIV Confirmed Date in E.C.", "HIV Confirmed Date in E.C.", String.class),
                    linkageNewLineListQuery.getEthiopianDate(followupConfirmedDate));
            row.addColumnValue(new DataSetColumn("ART Start Date in E.C.", "ART Start Date in E.C.", String.class),
                    linkageNewLineListQuery.getEthiopianDate(startArtDate));
            row.addColumnValue(new DataSetColumn("Days Difference", "Days Difference", Integer.class), getDateDifference(confirmedDate,startArtDate));
            row.addColumnValue(
                    new DataSetColumn("Linked to Care & Treatment?", "Linked to Care & Treatment?", String.class),
                    linkage.getLinkedToCareAndTreatment().getName());
            row.addColumnValue(new DataSetColumn("Date Linked to Care & Treatment in E.C.",
                    "Date Linked to Care & Treatment in E.C.", String.class), linkageNewLineListQuery
                    .getEthiopianDate(linkedToCareDate));
            row.addColumnValue(new DataSetColumn("Reason for not Starting ART the same day",
                    "Reason for not Starting ART the same day", String.class),  linkage.getReasonForNotStartedArt().getName());
            row.addColumnValue(new DataSetColumn("Plan for next step", "Plan for next step", String.class),
                    planForNextHashMap.get(person.getPersonId()));
            row.addColumnValue(new DataSetColumn("Final Outcome Known Date", "Final Outcome Known Date", String.class),
                    linkageNewLineListQuery.getEthiopianDate(finalOutComeDate));
            row.addColumnValue(new DataSetColumn("Final Outcome", "Final Outcome", String.class),
                    linkage.getFinalOutCome().getName());


//            row.addColumnValue(new DataSetColumn("registrationDate", "Registration Date", Date.class), registrationDate);
//            row.addColumnValue(new DataSetColumn("registrationDateETC", "Registration  Date ETH", String.class),
//                    linkageNewLineListQuery.getEthiopianDate(registrationDate));
//
//            row.addColumnValue(new DataSetColumn("dateHivPositive", "Date Test Hiv Positive", Date.class), confirmedDate);
//            row.addColumnValue(new DataSetColumn("dateHivPositiveETC", "Date Test Hiv PositiveETH", String.class),
//                    linkageNewLineListQuery.getEthiopianDate(confirmedDate));
//
//            row.addColumnValue(new DataSetColumn("entre-point", "Enter Point", String.class),
//                    enterPointHashMap.get(person.getPersonId()));
//
//            row.addColumnValue(new DataSetColumn("HIV-Confirmed-Date", "HIV Confirmed Date (from ART Follow-up)", Date.class), followupConfirmedDate);
//            row.addColumnValue(new DataSetColumn("ConfirmedETC", "Confirmed  Date ETH", String.class),
//                    linkageNewLineListQuery.getEthiopianDate(followupConfirmedDate));
//
//            row.addColumnValue(new DataSetColumn("Linked-to-care-treatment?", "Linked to care & treatment?", String.class), linkage.getLinkedToCareAndTreatment().getName());
//
//            row.addColumnValue(new DataSetColumn("dateLinked", "Date Linked to care & treatment", Date.class), linkedToCareDate);
//            row.addColumnValue(new DataSetColumn("linkedToCareETC", "Date Linked to care & treatment ETH", String.class),
//                    linkageNewLineListQuery.getEthiopianDate(linkedToCareDate));
//
//            row.addColumnValue(new DataSetColumn("art-started", "Art Started?", String.class), linkage.getStartedArt().getName());
//
//            row.addColumnValue(new DataSetColumn("artStartedDate", "Art Start Date", Date.class), startArtDate);
//            row.addColumnValue(new DataSetColumn("art-start-dateETC", "Art Start  ETH", String.class),
//                    linkageNewLineListQuery.getEthiopianDate(startArtDate));
//
//            row.addColumnValue(new DataSetColumn("reason-for-not-starting-ART", "Reason for not Starting ART the same day", String.class), linkage.getReasonForNotStartedArt().getName());
//
//            row.addColumnValue(new DataSetColumn("plan-for-next-step", "Plan for next step", String.class), planForNextHashMap.get(person.getPersonId()));
//
//            row.addColumnValue(new DataSetColumn("final-outcome", "final-outcome?", String.class), linkage.getFinalOutCome().getName());
//
//            row.addColumnValue(new DataSetColumn("final-outcomeDate", "Final OutCome Date", Date.class), finalOutComeDate);
//            row.addColumnValue(new DataSetColumn("final-out-come-ETC", "Final Outcome Date ETH", String.class),
//                    linkageNewLineListQuery.getEthiopianDate(finalOutComeDate));
//
//            row.addColumnValue(new DataSetColumn("phone-number", "Phone number", String.class), person.getAttribute("Home Phone"));
//            row.addColumnValue(new DataSetColumn("days-difference-between-hiv-confirmed-art-start-date", "Days difference b/n HIV confirmed & ART start date", String.class), getDateDifference(confirmedDate,startArtDate));
            dataSet.addRow(row);

        }

        return dataSet;
    }
	
	private int getDateDifference(Date confirmedDate, Date startArtDate) {
		if (Objects.isNull(confirmedDate) || Objects.isNull(startArtDate))
			return 0;
		return (int) TimeUnit.DAYS
		        .convert(Math.abs(startArtDate.getTime() - confirmedDate.getTime()), TimeUnit.MILLISECONDS);
	}
	
	private Object getStringIdentifier(Object patientIdentifier) {
		return Objects.isNull(patientIdentifier) ? "--" : patientIdentifier.toString();
	}
}
