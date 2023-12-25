package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.*;

public class Linkage {
    private int person_id;
    private Integer linkedToCareAndTreatment;
    private Integer reasonForNotStartedArt;
    private Integer finalOutCome;
    private Integer startedArt;

    public void setLinkage(String conceptUUid,Integer value){
        switch (conceptUUid){
            case LINKED_TO_CARE_TREATMENT:
                linkedToCareAndTreatment = value;
                break;
            case STARTED_ART_OR_kNOWN_POSITIVE_ON_ART:
                startedArt = value;
                break;
            case REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY:
                reasonForNotStartedArt =value;
                break;
            case FINAL_OUT_COME:
                finalOutCome = value;
                break;
            default:
                break;
        }
    }

    public Linkage(int person_id) {
        this.person_id = person_id;
    }

    public int getPerson_id() {
        return person_id;
    }


    public Integer getLinkedToCareAndTreatment() {
        return linkedToCareAndTreatment;
    }

    public Integer getReasonForNotStartedArt() {
        return reasonForNotStartedArt;
    }

    public Integer getFinalOutCome() {
        return finalOutCome;
    }

    public Integer getStartedArt() {
        return startedArt;
    }



    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }


}
