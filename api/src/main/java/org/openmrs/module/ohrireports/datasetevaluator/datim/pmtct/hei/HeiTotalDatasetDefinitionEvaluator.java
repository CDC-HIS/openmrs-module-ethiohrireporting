package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.HeiTotalDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.PMTCT_DATE_OF_SAMPLE_RECEIVED_BY_LAB;

@Handler(supports = { HeiTotalDatasetDefinition.class })
public class HeiTotalDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EIDQuery eidQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		HeiTotalDatasetDefinition dsd = (HeiTotalDatasetDefinition) dataSetDefinition;
		AtomicInteger count = new AtomicInteger(0);
		eidQuery.generateReport(dsd.getStartDate(), dsd.getEndDate(), PMTCT_DATE_OF_SAMPLE_RECEIVED_BY_LAB);
		eidQuery.getPatientEncounterHashMap().forEach((k, p) -> {
			p.getEncounterList().forEach(e -> {
				if (Objects.nonNull(e.getDnaPcrResult())) {
					count.getAndIncrement();
				}
			});
		});
		SimpleDataSet data   = new SimpleDataSet(dataSetDefinition,evalContext);
		DataSetRow headerRow = new DataSetRow();
		headerRow.addColumnValue(new DataSetColumn("sum"," ",String.class),"Sum result ");
		headerRow.addColumnValue(new DataSetColumn("count"," ",Integer.class),count.intValue());
		
		data.addRow(headerRow);
		return data;
	}
}
