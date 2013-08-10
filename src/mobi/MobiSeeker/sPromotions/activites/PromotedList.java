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

public class PromotedList extends ListFragment {

	protected Repository repository;
	protected Adapter adapter;
	Button entryAdd = null;

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

	private void PopulateList() throws Exception {
		Context context = getActivity().getBaseContext();
		this.adapter = new Adapter(context, 0,
				repository.getEntries(context));
		setListAdapter(this.adapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.promotelist, container, false);

		this.entryAdd = (Button) rootView.findViewById(R.id.entryAdd);
		this.entryAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Context context = getActivity().getBaseContext();
				Intent intent = new Intent(Promotions.Add_New_Promotion_Action);
				context.sendBroadcast(intent);
			}
		});

		return rootView;
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
}
