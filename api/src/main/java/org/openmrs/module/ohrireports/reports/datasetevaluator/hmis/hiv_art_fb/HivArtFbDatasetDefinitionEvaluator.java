package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.hiv_art_fb;

import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_art_fb.HivArtFbDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

@Handler(supports={HivArtFbDatasetDefinition.class})
public class HivArtFbDatasetDefinitionEvaluator  implements DataSetEvaluator{

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }    
}
