package org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_scrn;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.cxca_scrn.HmisCxCaScrnDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import static org.openmrs.module.ohrireports.reports.datasetvaluator.hmis.HMISConstant.*;

@Handler(supports = { HmisCxCaScrnDataSetDefinition.class })
public class HmisCxCaScrnDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	private String baseName = "HIV_CXCA_SCRN. ";
	private String COLUMN_3_NAME = "Number";
	
	private HmisCxCaScrnDataSetDefinition hdsd;
		
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	List<Obs> obses = new ArrayList<>();
	List<Integer> cxcaScreenedPatientsID = new ArrayList<>();
	private Concept hpvAndDNAScreeningResultConcept, positiveConcept, viaScreeningResultConcept, viaNegativeConcept,viaPositiveConcept,viaSuspiciousConcept;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (HmisCxCaScrnDataSetDefinition) dataSetDefinition;
		context = evalContext;
		cxcaScreenedPatientsID=getPatientByScreeningType();
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);	

		data.addRow(buildColumn("1","Cervical Cancer screening by type of test",0));
        data.addRow(buildColumn("1. 1","Screened by VIA",getVIAScreeningPatients()));
		data.addRow(buildColumn("1. 2","Screened by HPV DNA", getHPVANDDNAScreeningPatients()));
		data.addRow(buildColumn("2","VIA Screening Result",gettbscrnByAgeAndGender(20,24,Gender.Female)));
		viaScreeningResultConcept=conceptService.getConceptByUuid(VIA_SCREENING_RESULT);
		viaNegativeConcept = conceptService.getConceptByUuid(VIA_NEGATIVE);
        viaPositiveConcept = conceptService.getConceptByUuid(VIA_POSITIVE);
        viaSuspiciousConcept = conceptService.getConceptByUuid(VIA_SUSPICIOUS_RESULT);

		obses = getScreeningTypeCohort(viaScreeningResultConcept, viaNegativeConcept);
		data.addRow(buildColumn("2.1","Normal cervix:"
		,gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("2.1. 1","15 - 19 years"
		,gettbscrnByAgeAndGender(15,19,Gender.Female)));
		data.addRow(buildColumn("2.1. 2","20 - 24 years"
		,gettbscrnByAgeAndGender(20,24,Gender.Female)));
		data.addRow(buildColumn("2.1. 3","25 - 29 years"
		,gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("2.1. 4","30 - 49 years"
		,gettbscrnByAgeAndGender(30,49,Gender.Female)));
		data.addRow(buildColumn("2.1. 5",">= 50 years"
		,gettbscrnByAgeAndGender(51,150,Gender.Female)));
		
		obses = getScreeningTypeCohort(viaScreeningResultConcept, viaPositiveConcept);
        data.addRow(buildColumn("2.3","Precancerous Lesion:"
		,gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("2.3. 1","15 - 19 years"
		,gettbscrnByAgeAndGender(15,19,Gender.Female)));
		data.addRow(buildColumn("2.3. 2","20 - 24 years"
		,gettbscrnByAgeAndGender(20,24,Gender.Female)));
		data.addRow(buildColumn("2.3. 3","25 - 29 years"
		,gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("2.3. 4","30 - 49 years"
		,gettbscrnByAgeAndGender(30,49,Gender.Female)));
		data.addRow(buildColumn("2.3. 5",">= 50 years"
		,gettbscrnByAgeAndGender(51,150,Gender.Female)));

		obses = getScreeningTypeCohort(viaScreeningResultConcept, viaSuspiciousConcept);
        data.addRow(buildColumn("2.4","Suspecious cancerous Lesion:"
		,obses.size()));
		data.addRow(buildColumn("2.4. 1","15 - 19 years"
		,gettbscrnByAgeAndGender(15,19,Gender.Female)));
		data.addRow(buildColumn("2.4. 2","20 - 24 years"
		,gettbscrnByAgeAndGender(20,24,Gender.Female)));
		data.addRow(buildColumn("2.4. 3","25 - 29 years"
		,gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("2.4. 4","30 - 49 years"
		,gettbscrnByAgeAndGender(30,49,Gender.Female)));
		data.addRow(buildColumn("2.4. 5",">= 50 years"
		,gettbscrnByAgeAndGender(51,150,Gender.Female)));

		hpvAndDNAScreeningResultConcept = conceptService.getConceptByUuid(HPV_DNA_SCREENING_RESULT);
		positiveConcept = conceptService.getConceptByUuid(POSITIVE);
		obses = getScreeningTypeCohort(hpvAndDNAScreeningResultConcept, positiveConcept);
        data.addRow(buildColumn("2.5","HPV DNA test positive:"
		,obses.size()));
		data.addRow(buildColumn("2.5. 1","15 - 19 years"
		,gettbscrnByAgeAndGender(15,19,Gender.Female)));
		data.addRow(buildColumn("2.5. 2","20 - 24 years"
		,gettbscrnByAgeAndGender(20,24,Gender.Female)));
		data.addRow(buildColumn("2.5. 3","25 - 29 years"
		,gettbscrnByAgeAndGender(25,29,Gender.Female)));
		data.addRow(buildColumn("2.5. 4","30 - 49 years"
		,gettbscrnByAgeAndGender(30,49,Gender.Female)));
		data.addRow(buildColumn("2.5. 5",">= 50 years"
		,gettbscrnByAgeAndGender(51,150,Gender.Female)));


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
	
	private Integer gettbscrnByAgeAndGender(int minAge, int maxAge, Gender gender) {
        int _age = 0;
		List<Integer> patients = new ArrayList<>();
		String _gender=gender.equals(Gender.Female)?"f":"m";
		if (maxAge > 1){
				maxAge=maxAge+1;
			}
		for (Obs obs :obses) {
			_age =obs.getPerson().getAge();
			if (!patients.contains(obs.getPersonId()) 
			 && (_age>=minAge && _age< maxAge)
			 && (obs.getPerson().getGender().toLowerCase().equals(_gender))) {
				
				patients.add(obs.getPersonId());

			}
		}
		return patients.size();
    }

	private List<Obs> getScreeningTypeCohort(Concept strategy, Concept expectedResult) {
		List<Obs> localObs = new ArrayList<>();
		List<Integer> patients = new ArrayList<>();
        if (cxcaScreenedPatientsID.size() == 0)
                return localObs;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("distinct obs.personId")
                .from(Obs.class, "obs")
                .whereEqual("obs.concept", strategy)
                .and()
                .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
                .and()
                .whereEqual("obs.valueCoded", expectedResult)
                .whereBetweenInclusive("obs.obsDatetime", hdsd.getStartDate(),
                        hdsd.getEndDate())
                .and()
                .whereIn("obs.personId", cxcaScreenedPatientsID);

		for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
			if(!patients.contains(obs.getPersonId()))
					{
					patients.add(obs.getPersonId());
					localObs.add(obs);
					}
			}		
				return localObs;
    }


	private int getVIAScreeningPatients() {
		if (cxcaScreenedPatientsID.size() == 0)
                return 0;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();


		queryBuilder.select("distinct obs.personId")
			.from(Obs.class, "obs")
			.whereEqual("obs.concept", viaScreeningResultConcept)
			.and()
			.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
			.and()
			.whereIn("obs.valueCoded", Arrays.asList(
				conceptService.getConceptByUuid(VIA_NEGATIVE),
				conceptService.getConceptByUuid(VIA_POSITIVE),
				conceptService.getConceptByUuid(VIA_SUSPICIOUS_RESULT)))
			.whereBetweenInclusive("obs.obsDatetime", hdsd.getStartDate(),
							hdsd.getEndDate())
			.and()
			.whereIn("obs.personId", cxcaScreenedPatientsID);

		List<Integer> viaScreenedpersonIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
		return viaScreenedpersonIdList.size();
}

private Integer getHPVANDDNAScreeningPatients() {
	if (cxcaScreenedPatientsID.size() == 0)
                return 0;
	HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

	queryBuilder.select("distinct obs.personId")
		.from(Obs.class, "obs")
		.whereEqual("obs.concept", conceptService.getConceptByUuid(HPV_DNA_SCREENING_RESULT))
		.and().whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
		.and().whereIn("obs.valueCoded", Arrays.asList(
		conceptService.getConceptByUuid(POSITIVE),conceptService.getConceptByUuid(NEGATIVE),conceptService.getConceptByUuid(UNKNOWN)))
		.whereBetweenInclusive("obs.obsDatetime", hdsd.getStartDate(),hdsd.getEndDate())
		.and().whereIn("obs.personId", cxcaScreenedPatientsID);

	List<Integer> personIdList = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
	return personIdList.size();
}

private List<Integer> getPatientByScreeningType() {
		List<Integer> personIdList = getCXCAScreened();
		if (personIdList.size() == 0)
				return new ArrayList<>();
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs.personId")
			.from(Obs.class, "obs")
			.whereEqual("obs.concept", conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING))
			.and()
			.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
			.and()
			.whereIn("obs.valueCoded", Arrays.asList(conceptService.getConceptByUuid(CXCA_FIRST_TIME_SCREENING),
			conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING_POST_TREATMENT),
			conceptService.getConceptByUuid(CXCA_TYPE_OF_SCREENING_RESCREEN)))
			.and()
			.whereBetweenInclusive("obs.obsDatetime", hdsd.getStartDate(),
							hdsd.getEndDate())
			.whereIn("obs.personId", personIdList);

		return evaluationService.evaluateToList(queryBuilder, Integer.class, context);
}
	
	private List<Integer> getCXCAScreened() {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("distinct obs.personId");
		List<Integer> currentPatients = getArtstartedPatients();

		if (currentPatients.size() == 0)
				return new ArrayList<>();

		queryBuilder.from(Obs.class, "obs")
			.whereEqual("obs.person.gender", "F")
			.and()
			.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
			.and()
			.whereEqual("obs.concept", conceptService.getConceptByUuid(CXCA_SCREENING_ACCEPTED_DATE))
			.and()
			.whereBetweenInclusive("obs.valueDatetime", hdsd.getStartDate(),hdsd.getEndDate())
			.and().whereIn("obs.personId", currentPatients);
		List<Integer> personId = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
	   
		return personId;
}
	


	private List<Integer> getArtstartedPatients() {
		Calendar now = Calendar.getInstance();
                 now.add(Calendar.YEAR, -15);
		List<Integer> patientsId = getDatimTxCurrTotalEnrolledPatients();
		List<Integer> patients = new ArrayList<>();
        if (patientsId == null || patientsId.size() == 0)
                return patients;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs").from(Obs.class, "obs")
		.whereEqual("obs.concept", conceptService.getConceptByUuid(ART_START_DATE)).and()
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType()).and()
		.whereEqual("obs.person.gender", "F")
		.and().whereLessOrEqualToOrNull("obs.person.birthdate", now.getTime()).and()
        .whereLessOrEqualTo("obs.valueDatetime", hdsd.getEndDate()).and()
        .whereIdIn("obs.personId", patientsId)
        .orderDesc("obs.obsDatetime");

        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
			if(!patients.contains(obs.getPersonId()))
					{
					patients.add(obs.getPersonId());
					}
			}		
		return patients;
		
	}
	
	private List<Integer> getDatimTxCurrTotalEnrolledPatients() {

		List<Integer> patientsId = getListOfALiveORRestartPatientObservertions();
		List<Integer> patients = new ArrayList<>();
        if (patientsId == null || patientsId.size() == 0)
                return patients;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs");
        queryBuilder.from(Obs.class, "obs")
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and()
        .whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
        .and()
        .whereGreater("obs.valueDatetime", hdsd.getEndDate())
        .and()
        .whereLess("obs.obsDatetime", hdsd.getEndDate())
        .whereIdIn("obs.personId", patientsId)
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
                if(!patients.contains(obs.getPersonId()))
                        {
                        patients.add(obs.getPersonId());
                        }
        }
		// patients = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
				
		return patients;
	}
	
	private List<Integer> getListOfALiveORRestartPatientObservertions() {

		List<Integer> uniqiObs = new ArrayList<>();
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(FOLLOW_UP_STATUS))
				.and()
                .whereEqual("obs.person.gender", "F")
				.and()
				.whereIn("obs.valueCoded", Arrays.asList(conceptService.getConceptByUuid(ALIVE),conceptService.getConceptByUuid(RESTART)))
				.and().whereLess("obs.obsDatetime", hdsd.getEndDate());
		queryBuilder.orderDesc("obs.personId,obs.obsDatetime");

		List<Obs> liveObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

		for (Obs obs : liveObs) {
			if (!uniqiObs.contains(obs.getPersonId())) {
				uniqiObs.add(obs.getPersonId());
				// patientStatus.put(obs.getPersonId(), obs.getValueCoded());
			}
		}

		return uniqiObs;
	}

}
enum Gender {
	Female,
	Male
}