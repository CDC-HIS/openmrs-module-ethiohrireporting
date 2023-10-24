package org.openmrs.module.ohrireports.datasetevaluator.datim.tx_rtt;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tx_rtt.TxRttIITDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
@Handler(supports = { TxRttIITDataSetDefinition.class })
public class TxRttIITDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	private EvaluationContext context;
	
	private TxRttIITDataSetDefinition hdsd;
		
	
	@Autowired
	private ConceptService conceptService;
	
	@Autowired
	private EvaluationService evaluationService;
	private int minCount = 0;
    private int maxCount = 4;
	Date prevThreeMonth=new Date();
	Date prevFiveMonth=new Date();
    Date prevSixMonth=new Date();
    List<Obs> obses = new ArrayList<>();
    HashMap<Integer, Date> patientTreatmentEndDate = new HashMap<>();
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		hdsd = (TxRttIITDataSetDefinition) dataSetDefinition;
		context = evalContext;
        patientTreatmentEndDate = new HashMap<>();
        obses = getDatimTxRttTreatmentRestarted();
        SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
        
        DataSetRow belowThreeDateSet = new DataSetRow();
        belowThreeDateSet.addColumnValue(new DataSetColumn("aggByIITrange", "",
                String.class), "Experienced treatment interruption of <3 months before returning to treatment ");
        belowThreeDateSet.addColumnValue(new DataSetColumn("countedIIT", "", Integer.class),
        disaggregateByARTstartDate("three"));
        set.addRow(belowThreeDateSet);

        
        DataSetRow betweenThreeandFiveDateSet = new DataSetRow();
        betweenThreeandFiveDateSet.addColumnValue(new DataSetColumn("aggByIITrange", "",
                String.class), "Experienced treatment interruption of 3-5 months before returning to treatment");
                betweenThreeandFiveDateSet.addColumnValue(new DataSetColumn("countedIIT", "", Integer.class),
        disaggregateByARTstartDate("five"));
        set.addRow(betweenThreeandFiveDateSet);

   
        DataSetRow abovesixDateSet = new DataSetRow();
        abovesixDateSet.addColumnValue(new DataSetColumn("aggByIITrange", "",
                String.class), "Experienced treatment interruption of 6+ months before returning to treatment");
        abovesixDateSet.addColumnValue(new DataSetColumn("countedIIT", "", Integer.class),
        disaggregateByARTstartDate("six"));
        set.addRow(abovesixDateSet);
   
        return set;
	}


    private Integer disaggregateByARTstartDate(String range){
        List<Obs> patientARTstartedDate = obses;
        List<Integer> aggr = new ArrayList<>();
        if (patientARTstartedDate == null || patientARTstartedDate.size() == 0)
                return aggr.size();
        for (Obs obs : patientARTstartedDate){
            Date treatEnd=patientTreatmentEndDate.get(obs.getPersonId());
            Date restartedDate = obs.getObsDatetime();
            if (treatEnd!=null && restartedDate!=null){
                Calendar subThreeMonth = Calendar.getInstance();
                subThreeMonth.setTime(restartedDate);
                subThreeMonth.add(Calendar.MONTH, -3);
                prevThreeMonth = subThreeMonth.getTime();
                if (range==("five")){
                    Calendar subFiveMonth = Calendar.getInstance();
                    subFiveMonth.setTime(restartedDate);
                    subFiveMonth.add(Calendar.MONTH, -5);
                    prevFiveMonth = subFiveMonth.getTime();         
                    
                    if(treatEnd.before(prevThreeMonth) && treatEnd.after(prevFiveMonth)){
                        aggr.add(obs.getPersonId());
                    } 
                }
                if(range=="three"){
                    if(treatEnd.after(prevThreeMonth)){
                        aggr.add(obs.getPersonId());
                    } 
                }
                if (range=="six"){ 
                    Calendar subSixMonth = Calendar.getInstance();
                    subSixMonth.setTime(restartedDate);
                    subSixMonth.add(Calendar.MONTH, -6);
                    prevSixMonth = subSixMonth.getTime();
                    if(treatEnd.before(prevSixMonth)){
                        aggr.add(obs.getPersonId());
                    } 
                }
        }
        }
        return aggr.size();
    }
	

    private List<Obs> getDatimTxRttTreatmentRestarted(){
		List<Integer> prevPatientsTreatmentEnd = getDatimTxrttPrevTreatmentEndDate();
		List<Obs> patients = new ArrayList<>();
        List<Integer> p = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs");
        queryBuilder.from(Obs.class, "obs")
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and()
        .whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
        .and()
        .whereGreater("obs.obsDatetime", hdsd.getStartDate())
        .and().whereLessOrEqualTo("obs.obsDatetime", hdsd.getEndDate()).and()
		.whereIdIn("obs.personId", prevPatientsTreatmentEnd)     
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
            
                if(!p.contains(obs.getPersonId()))
                        {
                        p.add(obs.getPersonId());
                        patients.add(obs);
                        }
        }			
		return patients;
	}	
	
	private List<Integer> getDatimTxrttPrevTreatmentEndDate() {
		List<Integer> patients = new ArrayList<>();
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
        queryBuilder.select("obs");
        queryBuilder.from(Obs.class, "obs")
        .whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
        .and()
        .whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
        .and()
        .whereLess("obs.valueDatetime", hdsd.getStartDate()).and().whereLess("obs.obsDatetime", hdsd.getStartDate())
        .and()        
        .orderDesc("obs.personId,obs.obsDatetime");
        for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
                if(!patients.contains(obs.getPersonId()))
                        {
                        patients.add(obs.getPersonId());
                        patientTreatmentEndDate.put(obs.getPersonId(),obs.getValueDatetime());
                        }
        }			
		return patients;
	}

}
