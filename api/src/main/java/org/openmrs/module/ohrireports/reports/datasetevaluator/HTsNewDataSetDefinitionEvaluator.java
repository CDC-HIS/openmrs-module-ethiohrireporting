package org.openmrs.module.ohrireports.reports.datasetevaluator;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.HtsNewDataSetDefinition;
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

@Handler(supports = { HtsNewDataSetDefinition.class })
public class HTsNewDataSetDefinitionEvaluator implements DataSetEvaluator {
	
	@Autowired
	EvaluationService evaluationService;
	
	@Autowired
	ConceptService conceptService;
	
	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext) throws EvaluationException {
		
		HtsNewDataSetDefinition hdsd = (HtsNewDataSetDefinition) dataSetDefinition;
		
		SimpleDataSet data = new SimpleDataSet(dataSetDefinition, evalContext);
	
		
		List<Obs> obses = getObservations(hdsd, evalContext);
		
		DataSetRow row = null;
     
        List<Person> managObses = new ArrayList<>();
		for (Obs obs : obses) {

            if(!managObses.contains(obs.getPerson())){
                row = new DataSetRow();
                row.addColumnValue(new DataSetColumn("PersonID", "#", Integer.class), obs.getPersonId());
                row.addColumnValue(new DataSetColumn("Name", "Name", String.class), obs.getPerson().getNames());
                row.addColumnValue(new DataSetColumn("Age", "Age", Integer.class), obs.getPerson().getAge());
                row.addColumnValue(new DataSetColumn("Gender", "Gender", Integer.class), obs.getPerson().getGender());
                row.addColumnValue(new DataSetColumn("ArtStartDate", "Art StartDate", Date.class), obs.getValueDate());
                row.addColumnValue(new DataSetColumn("Encounter", "Encounter Type", String.class), obs.getEncounter().getEncounterType().getName());
				row.addColumnValue(new DataSetColumn("Regimen","Regmin",String.class), getRegmin(obs,evalContext));
                managObses.add(obs.getPerson());
                
                data.addRow(row);
            }
		
		}
		return data;
	}
	
	private List<Obs> getObservations(HtsNewDataSetDefinition hdsd,EvaluationContext context) {
      
        List<Obs> obses =new ArrayList<>();
       List<Integer> personIds = getPatientsWithARTStartedDate(hdsd, context);
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obv")
        .from(Obs.class,"obv")
		.whereInAny("obv.concept",conceptService.getConceptByUuid(ART_START_DATE))
		.whereGreaterOrEqualTo("obv.valueDatetime", hdsd.getStartDate())
		.and()
		.whereEqual("obv.encounter.encounterType", hdsd.getEncounterType())
		.and()
		.whereLessOrEqualTo("obv.valueDatetime", hdsd.getEndDate())
		.and()
		.whereIn("obv.personId", personIds)
		.orderDesc("obv.personId,obv.obsDatetime");
        for (Object[] row : evaluationService.evaluateToList(queryBuilder, context)) {
			obses.add((Obs)row[0]);
		}

        return obses;
    }
	
	private List<Integer> getPatientsWithARTStartedDate(HtsNewDataSetDefinition hdsd, EvaluationContext context){
		List<Integer> uniqPatientsId = new ArrayList<>();

		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
      
        queryBuilder.select("obv.personId")
        .from(Obs.class,"obv")  
		.and()
		.whereEqual("obv.concept", conceptService.getConceptByUuid(REASON_FOR_ART_ELIGABLITY)).and()
		.whereNotInAny("obv.valueCoded",Arrays.asList(conceptService.getConceptByUuid(TRANSFERE_IN)))
		.and()
		.whereEqual("obv.encounter.encounterType", hdsd.getEncounterType()).and()
		  	
        .orderDesc("obv.personId,obv.obsDatetime") ;
		List<Integer> personIds = evaluationService.evaluateToList(queryBuilder, Integer.class, context);
		for (Integer personId : personIds) {
			if(!uniqPatientsId.contains(personId))
				uniqPatientsId.add(personId);
		}
		return uniqPatientsId;
	}
	
	private String getRegmin(Obs obs, EvaluationContext context) {
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		
		queryBuilder.select("obv.valueCoded").from(Obs.class, "obv")
		        .whereInAny("obv.concept", conceptService.getConceptByUuid(REGIMEN))
		        .whereEqual("obv.encounter", obs.getEncounter()).and().whereEqual("obv.person", obs.getPerson())
		        .orderDesc("obv.obsDatetime").limit(1);
		List<Concept> concepts = evaluationService.evaluateToList(queryBuilder, Concept.class, context);
		
		Concept data = null;
		if (concepts != null && concepts.size() > 0)
			data = concepts.get(0);
		
		return data == null ? "" : data.getName().getName();
	}
	
}
