package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd;

import org.openmrs.Cohort;
import org.openmrs.module.ohrireports.api.impl.query.DSDQuery;
import org.openmrs.module.ohrireports.constants.ConceptAnswer;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Date;

@Component
@Scope("prototype")
public class HMISTXDSDEvaluator {
	
	@Autowired
	DSDQuery dsdQuery;
	
	public void buildDataset(Date start, Date end, SimpleDataSet dataSet) {
		
		dsdQuery.generateBaseReport(start, end);
		Cohort cohort;
		RowBuilder rowBuilder;
		int total = 0;
		
		//clients on 3MMD
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_3MMD);
		rowBuilder = new RowBuilder(dsdQuery.getPersonList(cohort), end);
		rowBuilder.buildDataSetRow("HIV_TX_3MMD", 1, 49, 25);
		DataSetRow headerRow = rowBuilder.buildDatasetColumn("HIV_TX_DSD",
		    "Proportion of PLHIV currently on differentiated service Delivery model (DSD)", "");
		dataSet.addRow(headerRow);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_3MMD", "Total number of clients on 3MMD",
		    String.valueOf(rowBuilder.getTotalCount())));
		total += rowBuilder.getTotalCount();
		rowBuilder.updateDataset(dataSet);
		
		// clients on ASM(6MMD)
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_6MMD);
		rowBuilder = new RowBuilder(dsdQuery.getPersonList(cohort), end);
		rowBuilder.buildDataSetRow("HIV_TX_ASM", 15, 49, 25);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_ASM", "Total number of clients on ASM(6MMD)",
		    String.valueOf(rowBuilder.getTotalCount())));
		total += rowBuilder.getTotalCount();
		rowBuilder.updateDataset(dataSet);
		
		// clients on FTAR
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_FTAR);
		rowBuilder = new RowBuilder(dsdQuery.getPersonList(cohort), end);
		rowBuilder.buildDataSetRow("HIV_TX_FTAR", 15, 49, 25);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_FTAR", "Total number of clients on FTAR",
		    String.valueOf(rowBuilder.getTotalCount())));
		total += rowBuilder.getTotalCount();
		rowBuilder.updateDataset(dataSet);
		
		// clients on CAG
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_HEP_CAG);
		rowBuilder = new RowBuilder(dsdQuery.getPersonList(cohort), end);
		rowBuilder.buildDataSetRow("HIV_TX_CAG", 15, 49, 25);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_CAG", "Total number of clients on CAG",
		    String.valueOf(rowBuilder.getTotalCount())));
		total += rowBuilder.getTotalCount();
		rowBuilder.updateDataset(dataSet);
		
		// clients on PCAD
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_PCAD);
		rowBuilder = new RowBuilder(dsdQuery.getPersonList(cohort), end);
		rowBuilder.buildDataSetRow("HIV_TX_PCAD", 15, 49, 25);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_PCAD", "Total number of clients on PCAD",
		    String.valueOf(rowBuilder.getTotalCount())));
		total += rowBuilder.getTotalCount();
		rowBuilder.updateDataset(dataSet);
		
		// clients on Adolescent DSD
		int count = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_ADOLESCENT).size();
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_Adolescent DSD.", "Total number of clients on Adolescent DSD",
		    String.valueOf(count)));
		total += count;
		
		// clients on KP DSD
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_KP);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_KP DSD", "Total number of clients on KP DSD",
		    String.valueOf(cohort.size())));
		total += cohort.size();
		
		// clients on  MCH DSD
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_MCH);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_MCH DSD", "Total number of clients on MCH DSD",
		    String.valueOf(cohort.size())));
		total += cohort.size();
		
		// clients on  other types of DSD
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_OTHER);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_ other types of DSD.",
		    "Total number of clients on other types of DSD", String.valueOf(cohort.size())));
		total += cohort.size();
		
		// clients on  Advanced HIV Disease Care Model
		cohort = dsdQuery.getCohortByDSDCategories(ConceptAnswer.DSD_AHDCM);
		rowBuilder = new RowBuilder(dsdQuery.getPersonList(cohort), end);
		rowBuilder.buildDataSetRow("HIV_TX_AHDCM", 1, 49, 25);
		dataSet.addRow(rowBuilder.buildDatasetColumn("HIV_TX_AHDCM",
		    "Total number of clients on Advanced HIV Disease Care Model", String.valueOf(rowBuilder.getTotalCount())));
		rowBuilder.updateDataset(dataSet);
		total += rowBuilder.getTotalCount();
		
		headerRow.addColumnValue(new DataSetColumn("Number", "Number", String.class), getPercentage(total));
		
	}
	
	private String getPercentage(int total) {
		if (dsdQuery.getBaseCohort().isEmpty())
			return "0";
		double value = (double) (total * 100) / dsdQuery.getBaseCohort().size();
		DecimalFormat decimalFormat = new DecimalFormat("###.##");
		String output = String.valueOf(Double.parseDouble(decimalFormat.format(value)));
		return output + "%";
	}
}
