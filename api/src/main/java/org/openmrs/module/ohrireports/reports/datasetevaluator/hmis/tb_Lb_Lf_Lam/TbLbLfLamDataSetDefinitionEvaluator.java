package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.tb_Lb_Lf_Lam;
import java.util.ArrayList;
import java.util.List;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.tb_Lb_Lf_Lam.TbLbLfLamDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.MapDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TbLbLfLamDataSetDefinition.class })
public class TbLbLfLamDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TbLbLfLamDataSetDefinition hdsd;
		

	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TbLbLfLamDataSetDefinition) dataSetDefinition;
		context = evalContext;
	
		MapDataSet data = new MapDataSet(dataSetDefinition, context);
				
        data.addData(new DataSetColumn("TB_LB_LF-LAM","Total Number of tests performed using Lateral Flow Urine Lipoarabinomannan (LF-LAM) assay",String.class)
		," ");
        data.addData(new DataSetColumn("TB_LB_LF-LAM. 1","Positive",Integer.class)
		,0);
		data.addData(new DataSetColumn("TB_LB_LF-LAM. 2","Negative",Integer.class)
		,0);
	
		return data;
	}
	
	
}
enum Gender {
	Female,
	Male
}