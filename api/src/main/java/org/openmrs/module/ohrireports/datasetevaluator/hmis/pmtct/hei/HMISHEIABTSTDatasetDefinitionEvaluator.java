package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.HEIHMISQuery;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISEIDDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISHEIABTSTDatasetDefinition;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.pmtct.HMISHEICOTRDatasetDefinition;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.NEGATIVE;
import static org.openmrs.module.ohrireports.OHRIReportsConstants.POSITIVE;

@Handler(supports = { HMISHEIABTSTDatasetDefinition.class })
public class HMISHEIABTSTDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	HEIHMISQuery heihmisQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HMISHEIABTSTDatasetDefinition dsd = (HMISHEIABTSTDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		heihmisQuery.generateHMISCONFORMATORY(dsd.getStartDate(), dsd.getEndDate());
		
		RowBuilder builder = new RowBuilder();
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST",
		    "Percentage of HIV exposed infants receiving HIV confirmatory (antibody test) test by 18 months", ""));
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST.1.",
		    "Number of HIV exposed infants receiving HIV confirmatory (antibody test) by 18 months",
		    heihmisQuery.getCountForRapidAntiBodyTestByResult(POSITIVE)));
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST.1.1", "Positive", heihmisQuery.getBaseCohort().size()));
		dataSet.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST.1.2", "Negative",
		    heihmisQuery.getCountForRapidAntiBodyTestByResult(NEGATIVE)));
		
		return dataSet;
	}
}
