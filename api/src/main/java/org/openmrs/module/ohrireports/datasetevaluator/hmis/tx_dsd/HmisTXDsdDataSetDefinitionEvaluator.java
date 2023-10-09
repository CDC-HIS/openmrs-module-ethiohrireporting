package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.tx_dsd.HmisTXDsdDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;

import org.openmrs.module.reporting.dataset.MapDataSet;

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HmisTXDsdDataSetDefinition.class })
public class HmisTXDsdDataSetDefinitionEvaluator implements DataSetEvaluator {

    private EvaluationContext context;

    private HmisTXDsdDataSetDefinition hdsd;
  
    @Autowired
    private ConceptService conceptService;

    @Autowired
    private EvaluationService evaluationService;
    private int minCount = 0;
    private int maxCount = 4;
    List<Obs> obses = new ArrayList<>();

    @Override
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
            throws EvaluationException {

        hdsd = (HmisTXDsdDataSetDefinition) dataSetDefinition;
        context = evalContext;
        
        MapDataSet data = new MapDataSet(dataSetDefinition, evalContext);
        data.addData(new DataSetColumn("HIV_TX_DSD","Proportion of PLHIV currently on differentiated service Delivery model (DSD)",String.class)
		," ");
        obses = getDSDModel(DSD_3MMD);
        data.addData(new DataSetColumn("HIV_TX_3MMD","Total number of clients on 3MMD",Integer.class)
		,obses.size());
        buildDataSet(data,1,50,"HIV_TX_3MMD");

        obses = getDSDModel(DSD_6MMD);
        data.addData(new DataSetColumn("HIV_TX_ASM","Total number of clients on ASM(6MMD)",Integer.class)
		,obses.size());
        buildDataSet(data,15,50,"HIV_TX_ASM");

        obses = getDSDModel(DSD_FTAR);
        data.addData(new DataSetColumn("HIV_TX_FTAR","Total number of clients on FTAR",Integer.class)
		,obses.size());
        buildDataSet(data,15,50,"HIV_TX_FTAR");

        obses = getDSDModel(DSD_HEP_CAG);
        data.addData(new DataSetColumn("HIV_TX_CAG","Total number of clients on CAG",Integer.class)
		,obses.size());
        buildDataSet(data,15,50,"HIV_TX_CAG");

        obses = getDSDModel(DSD_PCAD);
        data.addData(new DataSetColumn("HIV_TX_PCAD","Total number of clients on PCAD",Integer.class)
		,obses.size());
        buildDataSet(data,15,50,"HIV_TX_PCAD");

        obses = getDSDModel(DSD_ADOLESCENT);
        data.addData(new DataSetColumn("HIV_TX_Adolescent DSD","Total number of clients on Adolescent DSD",Integer.class)
		,obses.size());

        obses = getDSDModel(DSD_KP);
        data.addData(new DataSetColumn("HIV_TX_KP DSD","Total number of clients on KP DSD",Integer.class)
		,obses.size());

        obses = getDSDModel(DSD_MCH);
        data.addData(new DataSetColumn("HIV_TX_MCH DSD","Total number of clients on MCH DSD",Integer.class)
		,obses.size());

        obses = getDSDModel(DSD_OTHER);
        data.addData(new DataSetColumn("HIV_TX_ other types of DSD.","Total number of clients on other types of DSD",Integer.class)
		,obses.size());

        obses = getDSDModel(DSD_AHDCM);
        data.addData(new DataSetColumn("HIV_TX_AHDCM","Total number of clients on \"Advanced HIV Disease Care Model\"",Integer.class)
		,obses.size());
        buildDataSet(data,1,50,"HIV_TX_AHDCM");
        
        
        return data;
    }

    private void buildDataSet(MapDataSet data, Integer min_age, Integer max_age, String name) {
        Integer counter = 0;
        if (min_age==1){
            counter+=1;
            data.addData(new DataSetColumn(name+"."+counter,"< 1 year, Male",Integer.class),getdsdByAgeAndGender(0,min_age,Gender.Male));
            counter+=1;
            data.addData(new DataSetColumn(name+"."+counter,"< 1 year, Female",Integer.class),getdsdByAgeAndGender(0,min_age,Gender.Female));
            getdsdByAgeAndGender(0,min_age,Gender.Female);
        minCount = 1;
        maxCount = 4;
        }
        else{
            minCount = 15;
            maxCount = 19;
        }
        while (minCount <= max_age) {
            counter+=1;
            if (minCount == max_age) {
                data.addData(new DataSetColumn(name+"."+counter," >= "+max_age+" years, Male",Integer.class),getdsdByAgeAndGender(minCount,150,Gender.Male));
                counter+=1;
                data.addData(new DataSetColumn(name+"."+counter," >= "+max_age+" years, Female",Integer.class),getdsdByAgeAndGender(minCount,150,Gender.Female));
			
                
            } else {
                if(minCount==25){
                    maxCount=49;
                }
                data.addData(new DataSetColumn(name+"."+counter,minCount+" - "+maxCount+" years, Male",Integer.class),getdsdByAgeAndGender(minCount,maxCount,Gender.Male));
                counter+=1;
                data.addData(new DataSetColumn(name+"."+counter,minCount+" - "+maxCount+" years, Female",Integer.class),getdsdByAgeAndGender(minCount,maxCount,Gender.Female));
                

            }

            minCount=1+maxCount;
            maxCount = minCount + 4;
        }
    }
    
    private Integer getdsdByAgeAndGender(int minAge, int maxAge, Gender gender) {
        int _age = 0;
		List<Integer> patients = new ArrayList<>();
		String _gender=gender.equals(Gender.Female)?"f":"m";
		if (maxAge > 1){
				maxAge=maxAge+1;
			}
		for (Obs obs :obses) {
			_age =obs.getPerson().getAge();
			if (!patients.contains(obs.getPersonId()) 
			 && (_age>=minAge && _age< maxAge)
			 && (obs.getPerson().getGender().toLowerCase().equals(_gender))) {
				
				patients.add(obs.getPersonId());

			}
		}
		return patients.size();
    }

 
    private List<Obs> getDSDModel(String dsd_type){
        List<Integer> tx_curr = LoadObs();
        List<Obs> patient_dsd = new ArrayList<>();
        if (tx_curr == null || tx_curr.size() == 0)
			return patient_dsd;
        HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
                .and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(DSD_CATEGORY))
				.and()
                .whereIdIn("obs.personId", tx_curr)
                .and()
				.whereEqual("obs.valueCoded", conceptService.getConceptByUuid(dsd_type))
				.and().whereLess("obs.obsDatetime", hdsd.getEndDate());
		queryBuilder.orderDesc("obs.personId,obs.obsDatetime");
        
        return patient_dsd;

    }
    private List<Integer> LoadObs() {

		List<Integer> patientsId = getListOfALiveORRestartPatientObservertions();
		List<Integer> patients = new ArrayList<>();
		if (patientsId == null || patientsId.size() == 0)
			return patients;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs");
		queryBuilder.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())

                .and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(TREATMENT_END_DATE))
				.and()
				.whereGreater("obs.valueDatetime", hdsd.getEndDate())
				.and()
				.whereLess("obs.obsDatetime", hdsd.getEndDate())
				.whereIdIn("obs.personId", patientsId)
				.orderDesc("obs.personId,obs.obsDatetime");
				for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
					if(!patients.contains(obs.getPersonId()))
					  {
						patients.add(obs.getPersonId());
						
					  }
				}
		
		return patients;
	}
    private List<Integer> getListOfALiveORRestartPatientObservertions() {

		List<Integer> uniqiObs = new ArrayList<>();
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
                .and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(FOLLOW_UP_STATUS))
				.and()
				.whereIn("obs.valueCoded", Arrays.asList(conceptService.getConceptByUuid(ALIVE),
						conceptService.getConceptByUuid(RESTART)))
				.and().whereLess("obs.obsDatetime", hdsd.getEndDate());
		queryBuilder.orderDesc("obs.personId,obs.obsDatetime");

		List<Obs> aliveObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

		for (Obs obs : aliveObs) {
			if (!uniqiObs.contains(obs.getPersonId())) {
				uniqiObs.add(obs.getPersonId());
			}
		}

		return uniqiObs;
	}



}

enum Gender {
	Female,
	Male
}