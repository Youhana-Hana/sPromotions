package mobi.MobiSeeker.sPromotions.activites;

import mobi.MobiSeeker.sPromotions.R;
import mobi.MobiSeeker.sPromotions.data.Adapter;
import mobi.MobiSeeker.sPromotions.data.Entry;
import mobi.MobiSeeker.sPromotions.data.Repository;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

public class PromotedList extends ListFragment {

	protected Repository repository;
	protected Adapter adapter;
	ImageView entryAdd = null;
	ImageView clear = null;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.repository = new Repository(Promotions.Local);
			PopulateList();  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.promotelist, container, false);

		this.entryAdd = (ImageView) rootView.findViewById(R.id.entryAdd);
		this.entryAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Brodcast(null, Promotions.Add_New_Promotion_Action);
			}
		});

		this.clear = (ImageView) rootView.findViewById(R.id.clear_local);
		this.clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirmClear();
			}
		});
		
		return rootView;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Entry entry = adapter.getItem(position);
		Brodcast(entry, Promotions.View_Promotion_Action);
	}

	private void Brodcast(Entry entry, String action) {
		Context context = getActivity().getBaseContext();
		Intent intent = new Intent(action);
		if(entry != null) {
			intent.putExtra("entry", entry);
		}
		
		context.sendBroadcast(intent);
	}

	 public void confirmClear() {
	     	AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
	        alertDialog.setTitle(this.getResources().getString(R.string.clear_alert_title));
	        alertDialog.setMessage(this.getResources().getString(R.string.clear_alert_text));
	        alertDialog.setPositiveButton(this.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                clear();
	            }
	        });

	        alertDialog.setNegativeButton(this.getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            }
	        });

	        alertDialog.create().show();
	    }
	 
	protected void clear() {
		try {
			this.repository.clear(getActivity().getBaseContext());
			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(Entry entry) {
		try {
			Context context = getActivity().getBaseContext();
			this.repository.delete(context, entry);
			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.adapter = new Adapter(context, R.layout.entry,
				repository.getEntries(context));
		setListAdapter(this.adapter);
	}
}
