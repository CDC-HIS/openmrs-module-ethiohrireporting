package org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.hiv_linkage_new_ct;

import static org.openmrs.module.ohrireports.reports.datasetevaluator.hmis.HMISConstant.*;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.ohrireports.api.query.PatientQuery;
import org.openmrs.module.ohrireports.reports.datasetdefinition.hmis.hiv_linkage_new_ct.HIVLinkageNewCtDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { HIVLinkageNewCtDatasetDefinition.class })
public class HIVLinkageNewCtDatasetDefinitionEvaluator implements DataSetEvaluator {

	private HIVLinkageNewCtDatasetDefinition _datasetDefinition;
	private String baseName = "HIV_LINKAGE_NEW_CT ";
	private String column_3_name = "Tir 15";

	@Autowired
	private HivLinkageNewCtQuery hivLinkageNewCtQuery;

	List<Person> persons = new ArrayList<>();

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {
		
		_datasetDefinition = (HIVLinkageNewCtDatasetDefinition) dataSetDefinition;
		
		SimpleDataSet dataSet = new SimpleDataSet(dataSetDefinition, evalContext);
		
		hivLinkageNewCtQuery.initialize(_datasetDefinition.getStartDate(),_datasetDefinition.getEndDate());
		
		buildDataSet(dataSet);

		return dataSet;
	}

	public void buildDataSet(SimpleDataSet dataSet) {

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
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_1_NAME, COLUMN_1_NAME, String.class),
				baseName + "" + col_1_value);
		hivTxNewDataSetRow.addColumnValue(
				new DataSetColumn(COLUMN_2_NAME, COLUMN_2_NAME, String.class), col_2_value);
		hivTxNewDataSetRow.addColumnValue(new DataSetColumn(column_3_name, column_3_name, Integer.class),
				getHTXNew(aggregationType));
		return hivTxNewDataSetRow;
	}

	private Integer getHTXNew(AggregationType aggregateType) {
		Integer count = 0;
		switch (aggregateType) {
			case TOTAL:
				count = hivLinkageNewCtQuery.getTotalOnHivePatient();
				break;
			case LINKED_TO_CARE_TREATMENT:
				count = hivLinkageNewCtQuery.getLinkedToCareCohort().getActiveMemberships().size();
				break;
			case KNOWN_ON_ART:
				count = hivLinkageNewCtQuery.getKnownCohort().getActiveMemberships().size();
				break;
			case LOST_TO_FOLLOW_UP:
				count = hivLinkageNewCtQuery.getLostToFollowUpCohort().getActiveMemberships().size();
				break;
			case REFERRED_TO_OTHER_FACILITY:
				count = hivLinkageNewCtQuery.getReferToOtherCohort().getActiveMemberships().size();

				break;
			case DIED:
				count = hivLinkageNewCtQuery.getDiedCohort().getMemberIds().size();
				break;
			default:
				count = hivLinkageNewCtQuery.getOthersCohort();
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
