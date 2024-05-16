package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei;

import org.openmrs.module.ohrireports.api.impl.query.pmtct.HEIHMISQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISHEICOTRDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class HMISHEICOTREvaluator {
	
	@Autowired
	HEIHMISQuery heihmisQuery;
	
	public void buildDataset(Date start, Date end, SimpleDataSet dataSet) {
		heihmisQuery.generateHMISCORT(start, end);
		RowBuilder builder = new RowBuilder();
		dataSet.addRow(builder
		        .buildDatasetColumn(
		            "MTCT_HEI_COTR",
		            "Percentage of exposed Infants born to HIV positive women who were started on co-trimoxazole prophylaxis within two months of birth",
		            ""));
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_COTR.1.",
		    "Number of infants born to HIV positive women started on co-trimoxazole prophylaxis within two months of birth",
		    heihmisQuery.getBaseCohort().size()));
		
	}
}
