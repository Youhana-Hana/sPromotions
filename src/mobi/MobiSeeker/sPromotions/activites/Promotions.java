package mobi.MobiSeeker.sPromotions.activites;

import mobi.MobiSeeker.sPromotions.R;
import mobi.MobiSeeker.sPromotions.data.Entry;
import mobi.MobiSeeker.sPromotions.data.FragmentModes.FragmentMode;
import mobi.MobiSeeker.sPromotions.data.Settings;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class Promotions extends FragmentActivity implements
		ActionBar.TabListener {

	public static final String Local = "local";
	public static final String Remote = "remote";
	public static final String View_Promotion_Action = "mobi.MobiSeeker.sPromotions.VIEW_PROMOTION";
	public static final String View_Remote_Promotion_Action = "mobi.MobiSeeker.sPromotions.VIEW_REMOTE_PROMOTION";
	public static String Add_New_Promotion_Action = "mobi.MobiSeeker.sPromotions.ADD_NEW_PROMOTION";
	public static String Delete_Local_Promotion_Action = "mobi.MobiSeeker.sPromotions.DELETE_LOCAL_PROMOTION";
	public static String View_local_Promotions_Action = "mobi.MobiSeeker.sPromotions.VIEW_LOCAL_PROMOTIONS";
	private final int REQ_CODE_PICK_IMAGE = 1;

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	View imageView = null;
	BroadcastReceiver receiver;
	IntentFilter intentFIlter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promotions);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(),
				this);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		this.addTabs(actionBar);

		this.intentFIlter = new IntentFilter(
				Promotions.Add_New_Promotion_Action);

		this.intentFIlter.addAction(Promotions.View_local_Promotions_Action);
		this.intentFIlter.addAction(Promotions.View_Promotion_Action);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleReceiverIntent(intent);
			}
		};
	}

	protected void handleReceiverIntent(Intent intent) {
		if (intent.getAction().equalsIgnoreCase(
				Promotions.Add_New_Promotion_Action)) {
			this.addViewPromotion();
		} else if (intent.getAction().equalsIgnoreCase(
				Promotions.View_Promotion_Action)) {
			setIntent(intent);
			this.addViewPromotion();
		} else if (intent.getAction().equalsIgnoreCase(
				Promotions.View_local_Promotions_Action)) {
			this.viewLocalPromotions();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, this.intentFIlter);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mSectionsPagerAdapter.setPromotionsMode(FragmentMode.List);
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		this.viewLocalPromotions();
	}

	public void pickImage(View view) {
		this.imageView = view;
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQ_CODE_PICK_IMAGE);
	}

	public void deleteEntry(View view) {
		Entry entry = (Entry) view.getTag();
		if (entry == null) {
			return;
		}

		PromotedList promotedList = (PromotedList) mSectionsPagerAdapter
				.instantiateItem(mViewPager, 1);
		promotedList.delete(entry);
	}

	public void deleteRemoteEntry(View view) {
		Entry entry = (Entry) view.getTag();
		if (entry == null) {
			return;
		}

		PromotionsList promotionsList = (PromotionsList) mSectionsPagerAdapter
				.instantiateItem(mViewPager, 0);
		promotionsList.delete(entry);
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {

		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case REQ_CODE_PICK_IMAGE:
			if (resultCode != RESULT_OK) {
				return;
			}

			if (this.imageView == null) {
				return;
			}

			String imagePath = getImageFromGallery(imageReturnedIntent,
					(ImageView) this.imageView);
			this.imageView.setTag(imagePath);

			if (this.imageView.getId() != R.id.logo) {
				return;
			}

			Settings settings = new Settings(this);
			settings.setLogo(imagePath);
		}
	}

	private String getImageFromGallery(Intent imageReturnedIntent,
			ImageView image) {
		Uri selectedImage = imageReturnedIntent.getData();
		String[] filePathColumn = { android.provider.MediaStore.Images.Media.DATA };

		Cursor cursor = getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);

		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String imagePath = cursor.getString(columnIndex);
		cursor.close();

		image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
		return imagePath;
	}

	private void addViewPromotion() {
		mSectionsPagerAdapter.setPromotionsMode(FragmentMode.Edit);
		mSectionsPagerAdapter.notifyDataSetChanged();
	}

	private void viewLocalPromotions() {
		mSectionsPagerAdapter.setPromotionsMode(FragmentMode.List);
		mSectionsPagerAdapter.notifyDataSetChanged();
	}

	private void addTabs(final ActionBar actionBar) {
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
}
