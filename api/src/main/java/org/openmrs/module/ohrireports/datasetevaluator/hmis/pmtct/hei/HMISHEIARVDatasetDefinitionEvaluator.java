package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.HEIHMISQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISHEIARVDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HMISHEIARVDatasetDefinition.class })
public class HMISHEIARVDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	HEIHMISQuery heihmisQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HMISHEIARVDatasetDefinition dsd = (HMISHEIARVDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		heihmisQuery.generateHMISIARV(dsd.getStartDate(), dsd.getEndDate());
		RowBuilder builder = new RowBuilder();
		
		dataSet.addRow(builder
		        .buildDatasetColumn(
		            "RMH_PMTCT_IARV",
		            "Percentage of Infants born to HIV-infected women receiving antiretroviral (ARV) prophylaxis for prevention of Women-to-child transmission (PMTCT)",
		            ""));
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_COTR.1.",
		    "Number of HIV exposed infants who received ARV prophylaxis For 12 weeks", heihmisQuery.getBaseCohort().size()));
		
		return dataSet;
	}
}
