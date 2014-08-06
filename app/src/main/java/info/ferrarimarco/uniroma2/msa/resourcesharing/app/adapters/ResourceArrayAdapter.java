package info.ferrarimarco.uniroma2.msa.resourcesharing.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.R;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.model.Resource;

public class ResourceArrayAdapter extends ArrayAdapter<Resource> {

    private Context context;
    private int layoutResourceId;

    public ResourceArrayAdapter(Context context, int layoutResourceId) {
        this(context, layoutResourceId, new ArrayList<Resource>());
    }

    public ResourceArrayAdapter(Context context, int layoutResourceId, List<Resource> resources) {
        super(context, layoutResourceId, resources);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
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
        resourceTitleTextView.setTag(resource.getId());

        TextView resourceDescriptionTextView = (TextView) convertView.findViewById(R.id.resource_description_text_view);
        resourceDescriptionTextView.setText(resource.getDescription());
        resourceDescriptionTextView.setTag(resource.getId());

        return convertView;

    }
}
