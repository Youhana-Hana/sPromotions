package mobi.MobiSeeker.sPromotions.activites;

import java.util.Locale;

import mobi.MobiSeeker.sPromotions.R;
import mobi.MobiSeeker.sPromotions.data.FragmentModes.FragmentMode;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

	private Context context;
	private FragmentMode promoteMode;
	private FragmentMode promotionMode;
	private String nodeName;

	public SectionsPagerAdapter(FragmentManager fm, Context context, String nodeName) {
		super(fm);
		this.context = context;
		this.promoteMode = FragmentMode.List;
		this.promotionMode  = FragmentMode.List;
		this.nodeName = nodeName;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position) {
		case 0:	
			return getPromotions();
		case 1:
			return getPromoteFragment();
		default:
			return new SettingsFragment();
		}
	}

	private Fragment getPromotions() {
		if (this.promotionMode == FragmentMode.List) {
			return new PromotionsList();
		}
		
		return new Promotion();
	}

	private Fragment getPromoteFragment() {
		if (this.promoteMode == FragmentMode.List) {
			return new PromotedList();
		}
		
		NewPromotion newPromotion = new NewPromotion();
		newPromotion.setNodeName(this.nodeName);
		
		return newPromotion;
	}
	
		 @Override
	    public int getItemPosition(Object object)
	    {
	        if (object instanceof PromotedList && this.promoteMode == FragmentMode.List) {
	        	return POSITION_UNCHANGED;
	        
	        }
	        if (object instanceof NewPromotion && this.promoteMode == FragmentMode.NEW) {
	        	return POSITION_NONE;
	        }
	        
	        
	        if (object instanceof NewPromotion && this.promoteMode == FragmentMode.Edit) {
	        	return POSITION_UNCHANGED;
	        }
	        
	        if (object instanceof PromotionsList && this.promotionMode == FragmentMode.List) {
	        	return POSITION_UNCHANGED;
	        }
	        
	        if (object instanceof Promotion && this.promotionMode == FragmentMode.View) {
	        	return POSITION_UNCHANGED;
	        }
	        
	        if (object instanceof SettingsFragment) {
	        	return POSITION_UNCHANGED;
	        }
	        
	        return POSITION_NONE;
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

	public void setPromotionsMode(FragmentMode mode) {
		this.promoteMode = mode;
	}
	
	public void setRemotePromotionsMode(FragmentMode mode) {
		this.promotionMode = mode;
	}
	
	public FragmentMode getPromoteMode() {
		return promoteMode;
	}

	public FragmentMode getPromotionMode() {
		return promotionMode;
	}
}
