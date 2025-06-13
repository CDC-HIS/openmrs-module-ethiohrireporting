package org.openmrs.module.ohrireports.datasetevaluator.datim.prep_ct;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.api.impl.query.PreExposureProphylaxisQuery;
import org.openmrs.module.ohrireports.datasetdefinition.datim.pr_ep_ct.AutoCalculatePrEPCTDatasetDefinition;
import org.openmrs.module.ohrireports.helper.EthiOhriUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { AutoCalculatePrEPCTDatasetDefinition.class })
public class AutoCalculatePrEPCTDatasetDefinitionEvaluator implements DataSetEvaluator {
	
	private Concept tdfConcept, tdf_ftcConcept, tdf3tcConcept, prEpStatedConcept;
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	
	private EvaluationContext context;
	
	@Autowired
	private PreExposureProphylaxisQuery preExposureProphylaxisQuery;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		AutoCalculatePrEPCTDatasetDefinition aucDataset = (AutoCalculatePrEPCTDatasetDefinition) dataSetDefinition;
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		SimpleDataSet _dataSet = EthiOhriUtil.isValidReportDateRange(aucDataset.getStartDate(), aucDataset.getEndDate(),
		    dataSet);
		if (_dataSet != null)
			return _dataSet;
		
		if (!aucDataset.getHeader()) {
			
			preExposureProphylaxisQuery.setStartDate(aucDataset.getStartDate());
			preExposureProphylaxisQuery.setEndDate(aucDataset.getEndDate());
			
			DataSetRow dRow = new DataSetRow();
			Cohort cohort = preExposureProphylaxisQuery.getAllPrEPCT();
			
			dRow.addColumnValue(new DataSetColumn("Numerator", "Numerator", Integer.class), cohort.size());
			dataSet.addRow(dRow);
		}
		return dataSet;
	}
	
}
