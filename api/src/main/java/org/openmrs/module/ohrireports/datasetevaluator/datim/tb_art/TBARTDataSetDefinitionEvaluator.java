package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_art;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.ART_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_DIAGNOSTIC_TEST_RESULT;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TREATMENT_END_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_SCREENING_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_TREATMENT_START_DATE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.POSITIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.TB_ACTIVE_DATE;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_art.TBARTDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TBARTDataSetDefinition.class })
public class TBARTDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private TBARTDataSetDefinition hdsd;
	
	@Autowired
	private TBQuery tbQuery;
	
	private SimpleDataSet simpleDataSet;
	
	private List<Person> persons;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	@Autowired
	private AggregateBuilder _AggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TBARTDataSetDefinition) dataSetDefinition;
		simpleDataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		List<Integer> encounter = encounterQuery.getEncounters(Arrays.asList(TB_TREATMENT_START_DATE, TB_ACTIVE_DATE), null,
		    hdsd.getEndDate());
		List<Integer> followUpEncounter = encounterQuery.getAliveFollowUpEncounters(hdsd.getEndDate());
		Cohort cohort = tbQuery.getActiveOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null, encounter);
		Cohort activeTBCohort = tbQuery.getCurrentOnActiveTB(cohort, hdsd.getStartDate(), hdsd.getEndDate(), encounter);
		
		Cohort alreadyOnARTCohort = new Cohort(tbQuery.getArtStartedCohort(followUpEncounter, null, hdsd.getStartDate(),
		    activeTBCohort));
		Cohort newOnARTCohort = new Cohort(tbQuery.getArtStartedCohort(followUpEncounter, hdsd.getStartDate(),
		    hdsd.getEndDate(), activeTBCohort));
		
		DataSetRow descRow = new DataSetRow();
		descRow.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", String.class), "Already On ART");
		simpleDataSet.addRow(descRow);
		buildRow(alreadyOnARTCohort);
		
		DataSetRow newDescRow = new DataSetRow();
		newDescRow.addColumnValue(new DataSetColumn("ByAgeAndSexData", "Gender", String.class), "New On ART");
		simpleDataSet.addRow(newDescRow);
		buildRow(newOnARTCohort);
		
		return simpleDataSet;
		
	}
	
	private void buildRow(Cohort cohort) {
		persons = tbQuery.getPersons(cohort);
		_AggregateBuilder.setPersonList(persons);
		
		DataSetRow femaleSetRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(femaleSetRow, "F");
		simpleDataSet.addRow(femaleSetRow);
		
		DataSetRow maleSetRow = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(maleSetRow, "M");
		simpleDataSet.addRow(maleSetRow);
	}
	
}
