package org.openmrs.module.ohrireports.datasetevaluator.linelist.pmtct;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.datasetevaluator.linelist.LineListUtilities;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.ohrireports.datasetdefinition.linelist.HeiDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Handler(supports = { HeiDatasetDefinition.class })
public class HeiDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EidLineListQuery eidQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HeiDatasetDefinition dsd = (HeiDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		List list = eidQuery.getHeiListObjects(dsd.getStartDate(), dsd.getEndDate());
		DataSetRow row = new DataSetRow();
		int rowNumber = 0;
		if (list.isEmpty()) {
			dataSet.addRow(LineListUtilities.buildEmptyRow(Arrays.asList("#", "Full Name", "Age", "Age at Enrollment",
			    "Infant Referred", "Date of Enrollment", "Infant ARV Prophylaxis", "Immunization",
			    "Mother's PMTCT Intervention", "Follow-up Date", "Weight on follow-up", "Growth Pattern",
			    "Reason for Growth Failure", "Development Milestone", "Infant Feeding Practice",
			    "Mother's Breast Condition", "Rapid Anti-body Test", "DNA PCR Sample Collection Date", "CPT Dose",
			    "Conclusion", "decision", "HEI PMTCT Final Outcome"), "#", "Full Name"));
			return dataSet;
		}
		for (Object objects : list) {
			Object[] object = (Object[]) objects;
			row.addColumnValue(new DataSetColumn("#", "#", String.class), rowNumber);
			row.addColumnValue(new DataSetColumn("Full Name", "Full Name", String.class), object[2]);
			row.addColumnValue(new DataSetColumn("Age", "Age", String.class), object[3]);
			row.addColumnValue(new DataSetColumn("Age at Enrollment", "Age at Enrollment", String.class),
			    getAgeByEnrollmentDate(object[5], object[8]));
			row.addColumnValue(new DataSetColumn("Birth Weight", "Birth Weight", String.class), object[13]);
			row.addColumnValue(new DataSetColumn("MRN", "MRN", String.class), object[1]);
			row.addColumnValue(new DataSetColumn("Hei Code", "Hei Code", String.class), object[6]);
			row.addColumnValue(new DataSetColumn("Infant Referred?", "Infant Referred?", String.class), object[7]);
			row.addColumnValue(new DataSetColumn("Date of Enrollment", "Date of Enrollment", String.class), object[8]);
			row.addColumnValue(new DataSetColumn("Infant ARV Prophylaxis", "Infant ARV Prophylaxis", String.class),
			    object[9]);
			row.addColumnValue(new DataSetColumn("Immunization", "Immunization", String.class), object[10]);
			row.addColumnValue(
			    new DataSetColumn("Mother's PMTCT Intervention", "Mother's PMTCT Intervention", String.class), object[11]);
			row.addColumnValue(new DataSetColumn("Follow-up Date", "Follow-up Date", String.class), object[12]);
			row.addColumnValue(new DataSetColumn("Weight on follow-up", "Weight on follow-up ", String.class), object[14]);
			row.addColumnValue(new DataSetColumn("Growth Pattern", "Growth Pattern", String.class), object[15]);
			row.addColumnValue(new DataSetColumn("Reason for Growth Failure", "Reason for Growth Failure", String.class),
			    object[16]);
			row.addColumnValue(new DataSetColumn("Development Milestone", "Development Milestone", String.class), object[17]);
			row.addColumnValue(new DataSetColumn("Reason for Red Flag", "Reason for Red Flag", String.class), object[18]);
			row.addColumnValue(new DataSetColumn("Infant Feeding Practice", "Infant Feeding Practice", String.class),
			    object[19]);
			row.addColumnValue(new DataSetColumn("Mother's Breast Condition", "Mother's Breast Condition", String.class),
			    object[20]);
			row.addColumnValue(new DataSetColumn("Rapid Anti-body Test", "Rapid Anti-body Test", String.class), object[21]);
			row.addColumnValue(new DataSetColumn("DNA PCR Sample Collection Date", "DNA PCR Sample Collection Date",
			        String.class), object[23]);
			row.addColumnValue(new DataSetColumn("DNA PCR Result", "DNA PCR Result", String.class), object[22]);
			row.addColumnValue(new DataSetColumn("CPT Dose", "CPT Dose", String.class), object[28]);
			row.addColumnValue(new DataSetColumn("Conclusion", "Conclusion", String.class), object[26]);
			row.addColumnValue(new DataSetColumn("decision", "decision", String.class), object[27]);
			row.addColumnValue(new DataSetColumn("HEI PMTCT Final Outcome", "HEI PMTCT Final Outcome", String.class),
			    object[25]);
			row.addColumnValue(new DataSetColumn("Date of Final Outcome", "Date of Final Outcome", String.class), object[24]);
			dataSet.addRow(row);
		}
		return dataSet;
	}
	
	private String getAgeByEnrollmentDate(Object dateOfBirth, Object enrollmentDate) {
		if (Objects.isNull(dateOfBirth)) {
			return "";
		}
		Date birthDate = (Date) dateOfBirth;
		if (Objects.isNull(enrollmentDate)) {
			return birthDate.toString();
		}
		Date enrDate = (Date) enrollmentDate;
		return String.valueOf(EthiOhriUtil.getAgeInMonth(birthDate, enrDate));
		
	}
	
}
