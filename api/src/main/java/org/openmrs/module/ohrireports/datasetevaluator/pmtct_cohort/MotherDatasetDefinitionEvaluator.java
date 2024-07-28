package org.openmrs.module.ohrireports.datasetevaluator.pmtct_cohort;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.cohort.PMTCTCalculationType;
import org.openmrs.module.ohrireports.api.impl.query.cohort.PMTCTCohort;
import org.openmrs.module.ohrireports.datasetdefinition.pmtct_cohort.MotherDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;
import sun.security.krb5.internal.crypto.Des;

import java.util.Calendar;
import java.util.Date;

@Handler(supports = { MotherDatasetDefinition.class })
public class MotherDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private final String DESCRIPTION = "Fill in the exact Month/Year of the month of mother's PMTCT enrollment (COHORT MONTH & YEAR =Month 0), and Month 3, 6, and 12 months since PMTCT enrollment.";
	
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
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		MotherDatasetDefinition motherDatasetDefinition = (MotherDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		String TITLE = "title";
		dataASetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "A");
		dataASetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "Number of HIV-infected women enrolled in PMTCT in this facility during this month and year (Month 0)");
		dataBSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "B");
		dataBSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "Total number of Transfer in (TI) since Month 0");
		dataCSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "C");
		dataCSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "Total number of Transfer out (TO) since Month 0");
		dataDSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "D");
		dataDSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "Number of mothers in the current cohort = Net current cohort (A+B-C)");
		dataESetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "E");
		dataESetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), "Mothers Alive and on ART");
		dataFSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "F");
		dataFSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "lOST TO f/u (not seen > 1 month after scheduled appointment");
		dataGSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "G");
		dataGSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), "Known dead");
		dataHSetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "H");
		dataHSetRow.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		    "% of mothers in net current cohort Alive and on ART [(E/D*100% at 3,6,and 12 months since PMTCT enrollment)");
		dataISetRow.addColumnValue(new DataSetColumn(TITLE, "#", String.class), "I");
		dataISetRow
		        .addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class),
		            "% of mothers in net current cohort Lost to F/U and on ART [(F/D*100% at 3,6,and 12 months since PMTCT enrollment)");
		
		pmtctCohort.generateBaseReport(motherDatasetDefinition.getStartDate(), motherDatasetDefinition.getEndDate());
		
		String MONTH_ZERO = "Maternal Cohort Month 0 [mm/yy]";
		dataASetRow.addColumnValue(new DataSetColumn(MONTH_ZERO, MONTH_ZERO, String.class),
		    pmtctCohort.getCount(PMTCTCalculationType.IN_FACILITY_ENROLLED).intValue());
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 3));
		String MONTH_THREE = "Maternal Cohort Month 3 [mm/yy]";
		buildRow(MONTH_THREE);
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 6));
		String MONTH_SIX = "Maternal Cohort Month 6 [mm/yy]";
		buildRow(MONTH_SIX);
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 12));
		String MONTH_TWELVE = "Maternal Cohort Month 12 [mm/yy]";
		buildRow(MONTH_TWELVE);
		
		pmtctCohort.generateMonthlyRangeReport(motherDatasetDefinition.getStartDate(),
		    addMonth(motherDatasetDefinition.getStartDate(), 24));
		String MONTH_TWENTY_FOUR = "Maternal Cohort Month 24 [mm/yy]";
		buildRow(MONTH_TWENTY_FOUR);
		
		dataSet.addRow(dataASetRow);
		dataSet.addRow(getRowDescription("UPDATE THE COHORT SIZE"));
		
		dataSet.addRow(dataBSetRow);
		dataSet.addRow(dataCSetRow);
		dataSet.addRow(dataDSetRow);
		dataSet.addRow(getRowDescription("RECORD MATERNAL OUTCOME"));
		
		dataSet.addRow(dataESetRow);
		dataSet.addRow(dataFSetRow);
		dataSet.addRow(dataGSetRow);
		dataSet.addRow(getRowDescription("CALCULATE MATERNAL RETENTION AND LTF (%)"));
		
		dataSet.addRow(dataHSetRow);
		dataSet.addRow(dataISetRow);
		
		return dataSet;
	}
	
	private DataSetRow getRowDescription(String description) {
		DataSetRow row = new DataSetRow();
		row.addColumnValue(new DataSetColumn(DESCRIPTION, DESCRIPTION, String.class), description);
		return row;
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
	
	public Date addMonth(Date date, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}
	
}
