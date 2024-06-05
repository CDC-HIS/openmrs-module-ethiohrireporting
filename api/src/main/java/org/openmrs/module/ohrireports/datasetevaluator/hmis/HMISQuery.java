package org.openmrs.module.ohrireports.datasetevaluator.hmis;

import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.query.PatientQueryService;
import org.openmrs.module.ohrireports.datasetdefinition.hmis.hiv_pvls.HivPvlsType;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt.HMISARTTPTEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt_cr_1.HMISARTTPTCrOneEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.art_tpt_cr_2.HMISArtTptCrTwoEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_rx.HmisCxCaRXEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.cxca_scrn.HMISCxCaSCRNEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fb.HIVArtFBEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_fb.HIVArtFbMetEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_intr.HIVARTIntrEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_re_arv.HIVARTREVEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_art_ret.HIVARTRETEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct.HIVLinkageNewCTEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_plhiv.HivPlHivEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_pvls.HivPVLSEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.mtct_art.HMISARTEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pep.HIVPEPCategoryEvaluators;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.eid.HMISEIDEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei.HMISHEIABTSTEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei.HMISHEIARVEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pmtct.hei.HMISHEICOTREvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep.HIVPREPEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.pr_ep.hiv_prep_curr.HivPrEpCurrEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tb_Lb_Lf_Lam.TPLbLFLAMEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tb_scrn.TPSCRNEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_curr.HMISTXCurrEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_dsd.HMISTXDSDEvaluator;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.tx_new.HMISTXNewEvaluator;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class HMISQuery extends ColumnBuilder {
	@Autowired
	private EncounterQuery encounterQuery;
	@Autowired
	private HMISTXNewEvaluator hmistxNewEvaluator;
	@Autowired
	private HMISTXCurrEvaluator hmistxCurrEvaluator;
	@Autowired
	private HIVARTRETEvaluator hivartretEvaluator;
	@Autowired
	private HIVLinkageNewCTEvaluator hivLinkageNewCTEvaluator;
	@Autowired
	private HivPVLSEvaluator hivpVLSEvaluator;
	@Autowired
	private HMISTXDSDEvaluator hmistxdsdEvaluator;
	@Autowired
	private HIVARTIntrEvaluator hivartIntrEvaluator;
	@Autowired
	private HIVARTREVEvaluator hivartrevevaluator;
	@Autowired
	private HIVPREPEvaluator hivprepevaluator;
	@Autowired
	private HivPrEpCurrEvaluator hivprepCurrEvaluator;
	@Autowired
	private HIVPEPCategoryEvaluators hivpepcategoryEvaluators;
	@Autowired
	private HivPlHivEvaluator hivplHivEvaluator;
	@Autowired
	private HIVArtFBEvaluator hivArtFBEvaluator;
	@Autowired
	private HIVArtFbMetEvaluator hivArtFbMetEvaluator;
	@Autowired
	private TPSCRNEvaluator  tpscrnevaluator;
	@Autowired
    private HMISARTTPTEvaluator hmisarttptEvaluator;
	@Autowired
	private HMISARTTPTCrOneEvaluator hmisarttptCrOneEvaluator;
	@Autowired
	private HMISArtTptCrTwoEvaluator hmisArtTptCrTwoEvaluator;
	@Autowired
	private HMISCxCaSCRNEvaluator hmisCxCaSCRNEvaluator;
	@Autowired
	private HmisCxCaRXEvaluator hmisCxCaRXEvaluator;
	@Autowired
	private TPLbLFLAMEvaluator tpLbLFLAMEvaluator;
	@Autowired
	private HMISARTEvaluator hmisartEvaluator;
	@Autowired
	private HMISEIDEvaluator hmiseidEvaluator;
	@Autowired
	private HMISHEIABTSTEvaluator hmisHEIABTSTEvaluator;
	@Autowired
    private HMISHEIARVEvaluator hmisheiarvEvaluator;
	@Autowired
	private HMISHEICOTREvaluator hmisheicotrevaluator;
	List<Integer> encounterAlive = new ArrayList<>();
	
    public void run(Date start, Date end, SimpleDataSet dataSet) {
	    initialize(start, end);
		
	    hmistxCurrEvaluator.buildDataSet(dataSet, end, encounterQuery.getAliveFollowUpEncounters(null, end));
		//TODO: encounter should be updated
	    hmistxNewEvaluator.buildDataSet(dataSet, start, end,encounterAlive);
	
	    hivartretEvaluator.buildDataSet(dataSet, false ,start, end);
	    hivartretEvaluator.buildDataSet(dataSet, true ,start, end);

	    hivLinkageNewCTEvaluator.buildDataset(start, end,dataSet);
	    
	    hivpVLSEvaluator.buildDataset(start,end,dataSet,".1", HivPvlsType.TESTED,"Number of adult and pediatric ART patients for whom viral  load test result received in the reporting period");
	    hivpVLSEvaluator.buildDataset(start,end,dataSet,"_UN", HivPvlsType.SUPPRESSED,"(UN) Viral load Suppression in the reporting period ");
	    hivpVLSEvaluator.buildDataset(start,end,dataSet,"_LV", HivPvlsType.LOW_LEVEL_LIVERMIA,"Total number of adult and paediatric ART patients with low level viremia (50 -1000 copies/ml) in the reporting period ");
		
	    hmistxdsdEvaluator.buildDataset(start,end,dataSet);
		
	    hivartIntrEvaluator.buildDataset(start,end,dataSet);
	    
	    hivartrevevaluator.buildDataset(start,end,dataSet);
		
	    hivprepevaluator.buildDataset(start,end,dataSet);
	    
	    hivprepCurrEvaluator.buildDataset(start,end,dataSet);
	    
	    hivpepcategoryEvaluators.buildDataset(start,end,dataSet);
	  
	    hivplHivEvaluator.buildDataset(start,end,dataSet);
		
	    hivArtFBEvaluator.buildDataset(start,end,"FP:Number of non-pregnant women living with HIV on ART aged 15-49 reporting the use of any method of modern family planning by age",dataSet);
	    
	    hivArtFbMetEvaluator.buildDataset(dataSet);
	
	    tpscrnevaluator.buildDataset(start,end,dataSet);
	
	    hmisarttptEvaluator.buildDataset(start,end,dataSet);
	
	    hmisarttptCrOneEvaluator.buildDataset(start,end,dataSet);
	    
		hmisArtTptCrTwoEvaluator.buildDataset(start,end,dataSet);
		
	    hmisCxCaSCRNEvaluator.buildDataset(start,end,dataSet);
	    
		hmisCxCaRXEvaluator.buildDataset(start,end,dataSet);
	 
		tpLbLFLAMEvaluator.buildDataset(start,end,dataSet);
		
	    hmisartEvaluator.buildDataset(start,end,dataSet);
	    hmiseidEvaluator.buildDataset(start,end,dataSet);
	    hmisHEIABTSTEvaluator.buildDataset(start,end,dataSet);
	    hmisheiarvEvaluator.buildDataset(start,end,dataSet);
	    hmisheicotrevaluator.buildDataset(start,end,dataSet);
	}
	
	private void initialize(Date start, Date end) {
		encounterAlive = encounterQuery. getAliveFirstFollowUpEncounters(null, end);
	}
	
}
