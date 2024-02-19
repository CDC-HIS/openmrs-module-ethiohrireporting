package org.openmrs.module.ohrireports.datasetevaluator.hmis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
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
	
	public static Cohort getLeftOuterUnion(Cohort a, Cohort b) {
		if (a == null && b == null)
			return new Cohort();
		if (a == null || a.isEmpty()) {
			return new Cohort();
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
	
	public static HashMap<Integer, Object> getDictionary(Query query) {
		List list = query.list();
		HashMap<Integer, Object> dictionary = new HashMap<>();
		int personId = 0;
		Object[] objects;
		for (Object object : list) {

			objects = (Object[]) object;
			personId = (Integer) objects[0];

			if (dictionary.get((Integer) personId) == null) {
				dictionary.put(personId, objects[1]);
			}

		}

		return dictionary;
	}
	
	public static HashMap<Integer, BigDecimal> getDictionaryWithBigDecimal(Query query) {
		List list = query.list();
		HashMap<Integer, BigDecimal> dictionary = new HashMap<>();
		int personId = 0;
		Object[] objects;
		for (Object object : list) {
			objects = (Object[]) object;
			personId = (Integer) objects[0];

			if (dictionary.get((Integer) personId) == null) {
				dictionary.put(personId, (BigDecimal) objects[1]);
			}
		}

		return dictionary;
	}
}
