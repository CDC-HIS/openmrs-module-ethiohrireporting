package org.openmrs.module.ohrireports.datasetevaluator.linelist.linkageNew;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.LinkageNewLineListDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct.Linkage;
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
        SimpleDataSet data = new SimpleDataSet(linkageDataset, evalContext);

        linkageNewLineListQuery.initializeLinkage(linkageDataset.getStartDate(), linkageDataset.getEndDate());
        Cohort cohort = linkageNewLineListQuery.getBaseCohort();
        List<Person> persons = linkageNewLineListQuery.getPersons();

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
        HashMap<Integer, Object> enterPointHashMap = linkageNewLineListQuery.getConceptValue(ENTRE_POINT, linkageNewLineListQuery.getBaseEncounters(),
                cohort);


        DataSetRow row;
        if (!persons.isEmpty()) {

            row = new DataSetRow();

            row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
            row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());

            data.addRow(row);
        }

        for (Person person : persons) {

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

            row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
            row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class),
                    getStringIdentifier(mrnIdentifierHashMap.get(person.getPersonId())));
            row.addColumnValue(new DataSetColumn("UANO", "UANO", String.class),
                    getStringIdentifier(uanIdentifierHashMap.get(person.getPersonId())));
            row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class),
                    person.getAge(linkageDataset.getEndDate()));
            row.addColumnValue(new DataSetColumn("Gender", "Gender", String.class), person.getGender());

            row.addColumnValue(new DataSetColumn("registrationDate", "Registration Date", Date.class), registrationDate);
            row.addColumnValue(new DataSetColumn("registrationDateETC", "Registration  Date ETH", String.class),
                    linkageNewLineListQuery.getEthiopianDate(registrationDate));

            row.addColumnValue(new DataSetColumn("dateHivPositive", "Date Test Hiv Positive", Date.class), confirmedDate);
            row.addColumnValue(new DataSetColumn("dateHivPositiveETC", "Date Test Hiv PositiveETH", String.class),
                    linkageNewLineListQuery.getEthiopianDate(confirmedDate));

            row.addColumnValue(new DataSetColumn("entre-point", "Enter Point", String.class),
                    enterPointHashMap.get(person.getPersonId()));

            row.addColumnValue(new DataSetColumn("HIV-Confirmed-Date", "HIV Confirmed Date (from ART Follow-up)", Date.class), followupConfirmedDate);
            row.addColumnValue(new DataSetColumn("ConfirmedETC", "Confirmed  Date ETH", String.class),
                    linkageNewLineListQuery.getEthiopianDate(followupConfirmedDate));

            row.addColumnValue(new DataSetColumn("Linked-to-care-treatment?", "Linked to care & treatment?", String.class), linkage.getLinkedToCareAndTreatment().getName());

            row.addColumnValue(new DataSetColumn("dateLinked", "Date Linked to care & treatment", Date.class), linkedToCareDate);
            row.addColumnValue(new DataSetColumn("linkedToCareETC", "Date Linked to care & treatment ETH", String.class),
                    linkageNewLineListQuery.getEthiopianDate(linkedToCareDate));

            row.addColumnValue(new DataSetColumn("art-started", "Art Started?", String.class), linkage.getStartedArt().getName());

            row.addColumnValue(new DataSetColumn("artStartedDate", "Art Start Date", Date.class), startArtDate);
            row.addColumnValue(new DataSetColumn("art-start-dateETC", "Art Start  ETH", String.class),
                    linkageNewLineListQuery.getEthiopianDate(startArtDate));

            row.addColumnValue(new DataSetColumn("reason-for-not-starting-ART", "Reason for not Starting ART the same day", String.class), linkage.getReasonForNotStartedArt().getName());

            row.addColumnValue(new DataSetColumn("plan-for-next-step", "Plan for next step", String.class), planForNextHashMap.get(person.getPersonId()));

            row.addColumnValue(new DataSetColumn("final-outcome", "final-outcome?", String.class), linkage.getFinalOutCome().getName());

            row.addColumnValue(new DataSetColumn("final-outcomeDate", "Final OutCome Date", Date.class), finalOutComeDate);
            row.addColumnValue(new DataSetColumn("final-out-come-ETC", "Final Outcome Date ETH", String.class),
                    linkageNewLineListQuery.getEthiopianDate(finalOutComeDate));

            row.addColumnValue(new DataSetColumn("phone-number", "Phone number", String.class), person.getAttribute("Home Phone"));
            row.addColumnValue(new DataSetColumn("days-difference-between-hiv-confirmed-art-start-date", "Days difference b/n HIV confirmed & ART start date", String.class), getDateDifference(confirmedDate,startArtDate));
            data.addRow(row);

        }

        return data;
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
