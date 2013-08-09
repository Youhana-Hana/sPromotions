package mobi.MobiSeeker.sPromotions.activites;

import java.util.Locale;

import mobi.MobiSeeker.sPromotions.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private Context context;
	private int promotionMode;
	
	public SectionsPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
		this.promotionMode = 0;
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
			if (this.promotionMode == 0) {
				return new PromotedList();
			}
			
			return new SettingsFragment();
		default:
			return new SettingsFragment();
		}
	}

	 @Override
	    public int getItemPosition(Object object)
	    {
	        if (object instanceof PromotedList && promotionMode == 1) {
	            return POSITION_NONE;
	        }
	        
	        return POSITION_UNCHANGED;
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

	public void setPromotionsMode(int mode) {
		this.promotionMode = mode;
	}
}
