package org.openmrs.module.ohrireports.datasetevaluator.hmis;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;

public class HMISUtilies {
	
	public static Cohort getUnion(Cohort a, Cohort b) {
		if (a == null || a.isEmpty() || b == null || b.isEmpty())
			return new Cohort();
		List<Integer> list = new ArrayList<>();
		for (Integer integer : a.getMemberIds()) {
			if (b.getMemberIds().contains(integer)) {
				list.add(integer);
			}
		}
		return new Cohort(list);

	}
	
	public static Cohort getOuterUnion(Cohort a, Cohort b) {
		if (a == null && b == null)
			return new Cohort();
		if (a == null || a.isEmpty()) {
			return b;
		} else if (b == null || b.isEmpty()) {
			return a;
		}

		List<Integer> list = new ArrayList<>();
		for (Integer integer : a.getMemberIds()) {
			if (!b.getMemberIds().contains(integer)) {
				list.add(integer);
			}
		}
		return new Cohort(list);

	}
}
