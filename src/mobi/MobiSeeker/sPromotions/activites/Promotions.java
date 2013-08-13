package mobi.MobiSeeker.sPromotions.activites;


import java.util.ArrayList;

import com.google.gson.Gson;

import mobi.MobiSeeker.sPromotions.R;
import mobi.MobiSeeker.sPromotions.connection.ConnectionConstant;
import mobi.MobiSeeker.sPromotions.connection.IChordServiceListener;
import mobi.MobiSeeker.sPromotions.connection.NodeManager;
import mobi.MobiSeeker.sPromotions.connection.ServiceManger;
import mobi.MobiSeeker.sPromotions.connection.onConnected;
import mobi.MobiSeeker.sPromotions.data.Entry;
import mobi.MobiSeeker.sPromotions.data.Repository;
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
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Promotions extends BaseActivity implements
		ActionBar.TabListener ,IChordServiceListener,onConnected{

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
	String nodeName;
	
	protected Repository repository;

	/*
	 * Connection Manger
	 * */
	
	ServiceManger manger;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promotions);
        manger=ServiceManger.getInstance(this,true,this);
        manger.startService();
        manger.bindChordService();

		this.nodeName = "NodeName"; // need to get this from chrod nodeManager
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(),
				this, this.nodeName);

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
		this.intentFIlter.addAction(Promotions.View_Remote_Promotion_Action);

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
		} else if (intent.getAction().equalsIgnoreCase(
				Promotions.View_Remote_Promotion_Action)) {
			setIntent(intent);
			this.viewRemotePromotion();
		}
	}

	@Override
	public void onBackPressed() {
		if (mSectionsPagerAdapter.getPromoteMode() != FragmentMode.List) {
			this.viewLocalPromotions();
			return;	
		}
		
		if (mSectionsPagerAdapter.getPromotionMode() != FragmentMode.List) {
			this.viewRemotePromotions();
			return;	
		}
		
		super.onBackPressed();
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
		mSectionsPagerAdapter.setRemotePromotionsMode(FragmentMode.List);
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
		this.viewRemotePromotions();
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

			if( imageReturnedIntent == null) {
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
	
	private void viewRemotePromotion() {
		mSectionsPagerAdapter.setRemotePromotionsMode(FragmentMode.View);
		mSectionsPagerAdapter.notifyDataSetChanged();
	}
	
	private void viewRemotePromotions() {
		mSectionsPagerAdapter.setRemotePromotionsMode(FragmentMode.List);
		mSectionsPagerAdapter.notifyDataSetChanged();
	}
	
	private void addTabs(final ActionBar actionBar) {
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

		
Ringtone r ;
Vibrator   vibrator;
	public void runNotification()
	{
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		r.play();
		
		if(new Settings(this).isVibrate()){
		 //Set the pattern for vibration   
        long pattern[]={0,200,100,300,400};
        //Start the vibration
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        //start vibration with repeated count, use -1 if you don't want to repeat the vibration
        vibrator.vibrate(pattern, -1);        
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		stop();
		if(vibrator!=null&&vibrator.hasVibrator())
			vibrator.cancel();
	}
	
	public void stop()
	{
		if(r!=null&&r.isPlaying())
		r.stop();
	}

	/*
	 * Listeners
	 * 
	 * */
	
	@Override
	public void connected() {
		// TODO Auto-generated method stub
		System.out.println("Connected");
	}

	@Override
	public void onReceiveMessage(String node, String channel, String message,
			String MessageType) {
		// TODO Auto-generated method stub
		try{
		Repository Remoterepository = new Repository(Promotions.Remote);
		Entry currentEntry=new Gson().fromJson(message, Entry.class);
		Remoterepository.save(this,currentEntry);
		Toast.makeText(this, message, 10000).show();
		
		}catch(Exception ee){ee.printStackTrace();}
	}

	@Override
	public void onFileWillReceive(String node, String channel, String fileName,
			String exchangeId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileProgress(boolean bSend, String node, String channel,
			int progress, String exchangeId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileCompleted(int reason, String node, String channel,
			String exchangeId, String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNodeEvent(String node, String channel, boolean bJoined) {
		// TODO Auto-generated method stub
		System.out.println("On Node Event");
		try {
			this.repository = new Repository(Promotions.Local);
			ArrayList<Entry> entries=this.repository.getEntries(this);
			if(entries!=null)
			{
				for(int i=0;i<entries.size();i++)
				{
					manger.getmChordService().sendData(NodeManager.CHORD_API_CHANNEL, entries.get(i).toString().getBytes(), node, ConnectionConstant.ENTRY);
				}
			}
			
		}catch(Exception ee){ee.printStackTrace();}
	}

	@Override
	public void onNetworkDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateNodeInfo(String nodeName, String ipAddress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectivityChanged() {
		// TODO Auto-generated method stub
		System.out.println("Connected");
	}

	
}
