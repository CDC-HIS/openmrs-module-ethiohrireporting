package org.openmrs.module.ohrireports.datasetevaluator.datim.pmtct.hei;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.pmtct.EIDQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pmtct.HeiTotalDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
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

import static org.openmrs.module.ohrireports.constants.PMTCTConceptQuestions.PMTCT_DATE_OF_SAMPLE_RECEIVED_BY_LAB;

@Handler(supports = { HeiTotalDatasetDefinition.class })
public class HeiTotalDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	private EIDQuery eidQuery;
	
	@Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
        HeiTotalDatasetDefinition dsd = (HeiTotalDatasetDefinition) dataSetDefinition;
        SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);

        SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(dsd.getStartDate(),
                dsd.getEndDate(), data);
        if (_dataSet != null) return _dataSet;

        if (!dsd.getHeader()) {
            AtomicInteger count = new AtomicInteger(0);
            eidQuery.generateReport(dsd.getStartDate(), dsd.getEndDate(), PMTCT_DATE_OF_SAMPLE_RECEIVED_BY_LAB);
            eidQuery.getPatientEncounterHashMap().forEach((k, p) -> {
                p.getEncounterList().forEach(e -> {
                    if (Objects.nonNull(e.getDnaPcrResult())) {
                        count.getAndIncrement();
                    }
                });
            });
            DataSetRow headerRow = new DataSetRow();
            headerRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", String.class), count.intValue());

            data.addRow(headerRow);
        }
        return data;
    }
}
