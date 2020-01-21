package com.newgen.comparator;

import java.util.Comparator;

import com.newgen.model.Profile;

public class ProfileComparator implements Comparator<Profile> {

	@Override
	public int compare(Profile o1, Profile o2) {
		// TODO Auto-generated method stub

		return o1.getObjectId().compareTo(o2.getObjectId());
	}

}
