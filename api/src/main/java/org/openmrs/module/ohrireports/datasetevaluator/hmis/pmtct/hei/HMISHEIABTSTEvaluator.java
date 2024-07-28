package org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei;

import org.openmrs.module.ohrireports.api.impl.query.pmtct.HEIHMISQuery;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.RowBuilder;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.openmrs.module.ohrireports.constants.ConceptAnswer.NEGATIVE;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.POSITIVE;

@Component
@Scope("prototype")
public class HMISHEIABTSTEvaluator {
	
	@Autowired
	HEIHMISQuery heihmisQuery;
	
	public void buildDataset(Date start, Date end, SimpleDataSet dataset) {
		heihmisQuery.generateHMISCONFORMATORY(start, end);
		
		RowBuilder builder = new RowBuilder();
		dataset.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST",
		    "Percentage of HIV exposed infants receiving HIV confirmatory (antibody test) test by 18 months", ""));
		dataset.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST.1.",
		    "Number of HIV exposed infants receiving HIV confirmatory (antibody test) by 18 months",
		    heihmisQuery.getCountForRapidAntiBodyTestByResult(POSITIVE)));
		dataset.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST.1.1", "Positive", heihmisQuery.getBaseCohort().size()));
		dataset.addRow(builder.buildDatasetColumn("MTCT_HEI_ABTST.1.2", "Negative",
		    heihmisQuery.getCountForRapidAntiBodyTestByResult(NEGATIVE)));
		
	}
}
