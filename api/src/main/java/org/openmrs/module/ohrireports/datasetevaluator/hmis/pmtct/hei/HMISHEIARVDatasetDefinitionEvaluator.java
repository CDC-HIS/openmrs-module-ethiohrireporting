package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.HEIHMISQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISEIDDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISHEICOTRDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = {HMISHEARVDatasetDefinition.class})
public class HMISHEARVDatasetDefinitionEvaluator implements DataSetEvaluator {
	@Autowired
	HEIHMISQuery heihmisQuery;
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HMISHEARVDatasetDefinition dsd     = (HMISHEARVDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition,evalContext);
		heihmisQuery.generateHMISCORT(dsd.getStartDate(),dsd.getEndDate());
		RowBuilder builder = new RowBuilder();
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_COTR",
				"Percentage of exposed Infants born to HIV positive women who were started on co-trimoxazole prophylaxis within two months of birth",""));
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_COTR.1.",
				"Number of infants born to HIV positive women started on co-trimoxazole prophylaxis within two months of birth",heihmisQuery.getBaseCohort().size()));
		
		
		
		return dataSet;
	}
}
