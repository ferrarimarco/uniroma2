package info.ferrarimarco.uniroma2.msa.resourcesharing.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import javax.inject.Inject;

import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

public class ResourceArrayAdapter extends ArrayAdapter<Resource> {

    private Context context;
    private int layoutResourceId;

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.fullDateTime();

    @Inject
    public ResourceArrayAdapter(Context context) {
        super(context, R.layout.resource_row_list, new ArrayList<Resource>());
        this.context = context;
        this.layoutResourceId = R.layout.resource_row_list;
        this.setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */
        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }

        // object item based on the position
        Resource resource = this.getItem(position);

        TextView resourceTitleTextView = (TextView) convertView.findViewById(R.id.resource_title_text_view);
        resourceTitleTextView.setText(resource.getTitle());
        resourceTitleTextView.setTag(resource.getAndroidId());

        TextView resourceDescriptionTextView = (TextView) convertView.findViewById(R.id.resource_description_text_view);
        resourceDescriptionTextView.setText(resource.getDescription());
        resourceDescriptionTextView.setTag(resource.getAndroidId());

        TextView resourceAcquisitionModeTextView = (TextView) convertView.findViewById(R.id.resource_acquisition_mode_text_view);
        resourceAcquisitionModeTextView.setText(resource.getAcquisitionMode());
        resourceAcquisitionModeTextView.setTag(resource.getAndroidId());

        TextView resourceTtlTextView = (TextView) convertView.findViewById(R.id.resource_ttl_text_view);
        DateTime ttl = new DateTime(resource.getTimeToLive());
        resourceTtlTextView.setText(ttl.toString(dateTimeFormatter));
        resourceTtlTextView.setTag(resource.getAndroidId());

        return convertView;

    }
}
