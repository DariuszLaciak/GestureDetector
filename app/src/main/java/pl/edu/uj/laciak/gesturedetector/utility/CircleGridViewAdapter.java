package pl.edu.uj.laciak.gesturedetector.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import pl.edu.uj.laciak.gesturedetector.R;

/**
 * Created by darek on 05.09.17.
 */

public class CircleGridViewAdapter extends ArrayAdapter<CircleItem> {
    Context mContext;
    int resourceId;
    ArrayList<CircleItem> data = new ArrayList<CircleItem>();

    public CircleGridViewAdapter(Context context, int layoutResourceId, ArrayList<CircleItem> data) {
        super(context, layoutResourceId, data);
        this.mContext = context;
        this.resourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        CircleGridViewAdapter.ViewHolder holder = null;

        if (itemView == null) {
            final LayoutInflater layoutInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(resourceId, parent, false);

            holder = new CircleGridViewAdapter.ViewHolder();
            holder.imgItem = itemView.findViewById(R.id.circleItem);
            itemView.setTag(holder);
        } else {
            holder = (CircleGridViewAdapter.ViewHolder) itemView.getTag();
        }

        CircleItem item = getItem(position);
        holder.imgItem.setImageResource(R.drawable.circle);
        holder.imgItem.setId(item.getId());

        return itemView;
    }

    static class ViewHolder {
        ImageView imgItem;
    }
}
