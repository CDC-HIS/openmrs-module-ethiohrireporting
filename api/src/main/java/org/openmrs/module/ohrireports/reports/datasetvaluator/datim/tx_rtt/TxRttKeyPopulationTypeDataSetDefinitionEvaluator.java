package org.openmrs.module.ohrireports.reports.datasetvaluator.datim.tx_rtt;



import java.util.ArrayList;


import java.util.List;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.datim.tx_rtt.*;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TxRttKeyPopulationTypeDataSetDefinition.class })
public class TxRttKeyPopulationTypeDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TxRttKeyPopulationTypeDataSetDefinition hdsd;


    List<Obs> obses = new ArrayList<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxRttKeyPopulationTypeDataSetDefinition) dataSetDefinition;
		context = evalContext;
        SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
		// total=0;
        DataSetRow keyPupulation = new DataSetRow();
        keyPupulation.addColumnValue(new DataSetColumn("keyPopulation", "key Population Type",Integer.class), "PWID");
        keyPupulation.addColumnValue(new DataSetColumn("value", "value", Integer.class),0);
        set.addRow(keyPupulation);
		keyPupulation = new DataSetRow();
		keyPupulation.addColumnValue(new DataSetColumn("keyPopulation", "key Population Type",Integer.class), "MSM");
        keyPupulation.addColumnValue(new DataSetColumn("value", "value", Integer.class),0);
		set.addRow(keyPupulation);
		keyPupulation = new DataSetRow();
		keyPupulation.addColumnValue(new DataSetColumn("keyPopulation", "key Population Type",Integer.class), "Transgender people");
        keyPupulation.addColumnValue(new DataSetColumn("value", "value", Integer.class),0);
		set.addRow(keyPupulation);
		keyPupulation = new DataSetRow();
		keyPupulation.addColumnValue(new DataSetColumn("keyPopulation", "key Population Type",Integer.class), "FSM");
        keyPupulation.addColumnValue(new DataSetColumn("value", "value", Integer.class),0);
		set.addRow(keyPupulation);
		keyPupulation = new DataSetRow();
		keyPupulation.addColumnValue(new DataSetColumn("keyPopulation", "key Population Type",Integer.class), "People in prison and other closed setting");
        keyPupulation.addColumnValue(new DataSetColumn("value", "value", Integer.class),0);
		set.addRow(keyPupulation); 
        return set;
	}
	}
