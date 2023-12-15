package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_rtt;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Date;

import org.openmrs.Obs;
import java.util.HashMap;

import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.AggregateBuilderImp;
import org.openmrs.module.ohrireports.api.impl.query.RTTQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_rtt.TxRttByAgeAndSexDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxRttByAgeAndSexDataSetDefinition.class })
public class TxRttByAgeAndSexDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private RTTQuery rttQuery;
	
	@Autowired
	private AggregateBuilder aggregateBuilder;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		TxRttByAgeAndSexDataSetDefinition _datasetDefinition = (TxRttByAgeAndSexDataSetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		
		List<Person> personList = rttQuery.getPersons(rttQuery.getBaseCohort());
		
		aggregateBuilder.setCalculateAgeFrom(_datasetDefinition.getEndDate());
		aggregateBuilder.setLowerBoundAge(0);
		aggregateBuilder.setUpperBoundAge(65);
		aggregateBuilder.setPersonList(personList);
		
		DataSetRow femaleDateSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(femaleDateSet, "F");
		set.addRow(femaleDateSet);
		
		DataSetRow maleDataSet = new DataSetRow();
		aggregateBuilder.buildDataSetColumn(maleDataSet, "M");
		
		set.addRow(maleDataSet);
		return set;
	}
}
