package mobi.MobiSeeker.sPromotions.activites;

import java.util.Locale;

import mobi.MobiSeeker.sPromotions.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	private Context context;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position) {
		case 0:	
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		case 1:
			return new PromotedList();
		case 2:
			return new SettingsFragment();
		case 3:
			return new SettingsFragment();
		default:
			return new SettingsFragment();
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.context.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return this.context.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return this.context.getString(R.string.action_settings).toUpperCase(l);
		}
		return null;
	}
}
