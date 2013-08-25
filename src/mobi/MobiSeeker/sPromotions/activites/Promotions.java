package mobi.MobiSeeker.sPromotions.activites;


import java.util.ArrayList;

import mobi.MobiSeeker.sPromotions.R;
import mobi.MobiSeeker.sPromotions.connection.ChordManagerService;
import mobi.MobiSeeker.sPromotions.connection.ConnectionConstant;
import mobi.MobiSeeker.sPromotions.connection.IChordServiceListener;
import mobi.MobiSeeker.sPromotions.connection.NodeManager;
import mobi.MobiSeeker.sPromotions.connection.ServiceManger;
import mobi.MobiSeeker.sPromotions.connection.onConnected;
import mobi.MobiSeeker.sPromotions.data.Entry;
import mobi.MobiSeeker.sPromotions.data.FragmentModes.FragmentMode;
import mobi.MobiSeeker.sPromotions.data.Repository;
import mobi.MobiSeeker.sPromotions.data.Settings;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

public class Promotions extends BaseActivity implements
		ActionBar.TabListener ,IChordServiceListener,onConnected{

	public static final String Local = "local";
	public static final String Remote = "remote";
	public static final String View_Promotion_Action = "mobi.MobiSeeker.sPromotions.VIEW_PROMOTION";
	public static final String View_Remote_Promotion_Action = "mobi.MobiSeeker.sPromotions.VIEW_REMOTE_PROMOTION";
	public static String Add_New_Promotion_Action = "mobi.MobiSeeker.sPromotions.ADD_NEW_PROMOTION";
	public static String Delete_Local_Promotion_Action = "mobi.MobiSeeker.sPromotions.DELETE_LOCAL_PROMOTION";
	public static String View_local_Promotions_Action = "mobi.MobiSeeker.sPromotions.VIEW_LOCAL_PROMOTIONS";
	private final int REQ_CODE_PICK_IMAGE_SETTINS = 1;
	private final int REQ_CODE_PICK_IMAGE = 2;
	private static Context applicationContext;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
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
		setCurrentRoboActivity(this);
		applicationContext=this;
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
			intent.removeExtra("entry");
			setIntent(intent);
			this.addNewViewPromotion();
		} else if (intent.getAction().equalsIgnoreCase(
				Promotions.View_Promotion_Action)) {
			setIntent(intent);
			this.addViewPromotion();
		} else if (intent.getAction().equalsIgnoreCase(
				Promotions.View_local_Promotions_Action)) 
		{
			this.viewLocalPromotions();
		} else if (intent.getAction().equalsIgnoreCase(
				Promotions.View_Remote_Promotion_Action))
		{
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
	
	public void refreshRemoteList()
	{
		try{
		runNotification();	
		PromotionsList promotionsList = (PromotionsList) mSectionsPagerAdapter
				.instantiateItem(mViewPager, 0);
		promotionsList.PopulateList(this);
		}catch(Exception ee){ee.printStackTrace();}
		
	}

	public void pickLogo(View view) {
		this.pickImageFromGallery(REQ_CODE_PICK_IMAGE_SETTINS);
	}

	public void pickImage(View view) {
		this.pickImageFromGallery(REQ_CODE_PICK_IMAGE);
	}

	private void pickImageFromGallery(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		startActivityForResult(intent, requestCode);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch (requestCode) {
		case REQ_CODE_PICK_IMAGE:
		case REQ_CODE_PICK_IMAGE_SETTINS:
			if (resultCode != RESULT_OK) {
				return;
			}

			if (imageReturnedIntent == null) {
				return;
			}

			ImageView image = null;

			if (requestCode == REQ_CODE_PICK_IMAGE)
			{
				image = (ImageView) findViewById(R.id.image);
			} else 
			{
				image = (ImageView) findViewById(R.id.logo);
			}

			if (image == null) {
				return;
			}

			String imagePath = getImageFromGallery(imageReturnedIntent, image);

			image.setTag(imagePath);

			if (requestCode == REQ_CODE_PICK_IMAGE) {
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
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap bitmap=BitmapFactory.decodeFile(imagePath,options);
		
		if(bitmap.getWidth()>200 &&bitmap.getHeight()>200)
		{
		
			Toast.makeText(this, "Please select small image maximum width and height 200 X 200 pixel", 3000).show();
			bitmap.recycle();
			return null;
		}
		
		image.setImageBitmap(bitmap);
		
		return imagePath;
	}

	private void addNewViewPromotion() {
		mSectionsPagerAdapter.setPromotionsMode(FragmentMode.NEW);
		mSectionsPagerAdapter.notifyDataSetChanged();
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
	@SuppressLint("NewApi")
	public void runNotification()
	{
		try{
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.launcher)
		        .setContentTitle(currentSelectedEntry.getTitle())
		        .setContentText(currentSelectedEntry.getText());
		Intent resultIntent = new Intent(this, Promotions.class);

		PendingIntent pIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

		mBuilder.setContentIntent(pIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
		}catch(Exception ee){ee.printStackTrace();}
		
		
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
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
Entry currentSelectedEntry;
	@Override
	public void onReceiveMessage(String node, String channel, String message,
			String MessageType) {
		// TODO Auto-generated method stub
		
		try
		{
			nodeName=node;
		Repository Remoterepository = new Repository(Promotions.Remote);
		
		Entry selectedEntry=new Gson().fromJson(message, Entry.class);
		currentSelectedEntry=selectedEntry;
		if(selectedEntry.getImagePath()!=null)
		{
			
			selectedEntry.setImagePath(new ChordManagerService(null).getChordFilePath()+getImageName(selectedEntry.getImagePath()));	
								}
		if(selectedEntry.getLogo()!=null)
		{
		selectedEntry.setLogo(new ChordManagerService(null).getChordFilePath()+getImageName(selectedEntry.getLogo()));
		}

		
		Remoterepository.save(this,selectedEntry);
		if(selectedEntry.getLogo()==null&&selectedEntry.getImagePath()==null)
		{
			refreshRemoteList();
		}

		//Toast.makeText(this, message, 10000).show();
		}catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}

	@Override
	public void onFileWillReceive(String node, String channel, String fileName,
			String exchangeId) {
		// TODO Auto-generated method stub
		if(!new Settings(Promotions.this).isTextOnly())
		{
			
			manger.getmChordService().acceptFile(channel, exchangeId);
		}else
		{
			manger.getmChordService().rejectFile(channel, exchangeId);
			refreshRemoteList();
			
			
		}
		
		
		
	}

	@Override
	public void onFileProgress(boolean bSend, String node, String channel,
			int progress, String exchangeId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFileCompleted(int reason, String node, String channel,
			String exchangeId, String fileName) {
		
		refreshRemoteList();
		
	}

	@Override
	public void onNodeEvent(String node, String channel, boolean bJoined) {
		// TODO Auto-generated method stub
		System.out.println("On Node Event");
		onJoinedNode(node, channel, bJoined);
	}

	
	public void onJoinedNode(final String node, final String channel, boolean bJoined)
	{
		if(bJoined)
		{
			Thread thread=new Thread()
			{
				public void run()
				{
					try {
						repository = new Repository(Promotions.Local);
						ArrayList<Entry> entries=repository.getEntries(Promotions.this);
						
						if(entries!=null)
						{
							for(int i=0;i<entries.size();i++)
							{
								Entry selectedEntry=entries.get(i);
								Settings settings=new Settings(Promotions.this);
								if(settings.getUserName()!=null&&!settings.getUserName().equalsIgnoreCase("Guest"))
								selectedEntry.setUsername(settings.getUserName());
								String originalImagePath=selectedEntry.getImagePath();
								String originalLogoPath=selectedEntry.getLogo();
								
								manger.getmChordService().sendData(NodeManager.CHORD_API_CHANNEL, selectedEntry.toString().getBytes(), node, ConnectionConstant.ENTRY);
								if (originalImagePath != null&&!originalImagePath.isEmpty()) {
									manger.getmChordService().sendFile(NodeManager.CHORD_API_CHANNEL, originalImagePath, node);
								}
								
								if (originalLogoPath != null&&!originalLogoPath.isEmpty()) {
									manger.getmChordService().sendFile(NodeManager.CHORD_API_CHANNEL, originalLogoPath, node);
								}
								
								
							}
						}
					}catch(Exception ee){ee.printStackTrace();}
				}
			};
			thread.start();
		}
	}
	
	public String getImageName(String filePath)
	{
		try{
		int indexofbackslash=filePath.lastIndexOf("/");
		return filePath.substring(indexofbackslash);
		}catch(Exception ee)
		{
			return "";
		}
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

	public static Context getContext()
	{
		
		return applicationContext;
		
	}
}
