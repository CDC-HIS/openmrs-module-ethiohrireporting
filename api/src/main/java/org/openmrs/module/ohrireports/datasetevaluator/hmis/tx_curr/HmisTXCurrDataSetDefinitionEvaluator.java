package org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.tx_curr.HmisTXCurrDataSetDefinition;
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
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.*;

@Handler(supports = { HmisTXCurrDataSetDefinition.class })
public class HmisTXCurrDataSetDefinitionEvaluator implements DataSetEvaluator {

	@Autowired
	EvaluationService evaluationService;

	@Autowired
	ConceptService conceptService;

	HashMap<Integer, Concept> patientStatus = new HashMap<>();
	List<Obs> obses = new ArrayList<>();
	List<Obs> sub_obses = new ArrayList<>();
	SimpleDataSet data = null;

	private HmisTXCurrDataSetDefinition hdsd;
	private EvaluationContext context;
	private String COLUMN_3_NAME = "Number";
	private PatientQueryService patientQueryService;

	List<String> children_first_line = Arrays.asList(R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV, R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4h_OTHER_CHILD_1ST_LINE_REGIMEN, R4i_TDF_3TC_DTG, R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG, R4L_ABC_3TC_EFV);

	List<String> children_second_line = Arrays.asList(R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV, R5d_TDF_DDL_NFV, R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV, R5i_TDF_3TC_LPVr, R5j_OTHER_CHILD_2ND_LINE_REGIMEN, R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC, R5m_ABC_3TC_DTG, R5n_AZT_3TC_DTG,R5o_TDF_3TC_DTG);

	List<String> children_third_line = Arrays.asList(R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC, R6e_OTHER_CHILD_3RD_LINE_REGIMEN, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);

	List<String> adult_first_line = Arrays.asList(R1a30_D4T_30_3TC_NVP, R1a40_D4T_40_3TC_NVP, R1b30_D4T_30_3TC_EFV, R1b40_D4T_40_3TC_EFV, R1c_AZT_3TC_NVP, R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1f_TDF_3TC_NVP, R1g_ABC_3TC_EFV, R1h_ABC_3TC_NVP, R1j_TDF_3TC_DTG, R1i_OTHER_ADULT_1ST_LINE_REGIMEN, R1k_AZT_3TC_DTG);

	List<String> adult_second_line = Arrays.asList(R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV, R2c_TDF_DDL_LPV, R2d_TDF_DDL_NFV, R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr, R2i_ABC_3TC_LPVr, R2j_TDF_3TC_DTG, R2k_AZT_3TC_DTG, R2L_OTHER_ADULT_2ND_LINE_REGIMEN);

	List<String> adult_third_line = Arrays.asList(R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG, R3d_OTHER_ADULT_3RD_LINE_REGIMEN, R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV);

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		hdsd = (HmisTXCurrDataSetDefinition) dataSetDefinition;
		context = evalContext;
		patientQueryService = Context.getService(PatientQueryService.class);
		
		data = new SimpleDataSet(dataSetDefinition, evalContext);	
		
		Cohort cohort =patientQueryService.getActiveOnArtCohort("", hdsd.getStartDate(), hdsd.getEndDate(), null);
		data.addRow(buildColumn("HIV_HIV_Treatement.", "Does health facility provide Monthly PMTCT / ART Treatment Service?", 0));
		obses = getTxCurrPatients();

		

		data.addRow(buildColumn("HIV_TX_CURR_ALL", "Number of adults and children who are currently on ART by age, sex and regimen category", cohort.size()));

		data.addRow(buildColumn("HIV_TX_CURR_U15", "Number of children (<15) who are currently on ART", getPersonByAge(0,15).size()));
		
		
		data.addRow(buildColumn("HIV_TX_CURR_U1", "Children currently on ART aged <1 yr", getPersonByAge(0, 1).size()));
	
		buildDataSet("HIV_TX_CURR_U1",data,0,1,"Children");

		Integer minCount = 1;
        Integer maxCount = 4;
        while (minCount <= 50) {
			String category = "Children";
			if (maxCount > 15){
				category = "Adult";
				if(maxCount==19){
		
					data.addRow(buildColumn("HIV_TX_CURR_ADULT", "Number of adults who are currently on ART (>=15)", getPersonByAge(15,150).size()));
				}
			}
            if (minCount == 50) {
		
				data.addRow(buildColumn("HIV_TX_CURR_U50", "Adult currently on ART aged 50+", getPersonByAge(minCount, maxCount).size()));

				buildDataSet("HIV_TX_CURR_U50", data, minCount, maxCount, category);            
            } 
			else {
		
				data.addRow(buildColumn("HIV_TX_CURR_U"+maxCount, category+" currently on ART aged "+minCount+"-"+maxCount+" yr", getPersonByAge(minCount, maxCount).size()));

				buildDataSet("HIV_TX_CURR_U"+maxCount,data,minCount,maxCount, category);
            }

            minCount=1+maxCount;
            maxCount = minCount + 4;
        }


		data.addRow(buildColumn("HIV_TX_CURR_PREG", "Currently on ART by pregnancy status", getPersonByAgeandSex(0, 150,Gender.Female).size()));
		data.addRow(buildColumn("HIV_TX_CURR_PREG.1", "Pregnant", getByPregnancyStatus(getPersonByAgeandSex(0, 150,Gender.Female), conceptService.getConceptByUuid(YES)).size()));
		data.addRow(buildColumn("HIV_TX_CURR_PREG.2", "Non pregnant", getByPregnancyStatus(getPersonByAgeandSex(0, 150,Gender.Female), conceptService.getConceptByUuid(NO)).size()));
		data.addRow(buildColumn("HIV_TX_CURR_REG_U19", "Number of children (<19) who are currently on ART by regimen type", 0));
		data.addRow(buildColumn("HIV_TX_CURR_REG_U1", "Children currently on ART aged <1 yr by regimen type", 0));


		List<String> temp_regimens = Arrays.asList(R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG);
		List<String> temp_regimens_others = Arrays.asList(R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV,R4h_OTHER_CHILD_1ST_LINE_REGIMEN,R4i_TDF_3TC_DTG, R4L_ABC_3TC_EFV );
		temp_regimens_others = Stream.of(temp_regimens_others, adult_first_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U1.1", temp_regimens, temp_regimens_others, 0, 1, "first", "4h=other first line", "Children");
		
		temp_regimens = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr,R5m_ABC_3TC_DTG, R5n_AZT_3TC_DTG);
		temp_regimens_others = Arrays.asList(R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV, R5d_TDF_DDL_NFV,R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV, R5i_TDF_3TC_LPVr, R5j_OTHER_CHILD_2ND_LINE_REGIMEN, R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC,R5o_TDF_3TC_DTG);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_second_line).flatMap(x -> x.stream()).collect(Collectors.toList());

		buildBysection(data, "HIV_TX_CURR_REG_U1.2", temp_regimens, temp_regimens_others, 0, 1, "second", "5j=other second line", "Children");


		temp_regimens = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6f_DRVr_DTG_ABC_3TC);
		temp_regimens_others = Arrays.asList(R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6d_DRVr_DTG_TDF_3TC, R6e_OTHER_CHILD_3RD_LINE_REGIMEN, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_third_line).flatMap(x -> x.stream()).collect(Collectors.toList());

		buildBysection(data, "HIV_TX_CURR_REG_U1.3", temp_regimens, temp_regimens_others, 0, 1, "third", "6e=other third line", "Children");

		data.addRow(buildColumn("HIV_TX_CURR_REG_U4", "Children currently on ART aged 1-4 yr by regimen type", 0));

		temp_regimens = Arrays.asList(R4d_AZT_3TC_EFV,R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG,R4L_ABC_3TC_EFV);
		temp_regimens_others = Arrays.asList(R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4e_TDF_3TC_EFV,R4h_OTHER_CHILD_1ST_LINE_REGIMEN,R4i_TDF_3TC_DTG);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_first_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U4.1", temp_regimens, temp_regimens_others, 1, 4, "first", "4h=other first line", "Children");

		temp_regimens = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr,R5h_ABC_3TC_EFV, R5m_ABC_3TC_DTG, R5n_AZT_3TC_DTG);
		temp_regimens_others = Arrays.asList(R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV, R5d_TDF_DDL_NFV, R5g_TDF_3TC_EFV, R5i_TDF_3TC_LPVr, R5j_OTHER_CHILD_2ND_LINE_REGIMEN, R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC,R5o_TDF_3TC_DTG);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_second_line).flatMap(x -> x.stream()).collect(Collectors.toList());

		buildBysection(data, "HIV_TX_CURR_REG_U4.2", temp_regimens, temp_regimens_others, 1, 4, "second", "5j=other second line", "Children");

		temp_regimens = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);
		temp_regimens_others = Arrays.asList(R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6d_DRVr_DTG_TDF_3TC, R6e_OTHER_CHILD_3RD_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_third_line).flatMap(x -> x.stream()).collect(Collectors.toList());

		buildBysection(data, "HIV_TX_CURR_REG_U4.3", temp_regimens, temp_regimens_others, 1, 4, "third", "6e=other third line", "Children");

		data.addRow(buildColumn("HIV_TX_CURR_REG_U9", "Children currently on ART aged 5-9 yr by regimen type", 0));

		temp_regimens = Arrays.asList(R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV, R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4i_TDF_3TC_DTG, R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG, R4L_ABC_3TC_EFV);
		temp_regimens_others = Arrays.asList(R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4h_OTHER_CHILD_1ST_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_first_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U9.1", temp_regimens, temp_regimens_others, 5, 9, "first", "4h=other first line", "Children");

		temp_regimens = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV, R5i_TDF_3TC_LPVr, R5m_ABC_3TC_DTG, R5n_AZT_3TC_DTG, R5o_TDF_3TC_DTG);
		temp_regimens_others = Arrays.asList(R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV, R5d_TDF_DDL_NFV, R5j_OTHER_CHILD_2ND_LINE_REGIMEN, R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_second_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U9.2", temp_regimens, temp_regimens_others, 5, 9, "second", "5j=other second line", "Children");

		temp_regimens = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);
		temp_regimens_others = Arrays.asList(R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6e_OTHER_CHILD_3RD_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_third_line).flatMap(x -> x.stream()).collect(Collectors.toList());

		buildBysection(data, "HIV_TX_CURR_REG_U9.3", temp_regimens, temp_regimens_others, 5, 9, "third", "6e=other third line", "Children");
		data.addRow(buildColumn("HIV_TX_CURR_REG_U14", "Children currently on ART aged 10-14 yr by regimen type", 0));
		temp_regimens = Arrays.asList(R4d_AZT_3TC_EFV, R4e_TDF_3TC_EFV, R4f_AZT_3TC_LPVr, R4g_ABC_3TC_LPVr, R4i_TDF_3TC_DTG, R4j_ABC_3TC_DTG, R4k_AZT_3TC_DTG, R4L_ABC_3TC_EFV);
		temp_regimens_others = Arrays.asList(R4a_D4T_3TC_NVP, R4b_D4T_3TC_EFV, R4c_AZT_3TC_NVP, R4h_OTHER_CHILD_1ST_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_first_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U14.1", temp_regimens, temp_regimens_others, 10, 14, "first", "4h=other first line", "Children");

		temp_regimens = Arrays.asList(R5e_ABC_3TC_LPVr, R5f_AZT_3TC_LPVr, R5g_TDF_3TC_EFV, R5h_ABC_3TC_EFV, R5i_TDF_3TC_LPVr, R5m_ABC_3TC_DTG, R5n_AZT_3TC_DTG, R5o_TDF_3TC_DTG);
		temp_regimens_others = Arrays.asList(R5a_ABC_DDL_LPY, R5b_ABC_DDL_NFV, R5c_TDF_DDL_LPV, R5d_TDF_DDL_NFV, R5j_OTHER_CHILD_2ND_LINE_REGIMEN, R5k_RAL_AZT_3TC, R5L_RAL_ABC_3TC);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_second_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U14.2", temp_regimens, temp_regimens_others, 10, 14, "second", "5j=other second line", "Children");

		temp_regimens = Arrays.asList(R6c_DRVr_DTG_AZT_3TC, R6d_DRVr_DTG_TDF_3TC, R6f_DRVr_DTG_ABC_3TC, R6g_DRVr_ABC_3TC_EFV, R6h_DRVr_AZT_3TC_EFV);
		temp_regimens_others = Arrays.asList(R6a_DRVr_RAL_AZT_3TC, R6b_DRVr_RAL_TDF_3TC, R6e_OTHER_CHILD_3RD_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, adult_third_line).flatMap(x -> x.stream()).collect(Collectors.toList());

		buildBysection(data, "HIV_TX_CURR_REG_U14.3", temp_regimens, temp_regimens_others, 10, 14, "third", "6e=other third line", "Children");
		data.addRow(buildColumn("HIV_TX_CURR_REG_U19", "Adult currently on ART aged 15-19 yr by regimen type", 0));

		temp_regimens = Arrays.asList(R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1g_ABC_3TC_EFV, R1j_TDF_3TC_DTG, R1k_AZT_3TC_DTG );
		temp_regimens_others = Arrays.asList(R1a30_D4T_30_3TC_NVP, R1a40_D4T_40_3TC_NVP, R1b30_D4T_30_3TC_EFV, R1b40_D4T_40_3TC_EFV, R1c_AZT_3TC_NVP, R1f_TDF_3TC_NVP, R1h_ABC_3TC_NVP, R1i_OTHER_ADULT_1ST_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, children_first_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U19.1", temp_regimens, temp_regimens_others, 15, 19, "first", "1i=other first line", "Adult");

		temp_regimens = Arrays.asList(R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr, R2i_ABC_3TC_LPVr, R2j_TDF_3TC_DTG, R2k_AZT_3TC_DTG);
		temp_regimens_others = Arrays.asList(R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV, R2c_TDF_DDL_LPV, R2d_TDF_DDL_NFV, R2L_OTHER_ADULT_2ND_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, children_second_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U19.2", temp_regimens, temp_regimens_others, 15, 19, "second", "2L=other second line", "Adult");

		temp_regimens = Arrays.asList(R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG, R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV);
		temp_regimens_others = Arrays.asList(R3d_OTHER_ADULT_3RD_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, children_third_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysection(data, "HIV_TX_CURR_REG_U19.3", temp_regimens, temp_regimens_others, 15, 19, "third", "3d=other third line", "Adult");
		data.addRow(buildColumn("HIV_TX_CURR_REG_20", "Adults >=20 years currently on ART by regimen type", 0));
		temp_regimens = Arrays.asList(R1d_AZT_3TC_EFV, R1e_TDF_3TC_EFV, R1g_ABC_3TC_EFV, R1j_TDF_3TC_DTG, R1k_AZT_3TC_DTG );
		temp_regimens_others = Arrays.asList(R1a30_D4T_30_3TC_NVP, R1a40_D4T_40_3TC_NVP, R1b30_D4T_30_3TC_EFV, R1b40_D4T_40_3TC_EFV, R1c_AZT_3TC_NVP, R1f_TDF_3TC_NVP, R1h_ABC_3TC_NVP, R1i_OTHER_ADULT_1ST_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, children_first_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysectionandGender(data, "HIV_TX_CURR_REG_20.1", temp_regimens, temp_regimens_others, 20, 150, "first", "1i = Other first line");
		temp_regimens = Arrays.asList(R2e_AZT_3TC_LPVr, R2f_AZT_3TC_ATVr, R2g_TDF_3TC_LPVr, R2h_TDF_3TC_ATVr, R2i_ABC_3TC_LPVr, R2j_TDF_3TC_DTG, R2k_AZT_3TC_DTG);
		temp_regimens_others = Arrays.asList(R2a_ABC_DDL_LPV, R2b_ABC_DDL_NFV, R2c_TDF_DDL_LPV, R2d_TDF_DDL_NFV, R2L_OTHER_ADULT_2ND_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, children_second_line).flatMap(x -> x.stream()).collect(Collectors.toList());

		buildBysectionandGender(data, "HIV_TX_CURR_REG_20.2", temp_regimens, temp_regimens_others, 20, 150, "second", "2l = Other second line");
		temp_regimens = Arrays.asList(R3a_DRVr_DTG_AZT_3TC, R3b_DRVr_DTG_TDF_3TC, R3c_DRVr_ABC_3TC_DTG, R3e_DRVr_TDF_3TC_EFV, R3f_DRVr_AZT_3TC_EFV);
		temp_regimens_others = Arrays.asList(R3d_OTHER_ADULT_3RD_LINE_REGIMEN);
		temp_regimens_others = Stream.of(temp_regimens_others, children_third_line).flatMap(x -> x.stream()).collect(Collectors.toList());
		buildBysectionandGender(data, "HIV_TX_CURR_REG_20.3", temp_regimens, temp_regimens_others, 20, 150, "third", "3d = Other third line");



		return data;

	}

	private void buildBysectionandGender(SimpleDataSet data, String serial_no, List<String> temp_regimens,List<String> temp_regimens_others, int start_age, int end_age, String line, String otherlines){
		

		data.addRow(buildColumn(serial_no, "Adults >=20 years currently on "+line+" line regimen by regimen type", getByRegimen(getConceptByuuid(Stream.of(temp_regimens, temp_regimens_others).flatMap(x -> x.stream()).collect(Collectors.toList())),getPersonByAge(start_age,end_age)).size()));

		buildRowByRegimenType(data, serial_no+".", temp_regimens, start_age, end_age, "A");


		data.addRow(buildColumn(serial_no+"."+((temp_regimens.size()*3)+1), otherlines+", Male", getByRegimen(getConceptByuuid(temp_regimens_others),getPersonByAgeandSex(start_age, end_age, Gender.Male)).size()));
	
		

		data.addRow(buildColumn(serial_no+"."+((temp_regimens.size()*3)+2), otherlines+", Female - pregnant", getByRegimen(getConceptByuuid(temp_regimens_others),getByPregnancyStatus(getPersonByAgeandSex(start_age, end_age,Gender.Female), conceptService.getConceptByUuid(YES))).size()));
	

		data.addRow(buildColumn(serial_no+"."+((temp_regimens.size()*3)+3), otherlines+", Female - non-pregnant", getByRegimen(getConceptByuuid(temp_regimens_others),getByPregnancyStatus(getPersonByAgeandSex(start_age, end_age,Gender.Female), conceptService.getConceptByUuid(NO))).size()));

	}

	private void buildBysection(SimpleDataSet data, String serial_no, List<String> temp_regimens,List<String> temp_regimens_others, int start_age, int end_age, String line, String otherlines, String category){
		String aged=start_age+"-"+end_age;
		if (end_age==1){
			 aged="<1";
		}
		
		data.addRow(buildColumn(serial_no, category + " currently on ART aged "+aged+" yr on "+line+" line regimen by regimen type", getByRegimen(getConceptByuuid(Stream.of(temp_regimens, temp_regimens_others).flatMap(x -> x.stream()).collect(Collectors.toList())),getPersonByAge(start_age,end_age)).size()));

		buildRowByRegimenType(data, serial_no+".", temp_regimens, start_age, end_age, "B");


		data.addRow(buildColumn(serial_no+"."+(temp_regimens.size()+1), otherlines, getByRegimen(getConceptByuuid(temp_regimens_others),getPersonByAge(start_age,end_age)).size()));

	}

	private void buildRowByRegimenType(SimpleDataSet data, String serial_no_prefix, List<String> regimens, int minAge, int maxAge, String type){
		int i = 0;
		if (type=="B"){
		for (String reg: regimens){
			i+=1;
		
		data.addRow(buildColumn(serial_no_prefix+i, conceptService.getConceptByUuid(reg).getName().getName(),getByRegimen(getConceptByuuid(Arrays.asList(reg)),getPersonByAge(minAge, maxAge)).size()));

		}
	}
		else{
			for (String reg: regimens){
			i+=1;

			data.addRow(buildColumn(serial_no_prefix+i, conceptService.getConceptByUuid(reg).getName().getName()+", Male",getByRegimen(getConceptByuuid(Arrays.asList(reg)),getPersonByAgeandSex(minAge, maxAge, Gender.Male)).size()));
			i+=1;

			data.addRow(buildColumn(serial_no_prefix+i, conceptService.getConceptByUuid(reg).getName().getName()+", Female - pregnant",getByRegimen(getConceptByuuid(Arrays.asList(reg)),getByPregnancyStatus(getPersonByAgeandSex(minAge, maxAge,Gender.Female), conceptService.getConceptByUuid(YES))).size()));
			i+=1;

			data.addRow(buildColumn(serial_no_prefix+i, conceptService.getConceptByUuid(reg).getName().getName()+", Female - non-pregnant",getByRegimen(getConceptByuuid(Arrays.asList(reg)),getByPregnancyStatus(getPersonByAgeandSex(minAge, maxAge,Gender.Female), conceptService.getConceptByUuid(NO))).size()));

		}}

		

	}


	private void buildDataSet(String name, SimpleDataSet data, int minAge, int maxAge, String category) {
		List<List<Concept>> listOfLevels = new ArrayList<List<Concept>>();
		listOfLevels=getListofLevels(maxAge);
		int i=0;
		for (List<Concept> level : listOfLevels) {
			i=i+1;
			sub_obses=getByRegimen(level,getPersonByAge(minAge, maxAge));
			String cname=name+"."+i;
			if (maxAge == 1){		

				data.addRow(buildColumn(cname, "Children currently on ART aged <1 yr on "+getLevel(i)+"-line regimen by sex",sub_obses.size()));
			}
			else{
				if(maxAge > 50){

					data.addRow(buildColumn(cname, category+" currently on ART aged 50+ on "+getLevel(i)+"-line regimen by sex",sub_obses.size()));
				}
				else{

				data.addRow(buildColumn(cname, category+" currently on ART aged "+minAge+"-"+maxAge+" yr on "+getLevel(i)+"-line regimen by sex",sub_obses.size()));
				}
			}
			data.addRow(buildColumn(cname+".1","Male",getPersonByAgeandSex(minAge,maxAge,Gender.Male).size()));

			data.addRow(buildColumn(cname+".2","Female",getPersonByAgeandSex(minAge,maxAge,Gender.Female).size()));
		}
           
    }

	private DataSetRow buildColumn(String col_1_value, String col_2_value, Integer col_3_value) {
		DataSetRow txCurrDataSetRow = new DataSetRow();
		txCurrDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				col_1_value);
		txCurrDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		
		txCurrDataSetRow.addColumnValue(new DataSetColumn(COLUMN_3_NAME, COLUMN_3_NAME, Integer.class),
				col_3_value);
		
		return txCurrDataSetRow;
	}

	private List<List<Concept>> getListofLevels(Integer age){
		
		List<List<Concept>> listOfLevels = new ArrayList<List<Concept>>();
		
		if (age > 15){
			listOfLevels.add(getConceptByuuid(adult_first_line));
			listOfLevels.add(getConceptByuuid(adult_second_line));
			listOfLevels.add(getConceptByuuid(adult_third_line));

		}
		else{
			listOfLevels.add(getConceptByuuid(children_first_line));
			listOfLevels.add(getConceptByuuid(children_second_line));
			listOfLevels.add(getConceptByuuid(children_third_line));

		}
		return listOfLevels;
		
	}
	private List<Concept> getConceptByuuid(List<String> conceptUuids){
		List<Concept> conceptList = new ArrayList<Concept>();
		for (String uuid: conceptUuids){
			conceptList.add(conceptService.getConceptByUuid(uuid));
		}
		return conceptList;
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

	private List<Obs> getByRegimen(List<Concept> _line, List<Integer> patientsId) {
		List<Integer> patients = new ArrayList<>();
		List<Obs> localObs = new ArrayList<>();
		if (patientsId == null || patientsId.size() == 0)
			return localObs;
		HqlQueryBuilder queryBuilder = new HqlQueryBuilder();
		queryBuilder.select("obs").from(Obs.class, "obs")
				.whereInAny("obs.concept", conceptService.getConceptByUuid(REGIMEN))
				.whereEqual("obs.encounter.encounterType", hdsd.getEncounterType())
				.and().whereIdIn("obs.personId", patientsId).and().whereIn("obs.valueCoded", _line).and().whereLess("obs.obsDatetime", hdsd.getEndDate())
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
	public List<Integer> getByPregnancyStatus(List<Integer> patientsId, Concept concept_answer){
		List<Obs> pregObs = getAllPregnancyStatus(patientsId);
		List<Integer> patientBypregStatus = new ArrayList<>();
		for(Obs preg: pregObs){
			if(preg.getValueCoded()==concept_answer)
					  {	
						patientBypregStatus.add(preg.getPersonId());
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
				.and().whereIdIn("obs.personId", patientsId).and().whereLess("obs.obsDatetime", hdsd.getEndDate())
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
