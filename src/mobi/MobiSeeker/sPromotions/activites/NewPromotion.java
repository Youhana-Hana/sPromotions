package mobi.MobiSeeker.sPromotions.activites;

import mobi.MobiSeeker.sPromotions.R;
import mobi.MobiSeeker.sPromotions.data.Entry;
import mobi.MobiSeeker.sPromotions.data.Repository;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class NewPromotion extends Fragment {

    Button entrySave = null;
    Button entrySaveCancel = null;
    EditText  entryTitle = null;
    EditText  entrySummary = null;
    ImageView  entryImage = null;
    
 	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.newpromotion, container, false);
	
		this.entryTitle = (EditText) rootView.findViewById(R.id.editTitle);
		this.entrySummary = (EditText) rootView.findViewById(R.id.editText);
		this.entryImage = (ImageView) rootView.findViewById(R.id.image);
		
		this.entrySave = (Button) rootView.findViewById(R.id.entrySave);
		this.entrySaveCancel = (Button) rootView.findViewById(R.id.entrySaveCancel);
	
		this.entrySave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
				goHome();
			}
		});
		
		this.entrySaveCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goHome();
			}
		});
		
		return rootView;
	}
	
 	void save() {
 		Repository repository = new Repository(Promotions.Local);
 		Context context = getActivity().getBaseContext();
 		try {
			repository.save(context, getEntry(context));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	}
 	
	private Entry getEntry(Context context) {
		
		String  title = this.entryTitle.getText().toString();
		String  content = this.entrySummary.getText().toString();
		
		String image = null;
		if (this.entryImage.getTag() != null)
		{
			image = (String)this.entryImage.getTag();
		}
		
		String  logo = new mobi.MobiSeeker.sPromotions.data.Settings(context).getLogo();
		
		return new Entry(title, content, logo, image);
	}

	void goHome() {
		Context context = getActivity().getBaseContext();
		Intent intent = new Intent(Promotions.View_local_Promotions_Action);
		context.sendBroadcast(intent);
	}
}
