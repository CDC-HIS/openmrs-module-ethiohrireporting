package org.openmrs.module.ohrireports.datasetevaluator.linelist.Tb;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.MRN_PATIENT_IDENTIFIERS;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.SPECIMEN_SENT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.LF_LAM_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.GENE_XPERT_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_DIAGNOSTIC_TEST_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.DIAGNOSTIC_TEST;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.TXTBDataSetDefinition;
import org.openmrs.module.ohrireports.reports.linelist.TXTBReport;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TXTBDataSetDefinition.class })
public class TXTBDataSetDefinitionEvaluator implements DataSetEvaluator {

	private PatientQueryService patientQuery;

	@Autowired
	private TBQuery tbQuery;

	@Autowired
	private TbQueryLineList tbQueryLineList;

	private TXTBDataSetDefinition hdsd;
	HashMap<Integer, Object> mrnIdentifierHashMap;
	HashMap<Integer, Object> tbTxStartDictionary = new HashMap<>();

	private HashMap<Integer, Object> screenedResultHashMap;

	private HashMap<Integer, Object> regimentDictionary;

	private HashMap<Integer, Object> artStartDictionary, specimen, geneXpertResult, _LAMResults, OtherDiagnosticTest,
			OtherDiagResult;

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		hdsd = (TXTBDataSetDefinition) dataSetDefinition;

		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
		patientQuery = Context.getService(PatientQueryService.class);
				Calendar subSixMonth = Calendar.getInstance();
		subSixMonth.setTime(hdsd.getStartDate());
		subSixMonth.add(Calendar.MONTH, -6);
		Date prevSixMonth = subSixMonth.getTime();
		hdsd.setStartDate(prevSixMonth);
		Cohort cohort = patientQuery.getActiveOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null);

		List<Integer> baseEncountersOfTreatmentStartDate = patientQuery.getBaseEncounters(TB_TREATMENT_START_DATE, hdsd.getStartDate(), hdsd.getEndDate());
		tbQueryLineList.setEncountersByScreenDate(
				tbQuery.getLatestEncounterIds());

		if (hdsd.getType().equals(TXTBReport.numerator)) {
			cohort = tbQuery.getTBTreatmentStartedCohort(cohort, hdsd.getStartDate(), hdsd.getEndDate(), "");
			tbTxStartDictionary = tbQueryLineList.getTBTreatmentStartDate(cohort,baseEncountersOfTreatmentStartDate);

		} else {
			cohort = tbQuery.getTBScreenedCohort(cohort, hdsd.getStartDate(), hdsd.getEndDate());
			tbTxStartDictionary = tbQueryLineList.getTBScreenedDate(cohort);

		}

		mrnIdentifierHashMap = tbQueryLineList.getIdentifier(cohort, MRN_PATIENT_IDENTIFIERS);
		screenedResultHashMap = tbQueryLineList.getTBScreenedResult(cohort);
		artStartDictionary = tbQueryLineList.getArtStartDate(cohort, null,
				hdsd.getEndDate());
		regimentDictionary = tbQueryLineList.getRegiment(
				patientQuery.getBaseEncountersByFollowUpDate(hdsd.getStartDate(), hdsd.getEndDate()), cohort);
		specimen = tbQueryLineList.getByResultTypeQuery(cohort, SPECIMEN_SENT);
		_LAMResults = tbQueryLineList.getByResultTypeQuery(cohort, LF_LAM_RESULT);
		OtherDiagnosticTest = tbQueryLineList.getByResultTypeQuery(cohort,
				DIAGNOSTIC_TEST);
		geneXpertResult = tbQueryLineList.getByResultTypeQuery(cohort,
				GENE_XPERT_RESULT);
		OtherDiagResult = tbQueryLineList.getByResultTypeQuery(cohort,
				TB_DIAGNOSTIC_TEST_RESULT);

		List<Person> persons = patientQuery.getPersons(cohort);

		DataSetRow row;

		if (persons.size() > 0) {

			row = new DataSetRow();

			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), "TOTAL");
			row.addColumnValue(new DataSetColumn("Name", "Name", Integer.class), persons.size());

			data.addRow(row);
		}
		if (hdsd.getType().equals(TXTBReport.numerator)) {
			getRowForNumerator(data, persons);
		} else {

			getRowForDenominator(data, persons);
		}

		return data;
	}

	private void getRowForNumerator(SimpleDataSet data, List<Person> persons) {
		DataSetRow row = new DataSetRow();
		for (Person person : persons) {

			row = new DataSetRow();
			Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
			String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

			addBaseColumns(row, person);

			row.addColumnValue(new DataSetColumn("TBTreatmentStartDate", "TB Treatment Start Date", Date.class),
					treatmentStartDate);

			row.addColumnValue(new DataSetColumn("TBTXTreatmentDateEth", "TB Treatment Start Date ETH", String.class),
					txEthiopianDate);

			data.addRow(row);
		}
	}

	private void getRowForDenominator(SimpleDataSet data, List<Person> persons) {
		DataSetRow row = new DataSetRow();
		for (Person person : persons) {

			row = new DataSetRow();
			Date treatmentStartDate = tbQueryLineList.getDate(tbTxStartDictionary.get(person.getPersonId()));
			String txEthiopianDate = tbQueryLineList.getEthiopianDate(treatmentStartDate);

			addBaseColumns(row, person);

			row.addColumnValue(new DataSetColumn("ScreenedResult", "Screen Result", String.class),
					screenedResultHashMap.get(person.getPersonId()));

			row.addColumnValue(new DataSetColumn("TBScreenedDate", "TB Screened Date", Date.class), treatmentStartDate);

			row.addColumnValue(new DataSetColumn("TBTXScreenedDateEth", "TB Screened Start Date ETH", String.class),
					txEthiopianDate);

			data.addRow(row);
		}
	}

	private void addBaseColumns(DataSetRow row, Person person) {
		Date artStartDate = tbQueryLineList.getDate(artStartDictionary.get(person.getPersonId()));
		String artEthiopianDate = tbQueryLineList.getEthiopianDate(artStartDate);

		row.addColumnValue(new DataSetColumn("MRN", "MRN", Integer.class),
				mrnIdentifierHashMap.get(person.getPersonId()));

		row.addColumnValue(new DataSetColumn("Name", "Name", String.class), person.getNames());
		row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), person.getAge(hdsd.getEndDate()));
		row.addColumnValue(new DataSetColumn("Gender", "Gender", Integer.class), person.getGender());

		row.addColumnValue(new DataSetColumn("ArtStartDate", "Art Start Date", Date.class), artStartDate);
		row.addColumnValue(new DataSetColumn("ArtStartDateEth", "Art Start Date ETH", String.class),
				artEthiopianDate);

		row.addColumnValue(new DataSetColumn("Regimen", "Regimen", String.class),
				regimentDictionary.get(person.getPersonId()));

		row.addColumnValue(new DataSetColumn("SpecimenSent", "Specimen Sent", String.class),
				specimen.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("LF_LAM-result", "LF_LAM Result", String.class),
				_LAMResults.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("Gene_Xpert-Result", "Gene_Xpert Result", String.class),
				geneXpertResult.get(person.getPersonId()));
		row.addColumnValue(new DataSetColumn("OtherTBDiagnosticTest", "Other TB Diagnostic Test", String.class),
				OtherDiagnosticTest.get(person.getPersonId()));
		row.addColumnValue(
				new DataSetColumn("OtherTBDiagnosticResult", "Other TB Diagnostic Test Result", String.class),
				OtherDiagResult.get(person.getPersonId()));
	}

}
