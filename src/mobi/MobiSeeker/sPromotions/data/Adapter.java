package mobi.MobiSeeker.sPromotions.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import mobi.MobiSeeker.sPromotions.R;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public class Adapter extends ArrayAdapter<Entry> {

    static class ViewHolder {
        public TextView title;
        public TextView timesPerDay;
        public TextView start;
        public TextView end;
        public Button share;
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
 /*         viewHolder.title = (TextView) rowView.findViewById(R.id.title);
            viewHolder.timesPerDay = (TextView) rowView.findViewById(R.id.timesPerDay);
            viewHolder.start = (TextView) rowView.findViewById(R.id.start);
            viewHolder.end = (TextView) rowView.findViewById(R.id.end);
            viewHolder.share = (Button) rowView.findViewById(R.id.share);
            viewHolder.delete = (Button) rowView.findViewById(R.id.delete);
*/
            rowView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        Entry entry = this.entries.get(position);

        viewHolder.title.setText(entry.getMedicineName());
        String timesPreDay = this.getTimesPerDay(entry);
        viewHolder.timesPerDay.setText(timesPreDay);
        viewHolder.start.setText(getStartTime(entry));
        viewHolder.end.setText(getEndTime(entry));
        viewHolder.share.setTag(entry);
        viewHolder.delete.setTag(entry);
        return rowView;
    }

    private String getTimesPerDay(Entry entry) {
        return "";//String.format(Locale.getDefault(), "%d %s", entry.getTimesPerDay(), this.context.getString(R.string.timesPerDay));
    }

    private String getStartTime(Entry entry) {
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(entry.getStartDate());

        return String.format(Locale.getDefault(), "%s %s",
                date,
                entry.getStartTime());
    }

    private String getEndTime(Entry entry) {
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(entry.getEndDate());

        return String.format(Locale.getDefault(), "%s %s",
                date,
                entry.getEndTime());
    }
}
