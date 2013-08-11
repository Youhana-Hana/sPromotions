package mobi.MobiSeeker.sPromotions.activites;

import mobi.MobiSeeker.sPromotions.R;
import mobi.MobiSeeker.sPromotions.data.Adapter;
import mobi.MobiSeeker.sPromotions.data.Entry;
import mobi.MobiSeeker.sPromotions.data.Repository;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

public class PromotionsList extends ListFragment {

	protected Repository repository;
	protected Adapter adapter;
	Button clear = null;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.repository = new Repository(Promotions.Remote);
			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.promotions_list, container, false);

		this.clear = (Button) rootView.findViewById(R.id.clear);
		this.clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clear();
			}
		});

		return rootView;
	}

	protected void clear() {
		try {
			this.repository.clear(getActivity().getBaseContext());
			PopulateList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Entry entry = adapter.getItem(position);
		Brodcast(entry, Promotions.View_Remote_Promotion_Action);
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
	
	private void Brodcast(Entry entry, String action) {
		Context context = getActivity().getBaseContext();
		Intent intent = new Intent(action);
		if(entry != null) {
			intent.putExtra("entry", entry);
		}
		
		context.sendBroadcast(intent);
	}

	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.adapter = new Adapter(context, R.layout.remote_entry,
				repository.getEntries(context));
		setListAdapter(this.adapter);
	}
}
