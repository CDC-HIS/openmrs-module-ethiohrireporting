package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.tx_curr.HmisTXCurrDataSetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr.RegimentCategory.REGIMENT_TYPE;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HmisTXCurrDataSetDefinition.class })
public class HmisTXCurrDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	ConceptService conceptService;
	
	@Autowired
	private HmisCurrQuery hmisCurrQuery;
	
	@Autowired
	private EncounterQuery encounterQuery;
	
	SimpleDataSet data = null;
	
	private HmisTXCurrDataSetDefinition hdsd;
	
	Cohort firstLineCohort;
	
	Cohort secondLineCohort;
	
	Cohort thirdLineCohort;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (HmisTXCurrDataSetDefinition) dataSetDefinition;
		
		data = new SimpleDataSet(dataSetDefinition, evalContext);
		List<Integer> aliveFollowUpEncounters = encounterQuery.getAliveFollowUpEncounters(null, hdsd.getEndDate());
		
		hmisCurrQuery.loadInitialCohort(hdsd.getEndDate(), aliveFollowUpEncounters);
		
		setHeaderRow();
		firstLineCohort = hmisCurrQuery.getByRegiment(RegimentCategory.getRegimentConcepts(REGIMENT_TYPE.FIRST_LINE),
		    hmisCurrQuery.getBaseCohort());
		secondLineCohort = hmisCurrQuery.getByRegiment(RegimentCategory.getRegimentConcepts(REGIMENT_TYPE.SECOND_LINE),
		    hmisCurrQuery.getBaseCohort());
		thirdLineCohort = hmisCurrQuery.getByRegiment(RegimentCategory.getRegimentConcepts(REGIMENT_TYPE.THIRD_LINE),
		    hmisCurrQuery.getBaseCohort());
		
		new AggregateByAgeAndGender(hmisCurrQuery, firstLineCohort, secondLineCohort, thirdLineCohort, data);
		new AggregateByPregnancyStatus(hmisCurrQuery, data);
		new AggregateByAgeAndRegiment(hmisCurrQuery, data, firstLineCohort, secondLineCohort, thirdLineCohort);
		new AggregateByAgeGenderAndPregnancyStatus(hmisCurrQuery, data, firstLineCohort, secondLineCohort, thirdLineCohort,
		        aliveFollowUpEncounters);
		return data;
		
	}
	
	private void setHeaderRow() {
		DataSetRow mainHeaderRow = new DataSetRow();
		mainHeaderRow.addColumnValue(new DataSetColumn("S.NO", "S.NO", String.class), "HIV_HIV_Treatment");
		mainHeaderRow.addColumnValue(new DataSetColumn("Activity", "Activity", String.class),
		    "Does health facility provide Monthly PMTCT / ART Treatment Service?");
		
		data.addRow(mainHeaderRow);
		
		DataSetRow subHeaderRow = new DataSetRow();
		subHeaderRow.addColumnValue(new DataSetColumn("S.NO", "S.NO", String.class), "HIV_TX_CURR_ALL");
		subHeaderRow.addColumnValue(new DataSetColumn("Activity", "Activity", String.class),
		    "Number of adults and children who are currently on ART by age, sex and regimen category");
		subHeaderRow.addColumnValue(new DataSetColumn("Number", "Number", Integer.class), hmisCurrQuery.getBaseCohort()
		        .size());
		
		data.addRow(subHeaderRow);
		
	}
	
}
