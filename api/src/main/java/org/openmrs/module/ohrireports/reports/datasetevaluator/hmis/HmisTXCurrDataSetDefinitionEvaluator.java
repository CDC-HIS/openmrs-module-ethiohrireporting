package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.HmisTXCurrDataSetDefinition;
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

@Handler(supports = { HmisTXCurrDataSetDefinition.class })
public class HmisTXCurrDataSetDefinitionEvaluator implements DataSetEvaluator {

	@Autowired
	EvaluationService evaluationService;

	@Autowired
	ConceptService conceptService;

	HashMap<Integer, Concept> patientStatus = new HashMap<>();
	List<Obs> obses = new ArrayList<>();
	List<Obs> sub_obses = new ArrayList<>();

	private HmisTXCurrDataSetDefinition hdsd;
	private EvaluationContext context;

	List<String> children_first_line = Arrays.asList(R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV, R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4h_OTHER_CHILD_1ST_LINE_REGIMEN, R4i_TDF_3TC_DTG, R4j_ABC_3TC_DTG, R4L_ABC_3TC_EFV);
	List<String> children_second_line = Arrays.asList(R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV, R5d_TDF_DDL_NFV, R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV, R5i_TDF_3TC_LPVr, R5j_OTHER_CHILD_2ND_LINE_REGIMEN, R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC);
	List<String> children_third_line = Arrays.asList(R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC, R6e_OTHER_CHILD_3RD_LINE_REGIMEN, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);

	List<String> adult_first_line = Arrays.asList(R1a30_D4T_30_3TC_NVP, R1a40_D4T_40_3TC_NVP, R1b30_D4T_30_3TC_EFV, R1b40_D4T_40_3TC_EFV, R1c_AZT_3TC_NVP, R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1f_TDF_3TC_NVP, R1g_ABC_3TC_EFV, R1h_ABC_3TC_NVP, R1j_TDF_3TC_DTG, R1i_OTHER_ADULT_1ST_LINE_REGIMEN, R1k_AZT_3TC_DTG);
	List<String> adult_second_line = Arrays.asList(R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV, R2c_TDF_DDL_LPV, R2d_TDF_DDL_NFV, R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr, R2i_ABC_3TC_LPVr, R2j_TDF_3TC_DTG, R2L_OTHER_ADULT_2ND_LINE_REGIMEN);
	List<String> adult_third_line = Arrays.asList(R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG, R3d_OTHER_ADULT_3RD_LINE_REGIMEN, R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV);

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		hdsd = (HmisTXCurrDataSetDefinition) dataSetDefinition;
		context = evalContext;
		
		MapDataSet data = new MapDataSet(dataSetDefinition, evalContext);
		
		data.addData(new DataSetColumn("HIV_HIV_Treatement.","Does health facility provide Monthly PMTCT / ART Treatment Service?",Integer.class)
		," ");
		obses = getTxCurrPatients();
		data.addData(new DataSetColumn("HIV_TX_CURR_ALL","Number of adults and children who are currently on ART by age, sex and regimen category",Integer.class)
		,obses.size());
		data.addData(new DataSetColumn("HIV_TX_CURR_U15","Number of children (<15) who are currently on ART",Integer.class) ,getPersonByAge(0,15).size());
		
		data.addData(new DataSetColumn("HIV_TX_CURR_U1","Children currently on ART aged <1 yr",Integer.class),getPersonByAge(0, 1).size());
	
		buildDataSet("HIV_TX_CURR_U1",data,0,1,"Children");

		Integer minCount = 1;
        Integer maxCount = 4;
        while (minCount <= 50) {
			String category = "Children";
			if (maxCount > 15){
				category = "Adult";
				if(maxCount==19){
					data.addData(new DataSetColumn("HIV_TX_CURR_ADULT","Number of adults who are currently on ART (>=15)",Integer.class) ,getPersonByAge(15,150).size());
				}
			}
            if (minCount == 50) {
				data.addData(new DataSetColumn("HIV_TX_CURR_U50","Adult currently on ART aged 50+",Integer.class),getPersonByAge(minCount, maxCount).size());
				buildDataSet("HIV_TX_CURR_U50", data, minCount, maxCount, category);            
            } 
			else {
				data.addData(new DataSetColumn("HIV_TX_CURR_U"+maxCount,category+" currently on ART aged "+minCount+"-"+maxCount+" yr",Integer.class),getPersonByAge(minCount, maxCount).size());
				buildDataSet("HIV_TX_CURR_U"+maxCount,data,minCount,maxCount, category);
            }

            minCount=1+maxCount;
            maxCount = minCount + 4;
        }

		data.addData(new DataSetColumn("HIV_TX_CURR_PREG","Currently on ART by pregnancy status",Integer.class),getPersonByAgeandSex(0, 1,Gender.Female).size());
		data.addData(new DataSetColumn("HIV_TX_CURR_PREG.1","Pregnant",Integer.class),getByPregnancyStatus(getPersonByAgeandSex(0, 1,Gender.Female), conceptService.getConceptByUuid(YES)).size());
		data.addData(new DataSetColumn("HIV_TX_CURR_PREG.2","Non pregnant",Integer.class),getByPregnancyStatus(getPersonByAgeandSex(0, 1,Gender.Female), conceptService.getConceptByUuid(NO)).size());

		return data;

	}

	private List<List<String>> getListofLevels(Integer age){
		
		List<List<String>> listOfLevels = new ArrayList<List<String>>();
		listOfLevels.add(children_first_line);
		listOfLevels.add(children_second_line);
		listOfLevels.add(children_third_line);
		if (age > 15){
			listOfLevels = new ArrayList<List<String>>();
			listOfLevels.add(adult_first_line);
			listOfLevels.add(adult_second_line);
			listOfLevels.add(adult_third_line);

		}
		return listOfLevels;
		
	}

	private void buildDataSet(String name, MapDataSet data, int minAge, int maxAge, String category) {
		List<List<String>> listOfLevels = new ArrayList<List<String>>();
		listOfLevels=getListofLevels(maxAge);
		int i=0;
		for (List<String> level : listOfLevels) {
			i=i+1;
			sub_obses=getByRegimen(level,getPersonByAge(minAge, maxAge));
			String cname=name+"."+i;
			if (maxAge == 1){		
				data.addData(new DataSetColumn(cname,"Children currently on ART aged <1 yr on "+getLevel(i)+"-line regimen by sex",Integer.class),sub_obses.size());
			}
			else{
				if(maxAge > 50){
					data.addData(new DataSetColumn(cname,category+" currently on ART aged 50+ on "+getLevel(i)+"-line regimen by sex",Integer.class),sub_obses.size());
				}
				else{
				data.addData(new DataSetColumn(cname,category+" currently on ART aged "+minAge+"-"+maxAge+" yr on "+getLevel(i)+"-line regimen by sex",Integer.class),sub_obses.size());
				}
			}
			
			data.addData(new DataSetColumn(cname+".1","Male",Integer.class)
			,getPersonByAgeandSex(minAge,maxAge,Gender.Male).size());

			data.addData(new DataSetColumn(cname+".2","Female",Integer.class)
			,getPersonByAgeandSex(minAge,maxAge,Gender.Female).size());
		}
           
    }
	private String getLevel(int i){
		String lev= "First";
		if (i==2){
			lev = "Second";
		}
		else if (i==3){
			lev = "Third";
		}
		return lev;
	}
	private List<Integer> getPersonByAge(int minAge, int maxAge){
		int _age = 0;
		List<Integer> patients = new ArrayList<>();
		if (maxAge > 1){
				maxAge=maxAge+1;
			}

		for (Obs obs :obses) {
			_age =obs.getPerson().getAge();
			if (!patients.contains(obs.getPersonId()) 
			 && (_age>=minAge && _age< maxAge)) {
				
				patients.add(obs.getPersonId());

			}
		}
		return patients;
	}


	private List<Integer> getPersonByAgeandSex(int minAge, int maxAge, Gender gender){
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
		return patients;
	}

	
	private List<Obs> getTxCurrPatients() {

		List<Integer> patientsId = getListOfALiveORRestartPatient();
		List<Integer> patients = new ArrayList<>();
		List<Obs> localObs = new ArrayList<>();
		if (patientsId == null || patientsId.size() == 0)
			return localObs;
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
			.whereIdIn("obs.personId", patientsId).and()
			.orderDesc("obs.personId,obs.obsDatetime");
				for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
					if(!patients.contains(obs.getPersonId()))
					  {
						patients.add(obs.getPersonId());
						localObs.add(obs);
					  }
				}
		
		return localObs;
	}

	
	private List<Integer> getListOfALiveORRestartPatient() {

			List<Integer> uniqiObs = new ArrayList<>();
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();

		queryBuilder.select("obs")
				.from(Obs.class, "obs")
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and()
				.whereEqual("obs.concept", conceptService.getConceptByUuid(FOLLOW_UP_STATUS))
				.and()
				.whereIn("obs.valueCoded", Arrays.asList(conceptService.getConceptByUuid(ALIVE),conceptService.getConceptByUuid(RESTART)))
				.and().whereLess("obs.obsDatetime", hdsd.getEndDate());
		queryBuilder.orderDesc("obs.personId,obs.obsDatetime");

		List<Obs> liveObs = evaluationService.evaluateToList(queryBuilder, Obs.class, context);

		for (Obs obs : liveObs) {
			if (!uniqiObs.contains(obs.getPersonId())) {
				uniqiObs.add(obs.getPersonId());
				// patientStatus.put(obs.getPersonId(), obs.getValueCoded());
			}
		}

		return uniqiObs;
	}

	private List<Obs> getByRegimen(List<String> _line, List<Integer> patientsId) {
		List<Integer> patients = new ArrayList<>();
		List<Obs> localObs = new ArrayList<>();
		if (patientsId == null || patientsId.size() == 0)
			return localObs;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class, "obs")
				.whereInAny("obs.concept", conceptService.getConceptByUuid(REGIMEN))
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and().whereIdIn("obv.personId", patientsId).and().whereIn("obs.valueCoded", _line)
				.orderDesc("obs.obsDatetime");
		for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
					if(!patients.contains(obs.getPersonId()))
					  {
						patients.add(obs.getPersonId());
						localObs.add(obs);
					  }
				}
		
		return localObs;
	}
	public List<Obs> getByPregnancyStatus(List<Integer> patientsId, Concept concept_answer){
		List<Obs> pregObs = getAllPregnancyStatus(patientsId);
		List<Obs> patientBypregStatus = new ArrayList<>();
		for(Obs preg: pregObs){
			if(preg.getValueCoded()==concept_answer)
					  {	
						patientBypregStatus.add(preg);
					  }
			}
		return patientBypregStatus;
	}

	public List<Obs> getAllPregnancyStatus(List<Integer> patientsId){
		List<Integer> patients = new ArrayList<>();
		List<Obs> pregObs = new ArrayList<>();
		if (patientsId == null || patientsId.size() == 0)
			return pregObs;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class, "obs")
				.whereInAny("obs.concept", conceptService.getConceptByUuid(PREGNANCY_STATUS))
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and().whereIdIn("obv.personId", patientsId)
				.orderDesc("obs.obsDatetime");
		for (Obs obs : evaluationService.evaluateToList(queryBuilder, Obs.class, context)) {
					if(!patients.contains(obs.getPersonId()))
					  {
						patients.add(obs.getPersonId());
						pregObs.add(obs);
					  }
				}
		
		return pregObs;

}

}



enum Gender {
	Female,
	Male
}
