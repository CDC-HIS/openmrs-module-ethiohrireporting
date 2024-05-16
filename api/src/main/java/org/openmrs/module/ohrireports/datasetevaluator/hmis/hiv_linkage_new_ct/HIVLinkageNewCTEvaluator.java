package org.openmrs.module.ohrireports.datasetevaluator.hmis.hiv_linkage_new_ct;

import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_1_NAME;
import static org.openmrs.module.ohrireports.datasetevaluator.hmis.HMISConstant.COLUMN_2_NAME;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.module.ohrireports.api.impl.query.HivLinkageNewCtQuery;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class HIVLinkageNewCTEvaluator {
	
	@Autowired
	private HivLinkageNewCtQuery hivLinkageNewCtQuery;

	List<Person> persons = new ArrayList<>();

	
	public void buildDataset(Date start, Date end,SimpleDataSet dataSet) {

		
		hivLinkageNewCtQuery.initialize(start, end);
		
		buildDataSet(dataSet);

	}

	private void buildDataSet(SimpleDataSet dataSet) {

		dataSet.addRow(buildColumn(" ",
				"Linkage outcome of newly identified Hiv positive individuals in the reporting period",
				AggregationType.TOTAL));

		dataSet.addRow(buildColumn(".1", "Linked to care and treatment", AggregationType.LINKED_TO_CARE_TREATMENT));

		dataSet.addRow(buildColumn(".2", "Known on Art", AggregationType.KNOWN_ON_ART));

		dataSet.addRow(buildColumn(".3", "Lost of follow up art", AggregationType.LOST_TO_FOLLOW_UP));

		dataSet.addRow(buildColumn(".4", "Referred to other facility", AggregationType.REFERRED_TO_OTHER_FACILITY));

		dataSet.addRow(buildColumn(".5", "Died", AggregationType.DIED));

		dataSet.addRow(buildColumn(".6", "Others", AggregationType.OTHERS));

	}

	private DataSetRow buildColumn(String col_1_value, String col_2_value, AggregationType aggregationType) {
		DataSetRow hivTxNewDataSetRow = new DataSetRow();
		String baseName = "HIV_LINKAGE_NEW_CT ";
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		String column_3_name = "Number";
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
				getHTXNew(aggregationType));
		return hivTxNewDataSetRow;
	}

	private Integer getHTXNew(AggregationType aggregateType) {
		Integer count = 0;
		switch (aggregateType) {
			case TOTAL:
				count = hivLinkageNewCtQuery.totalCount;
				break;
			case LINKED_TO_CARE_TREATMENT:
				count = hivLinkageNewCtQuery.countOfLinkedToCareAndTreatment();
				break;
			case KNOWN_ON_ART:
				count = hivLinkageNewCtQuery.countOfKnownOnArt();
				break;
			case LOST_TO_FOLLOW_UP:
				count = hivLinkageNewCtQuery.countOfLostToFollowUp();
				break;
			case REFERRED_TO_OTHER_FACILITY:
				count = hivLinkageNewCtQuery.countOfReferred();

				break;
			case DIED:
				count = hivLinkageNewCtQuery.countOfDied();
				break;
			default:
				count = hivLinkageNewCtQuery.countOfOther();
				break;
		}
		return count;
	}

	enum AggregationType {
		TOTAL,
		LINKED_TO_CARE_TREATMENT,
		KNOWN_ON_ART,
		LOST_TO_FOLLOW_UP,
		REFERRED_TO_OTHER_FACILITY,
		DIED,
		OTHERS,
	}

}
