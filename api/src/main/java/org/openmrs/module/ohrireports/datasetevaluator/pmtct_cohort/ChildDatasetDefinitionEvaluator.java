package org.openmrs.module.ohrireports.datasetevaluator.pmtct_cohort;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.cohort.PMTCTCalculationType;
import org.openmrs.module.ohrireports.api.impl.query.cohort.PMTCTCohort;
import org.openmrs.module.ohrireports.datasetdefinition.pmtct_cohort.ChildDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

@Handler(supports = { ChildDatasetDefinition.class })
public class ChildDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private final String DESCRIPTION = "Fill in the exact Month/Year of 12,18,24, and 30 months from the month of maternal PMTCT enrollment (MATERNAL COHORT MONTH & YEAR)";
	
	@Autowired
	private PMTCTCohort pmtctCohort;
	
	private final DataSetRow dataASetRow = new DataSetRow();
	
	private final DataSetRow dataBSetRow = new DataSetRow();
	
	private final DataSetRow dataCSetRow = new DataSetRow();
	
	private final DataSetRow dataDSetRow = new DataSetRow();
	
	private final DataSetRow dataESetRow = new DataSetRow();
	
	private final DataSetRow dataFSetRow = new DataSetRow();
	
	private final DataSetRow dataGSetRow = new DataSetRow();
	
	private final DataSetRow dataHSetRow = new DataSetRow();
	
	private final DataSetRow dataISetRow = new DataSetRow();
	
	private final DataSetRow dataJSetRow = new DataSetRow();
	
	private final DataSetRow dataKSetRow = new DataSetRow();
	
	private final DataSetRow dataLSetRow = new DataSetRow();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		ChildDatasetDefinition motherDatasetDefinition = (ChildDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		String TITLE = "title";
		dataASetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "A");
		dataASetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "Number of HEI born to HIV+ mothers who enrolled in PMTCT during this cohort month/year.");
		dataBSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "B");
		dataBSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "Total number of HEI Transfer in (TI) since Month 0");
		dataCSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "C");
		dataCSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "Number of HEI in the current cohort (A+B)");
		dataDSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "D");
		dataDSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "HEI Lost to F/U (not seen > 1 month after scheduled appointment)");
		dataESetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "E");
		dataESetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "HEI with DNA PCR test collected by 2 month age");
		dataFSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "F");
		dataFSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "HEI with DNA PCR test collected between 2 and 12 month of age");
		dataGSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "G");
		dataGSetRow
		        .addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), "HEI discharged negative (DN)");
		dataHSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "H");
		dataHSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), "HEI diagnosed positive (P)");
		dataISetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "I");
		dataISetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), "Final HEI Lost to F/U ");
		
		dataJSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "J");
		dataJSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "HEI still exposed/breastfeeding (CPT) ");
		
		dataKSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "K");
		dataKSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), "HEI known dead (D) ");
		
		dataLSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "L");
		dataLSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "HEI transferred out (TO to another facility -- NOT to ART clinic)");
		
		pmtctCohort.generateBaseReport(motherDatasetDefinition.getStartDate(), motherDatasetDefinition.getEndDate());
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 12));
		String MONTH_TWELVE = "Maternal Cohort Month 12 [mm/yy]";
		buildRow(MONTH_TWELVE);
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 18));
		String MONTH_EIGHTEEN = "Maternal Cohort Month 18 [mm/yy]";
		buildRow(MONTH_EIGHTEEN);
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 24));
		String MONTH_TWENTY_FOUR = "Maternal Cohort Month 24 [mm/yy]";
		buildRow(MONTH_TWENTY_FOUR);
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 30));
		String MONTH_THIRTY = "Maternal Cohort Month 30 [mm/yy]";
		buildRow(MONTH_THIRTY);
		
		dataSet.addRow(dataASetRow);
		dataSet.addRow(dataBSetRow);
		dataSet.addRow(dataCSetRow);
		dataSet.addRow(getRowDescription("LOST TO FOLLOW UP - (report at MATERNAL COHORT MONTH 12, 18, 24)"));
		
		dataSet.addRow(dataDSetRow);
		dataSet.addRow(getRowDescription("HEI WITH DNA PCR COLLECTION - (report at MATERNAL COHORT MONTH 12,18)"));
		
		dataSet.addRow(dataESetRow);
		dataSet.addRow(dataFSetRow);
		dataSet.addRow(getRowDescription("HEI FINAL OUTCOME - (report at MATERNAL COHORT MONTH 30)"));
		
		dataSet.addRow(dataGSetRow);
		dataSet.addRow(dataHSetRow);
		dataSet.addRow(dataISetRow);
		dataSet.addRow(dataJSetRow);
		dataSet.addRow(dataKSetRow);
		dataSet.addRow(dataLSetRow);
		
		return dataSet;
	}
	
	private void buildRow(String month) {
		
		dataBSetRow.addColumnValue(new DataSetColumn(month, month, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.ALL_TI).intValue());
		dataCSetRow.addColumnValue(new DataSetColumn(month, month, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.ALL_TO).intValue());
		dataDSetRow.addColumnValue(new DataSetColumn(month, month, Integer.class),
		    pmtctCohort.getCount(PMTCTCalculationType.NET_CURRENT_COHORT).intValue());
		
		dataESetRow.addColumnValue(new DataSetColumn(month, month, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.MOTHER_ALIVE_AND_ON_ART).intValue());
		dataFSetRow.addColumnValue(new DataSetColumn(month, month, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.LOST_TO_FOLLOW_UP).intValue());
		dataGSetRow.addColumnValue(new DataSetColumn(month, month, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.KNOWN_DEAD).intValue());
		
		dataHSetRow.addColumnValue(new DataSetColumn(month, month, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.PERCENTAGE_NET_CURRENT_COHORT_ALIVE_AND_ON_ART) + "%");
		dataISetRow.addColumnValue(new DataSetColumn(month, month, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.PERCENTAGE_NET_CURRENT_COHORT_LOST_TO_FOLLOW_UP) + "%");
	}
	
	private DataSetRow getRowDescription(String description) {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), description);
		return row;
	}
	
	public Date addMonth(Date date, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}
	
}
