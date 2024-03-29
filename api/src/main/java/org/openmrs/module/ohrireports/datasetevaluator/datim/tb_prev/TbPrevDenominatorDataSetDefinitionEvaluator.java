package org.openmrs.module.ohrireports.datasetevaluator.datim.tb_prev;

import static org.openmrs.module.ohrireports.OHRIReportsConstants.TPT_START_DATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.module.ohrireports.api.impl.query.EncounterQuery;
import org.openmrs.module.ohrireports.api.impl.query.TBQuery;
import org.openmrs.module.ohrireports.api.query.AggregateBuilder;
import org.openmrs.module.ohrireports.datasetdefinition.datim.tb_prev.TbPrevDominatorDatasetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.springframework.beans.factory.annotation.Autowired;

@Handler(supports = { TbPrevDominatorDatasetDefinition.class })
public class TbPrevDenominatorDataSetDefinitionEvaluator implements DataSetEvaluator {

	private TbPrevDominatorDatasetDefinition hdsd;

	@Autowired
	private TBQuery tbQuery;

	List<Integer> baseEncounters = new ArrayList<>();
	private Date endDate = null;
	@Autowired
	private EncounterQuery encounterQuery;

	@Autowired
	private AggregateBuilder _AggregateBuilder;

	@Override
	public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext evalContext)
			throws EvaluationException {

		hdsd = (TbPrevDominatorDatasetDefinition) dataSetDefinition;
		SimpleDataSet set = new SimpleDataSet(dataSetDefinition, evalContext);
	
		_AggregateBuilder.setCalculateAgeFrom(hdsd.getEndDate());
		Date prevSixMonth = getPrevSixMonth();
	
		if (Objects.isNull(endDate) || !endDate.equals(hdsd.getEndDate()))
			baseEncounters = encounterQuery.getEncounters(Arrays.asList(TPT_START_DATE), prevSixMonth, hdsd.getStartDate());

		endDate = hdsd.getEndDate();
		Cohort tptCohort = tbQuery.getTPTCohort(baseEncounters, TPT_START_DATE, prevSixMonth,
				hdsd.getStartDate());
		Cohort onArtCorCohort = new Cohort(
				tbQuery.getArtStartedCohort(baseEncounters, null, hdsd.getEndDate(), tptCohort));

		if (!hdsd.getAggregateType()) {

			buildRowForTotalValue(set, onArtCorCohort.size());

		} else {
			Cohort newOnARTCohort = new Cohort(
					tbQuery.getArtStartedCohort("", prevSixMonth, endDate, onArtCorCohort, null,baseEncounters));
			Cohort oldOnACohort = new Cohort(tbQuery.getArtStartedCohort("", null, prevSixMonth, onArtCorCohort, null,baseEncounters));

			buildRowForDisaggregation(set, newOnARTCohort, oldOnACohort);

		}

		return set;
	}

	private Date getPrevSixMonth() {
		Calendar subSixMonth = Calendar.getInstance();
		subSixMonth.setTime(hdsd.getStartDate());
		subSixMonth.add(Calendar.MONTH, -6);
        return subSixMonth.getTime();
	}

	private void buildRowForDisaggregation(SimpleDataSet set, Cohort newOnARTCohort, Cohort oldOnACohort) {
		// Disaggregated By ART Start by Age/Sex
		DataSetRow dataSetRow = new DataSetRow();
		dataSetRow.addColumnValue(new DataSetColumn("", "", String.class), "Newly enrolled on ART");
		_AggregateBuilder.setPersonList(tbQuery.getPersons(newOnARTCohort));
		DataSetRow femaleDataRowNew = new DataSetRow();

		_AggregateBuilder.buildDataSetColumn(femaleDataRowNew, "F", 15);
		set.addRow(femaleDataRowNew);

		DataSetRow maleDataRowNew = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(maleDataRowNew, "M", 15);
		set.addRow(maleDataRowNew);


		DataSetRow dataSetRowPrev = new DataSetRow();
		dataSetRowPrev.addColumnValue(new DataSetColumn("", "", String.class), "Previously enrolled on ART");

		_AggregateBuilder.setPersonList(tbQuery.getPersons(oldOnACohort));

		DataSetRow femaleRowPrev = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(femaleRowPrev, "F", 15);
		set.addRow(femaleRowPrev);

		DataSetRow maleRowPrev = new DataSetRow();
		_AggregateBuilder.buildDataSetColumn(maleRowPrev, "M", 15);
		set.addRow(maleRowPrev);
	}

	private void buildRowForTotalValue(SimpleDataSet set, int total) {
		DataSetRow dataSet = new DataSetRow();
		dataSet.addColumnValue(new DataSetColumn("AutoCalculate", "Auto-Calculate", String.class), "Denominator");
		dataSet.addColumnValue(new DataSetColumn("description",
				"Number of ART patients who were initiated on any course of TPT during the previous reporting period",
				Integer.class), total);
		set.addRow(dataSet);
	}

}
