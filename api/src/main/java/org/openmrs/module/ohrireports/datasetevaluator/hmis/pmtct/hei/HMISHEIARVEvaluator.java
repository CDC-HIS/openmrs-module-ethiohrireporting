package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei;

import org.openmrs.module.ohrireports.api.impl.query.pmtct.HEIHMISQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISHEIARVDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class HMISHEIARVEvaluator {
	
	@Autowired
	HEIHMISQuery heihmisQuery;
	
	public void buildDataset(Date start, Date end, SimpleDataSet dataSet) {
		
		heihmisQuery.generateHMISIARV(start, end);
		RowBuilder builder = new RowBuilder();
		
		dataSet.addRow(builder
		        .buildDatasetColumn(
		            "RMH_PMTCT_IARV",
		            "Percentage of Infants born to HIV-infected women receiving antiretroviral (ARV) prophylaxis for prevention of Women-to-child transmission (PMTCT)",
		            ""));
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_COTR.1.",
		    "Number of HIV exposed infants who received ARV prophylaxis For 12 weeks", heihmisQuery.getBaseCohort().size()));
		
	}
}
