package org.openmrs.module.ohrireports.datasetevaluator.hmis.pep;

import static org.openmrs.module.ohrireports.constants.ConceptAnswer.SEXUAL_ASSAULT;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.OCCUPATIONAL;
import static org.openmrs.module.ohrireports.constants.ConceptAnswer.NON_OCCUPATIONAL;

import org.openmrs.module.ohrireports.api.impl.query.PepQuery;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope("prototype")
public class HIVPEPCategoryEvaluators {
	
	@Autowired
	private PepQuery query;
	
	public void buildDataset(Date start, Date end, SimpleDataSet dataset) {
		
		query.generateReport(start, end);
		int total = 0;
		int occupationCount = query.getCountByExposureType(OCCUPATIONAL);
		int sexualAssaultCount = query.getCountByExposureType(SEXUAL_ASSAULT);
		int nonOccupationCount = query.getCountByExposureType(NON_OCCUPATIONAL);
		
		DataSetRow hivPepRow = new DataSetRow();
		hivPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP");
		hivPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    "Number of persons provided with post-exposure prophylaxis (PEP) for risk of HIV infection by exposure type");
		String column_3_name = "Number";
		hivPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), total);
		
		dataset.addRow(hivPepRow);
		
		DataSetRow occupationalPepRow = new DataSetRow();
		occupationalPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP.1");
		occupationalPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Occupation");
		occupationalPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class), occupationCount);
		
		dataset.addRow(occupationalPepRow);
		
		DataSetRow SexualAssaultPepRow = new DataSetRow();
		SexualAssaultPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP.2");
		SexualAssaultPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), "Sexual violence");
		SexualAssaultPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    sexualAssaultCount);
		
		dataset.addRow(SexualAssaultPepRow);
		
		DataSetRow otherNonOccupationPepRow = new DataSetRow();
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class), "HIV_PEP.3");
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class),
		    "Other Non-occupational");
		otherNonOccupationPepRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
		    nonOccupationCount);
		
		dataset.addRow(otherNonOccupationPepRow);
		
	}
	
}
