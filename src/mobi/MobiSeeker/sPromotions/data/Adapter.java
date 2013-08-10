package mobi.MobiSeeker.sPromotions.data;

import java.util.List;

import mobi.MobiSeeker.sPromotions.R;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter extends ArrayAdapter<Entry> {

    static class ViewHolder {
        public TextView title;
        public TextView text;
        public ImageView image;
        public Button delete;
    }

    protected Context context;

    protected List<Entry> entries;

    public Adapter(Context context, int textViewResourceId, List<Entry> entries) {
        super(context, textViewResourceId, entries);
        this.context = context;
        this.entries = entries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.entry, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.entryTitle);
            viewHolder.text = (TextView) rowView.findViewById(R.id.entrySummary);
            viewHolder.image = (ImageView) rowView.findViewById(R.id.entryLogo);
            viewHolder.delete = (Button) rowView.findViewById(R.id.entryDelete);

            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        Entry entry = this.entries.get(position);

        viewHolder.title.setText(entry.getTitle());
        viewHolder.text.setText(entry.getText());
        viewHolder.image.setImageURI(Uri.parse(entry.getImagePath()));
        viewHolder.delete.setTag(entry);
        return rowView;
    }
 }
