package org.openmrs.module.ohrireports.api.impl.query;

import org.hibernate.Query;
import org.openmrs.Cohort;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.ohrireports.api.impl.BaseEthiOhriQuery;
import org.openmrs.module.ohrireports.constants.*;
import org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct.Linkage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class HivLinkageNewCtQuery extends BaseEthiOhriQuery {

    public static final int NULL_VALUE = 0;
    public List<Linkage> linkageList = new ArrayList<>();
    public int totalCount = 0;
    Integer yesConceptId, knownPositiveConceptId, lostToFollowUpConceptId, conformedReferralConceptId, startArtinOtherHFConceptId, referredTxNotInitiatedConceptId, diedConceptId, declinedConceptId, onAdherencePreparationConceptId, startedArtConceptId, onOIManagementConceptId, otherConceptId, noConceptId = 0;
    @Autowired
    private DbSessionFactory sessionFactory;
    @Autowired
    private EncounterQuery encounterQuery;
    public List<Integer> baseEncounter;
    public Cohort baseCohort;
    public void initialize(Date startOnOrAfter, Date endOnOrBefore) {
        baseEncounter = encounterQuery.getEncounters(Collections.singletonList(PositiveCaseTrackingConceptQuestions.HIV_CONFIRMED_DATE), startOnOrAfter, endOnOrBefore, EncounterType.POSITIVE_TRACKING_ENCOUNTER_TYPE);
        baseCohort = getCohort(baseEncounter);
        linkageList = new ArrayList<>();
        for (Integer patientId : baseCohort.getMemberIds()) {
            linkageList.add(new Linkage(patientId));
        }
        setLinkage();
        setConcepts();
    }
    private void setConcepts() {
       List<String> uuids =Arrays.asList(ConceptAnswer.YES, ConceptAnswer.NO, FollowUpConceptQuestions.STARTED_ART, ConceptAnswer.ON_ADHERENCE, ConceptAnswer.IO_MANAGEMENT, ConceptAnswer.KNOWN_POSITIVE_ON_ART, ConceptAnswer.CONFIRMED_REFERRAL, ConceptAnswer.LOST_TO_FOLLOW_UP, ConceptAnswer.STARTED_ART_IN_OTHER_FACILITY, ConceptAnswer.REFERRED_TX_NOT_INITIATED, ConceptAnswer.DIED, ConceptAnswer.DECLINED, ConceptAnswer.OTHER);
        Query query = sessionFactory.getCurrentSession().createSQLQuery("select concept_id,uuid from concept where uuid in ('" + String.join("','", uuids) + "')");

        List list = query.list();
        Object[] objects;
        for (Object object : list) {
            objects = (Object[]) object;

            Integer conceptId = (Integer) objects[0];
            String uuid = (String) objects[1];

            switch (uuid) {
                case ConceptAnswer.YES:
                    yesConceptId = conceptId;
                    break;
                case FollowUpConceptQuestions.STARTED_ART:
                    startedArtConceptId = conceptId;
                    break;
                case ConceptAnswer.ON_ADHERENCE:
                    onAdherencePreparationConceptId = conceptId;
                    break;
                case ConceptAnswer.IO_MANAGEMENT:
                    onOIManagementConceptId = conceptId;
                    break;
                case ConceptAnswer.KNOWN_POSITIVE_ON_ART:
                    knownPositiveConceptId = conceptId;
                    break;
                case ConceptAnswer.DECLINED:
                    declinedConceptId = conceptId;
                    break;
                case ConceptAnswer.CONFIRMED_REFERRAL:
                    conformedReferralConceptId = conceptId;
                    break;
                case ConceptAnswer.LOST_TO_FOLLOW_UP:
                    lostToFollowUpConceptId = conceptId;
                    break;
                case ConceptAnswer.STARTED_ART_IN_OTHER_FACILITY:
                    startArtinOtherHFConceptId = conceptId;
                    break;
                case ConceptAnswer.REFERRED_TX_NOT_INITIATED:
                    referredTxNotInitiatedConceptId = conceptId;
                    break;
                case ConceptAnswer.DIED:
                    diedConceptId = conceptId;
                    break;
                case ConceptAnswer.OTHER:
                    otherConceptId = conceptId;
                    break;
                case ConceptAnswer.NO:
                    noConceptId = conceptId;
                    break;
                default:
                    break;
            }

        }
    }
    private void setLinkage() {
        setLinkage(PositiveCaseTrackingConceptQuestions.LINKED_TO_CARE_TREATMENT);
        setLinkage(PositiveCaseTrackingConceptQuestions.REASON_FOR_NOT_STARTING_ART_THE_SAME_DAY);
        setLinkage(PositiveCaseTrackingConceptQuestions.FINAL_OUT_COME);
        setLinkage(FollowUpConceptQuestions.STARTED_ART);
        totalCount = linkageList.size();
    }
    private void setLinkage(String conceptUUid) {
        String sqlBuilder = "select ob.person_id,ob.value_coded,c.name from obs as ob" +
                " inner join concept_name as c on c.concept_id = ob.value_coded and c.locale_preferred=1  and c.locale = 'en' "+
                " where ob.concept_id =" + conceptQuery(conceptUUid) +
                " and ob.encounter_id in (:encounters) " + " and ob.person_id in (:personId) " ;


        Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlBuilder);

        query.setParameterList("encounters", baseEncounter);
        query.setParameterList("personId", baseCohort.getMemberIds());

        List list = query.list();
        Object[] objects;
        Integer personId = 0;
        for (Object object : list) {
            objects = (Object[]) object;
            personId = (Integer) objects[0];
            for (Linkage linkage : linkageList) {
                if (linkage.getPersonId() == personId) {
                    linkage.setLinkage(conceptUUid, (Integer) objects[1],(String) objects[2]);
                }
            }
        }
    }
    
    private Cohort getCohort(List<Integer> encounterIds) {

        Query query = sessionFactory.getCurrentSession().createSQLQuery("select distinct (person_id) from obs where encounter_id in (:encounterIds) ");
        query.setParameterList("encounterIds", encounterIds);

        return new Cohort(query.list());

    }

    public int countOfLinkedToCareAndTreatment() {
        int count = 0;
        List<Integer> personIdList = new ArrayList<>();

        for (Linkage linkage : linkageList) {
            if (linkage.getLinkedToCareAndTreatment().isEqual(yesConceptId) &&
                    linkage.getReasonForNotStartedArt().isEqual(NULL_VALUE) &&
                    linkage.getFinalOutCome().isEqual(NULL_VALUE) ||
                    (linkage.getFinalOutCome().isEqual(startedArtConceptId) ||
                            (linkage.getStartedArt().isEqual(yesConceptId)) && linkage.getFinalOutCome().isEqual(NULL_VALUE))) {
                count++;
                personIdList.add(linkage.getPersonId());
            }
        }
        
        //Clear list of person ,so they would be counted in other linkage type
        personIdList.forEach(i -> {
            linkageList.removeIf(l -> l.getPersonId() == i);
        });
        return count;
    }

    public int countOfKnownOnArt() {
        int count = 0;
        List<Integer> personIdList = new ArrayList<>();

        for (Linkage linkage : linkageList) {
            if (linkage.getReasonForNotStartedArt().isEqual(knownPositiveConceptId) &&
                    linkage.getFinalOutCome().isEqual(NULL_VALUE)) {
                count++;
                personIdList.add(linkage.getPersonId());
            }
        }
        
        //Clear list of person ,so they would be counted in other linkage type
        personIdList.forEach(i -> {
            linkageList.removeIf(l -> l.getPersonId() == i);
        });
        return count;
    }

    public int countOfLostToFollowUp() {
        int count = 0;
        List<Integer> personIdList = new ArrayList<>();

        for (Linkage linkage : linkageList) {
            if (linkage.getFinalOutCome().isEqual(lostToFollowUpConceptId)) {
                count++;
                personIdList.add(linkage.getPersonId());
            }
        }
        //Clear list of person ,so they would be counted in other linkage type
        personIdList.forEach(i -> {
            linkageList.removeIf(l -> l.getPersonId() == i);
        });
        return count;
    }

    public int countOfReferred() {
        int count = 0;
        List<Integer> personIdList = new ArrayList<>();

        for (Linkage linkage : linkageList) {
            if (linkage.getFinalOutCome().isEqual(conformedReferralConceptId) ||
                    (linkage.getFinalOutCome().isEqual(startArtinOtherHFConceptId)) ||
                    (linkage.getReasonForNotStartedArt().isEqual(referredTxNotInitiatedConceptId) &&
                            linkage.getFinalOutCome().isEqual(NULL_VALUE))) {
                count++;
                personIdList.add(linkage.getPersonId());
            }
        }
        //Clear list of person ,so they would be counted in other linkage type
        personIdList.forEach(i -> {
            linkageList.removeIf(l -> l.getPersonId() == i);
        });
        return count;
    }

    public int countOfDied() {
        int count = 0;
        List<Integer> personIdList = new ArrayList<>();

        for (Linkage linkage : linkageList) {
            if (linkage.getFinalOutCome().isEqual(diedConceptId) ||
                    linkage.getReasonForNotStartedArt().isEqual(diedConceptId)) {
                count++;
                personIdList.add(linkage.getPersonId());
            }
        }
        //Clear list of person ,so they would be counted in other linkage type
        personIdList.forEach(i -> {
            linkageList.removeIf(l -> l.getPersonId() == i);
        });
        return count;
    }

    public int countOfOther() {
        int count = 0;
        List<Integer> personIdList = new ArrayList<>();

        for (Linkage linkage : linkageList) {
            if (linkage.getFinalOutCome().isEqual(declinedConceptId) ||
                    linkage.getReasonForNotStartedArt().isEqual(otherConceptId) ||
                    (linkage.getReasonForNotStartedArt().isEqual(declinedConceptId) && linkage.getFinalOutCome().isEqual(NULL_VALUE)) ||
                    (linkage.getReasonForNotStartedArt().isEqual(onAdherencePreparationConceptId) && linkage.getFinalOutCome().isEqual(NULL_VALUE)) ||
                    (linkage.getReasonForNotStartedArt().isEqual(onOIManagementConceptId) && linkage.getFinalOutCome().isEqual(NULL_VALUE)) ||
                    (linkage.getReasonForNotStartedArt().isEqual(otherConceptId) && linkage.getFinalOutCome().isEqual(NULL_VALUE)) ||
                    ((linkage.getLinkedToCareAndTreatment().isEqual(NULL_VALUE)) || linkage.getLinkedToCareAndTreatment().isEqual(noConceptId) &&
                            linkage.getStartedArt().isEqual(NULL_VALUE) ||
                            linkage.getStartedArt().isEqual(noConceptId) && linkage.getFinalOutCome().isEqual(NULL_VALUE))) {
                count++;
                personIdList.add(linkage.getPersonId());
            }
        }
        //Clear list of person ,so they would be counted in other linkage type
        personIdList.forEach(i -> {
            linkageList.removeIf(l -> l.getPersonId() == i);
        });
        return count;
    }

}
