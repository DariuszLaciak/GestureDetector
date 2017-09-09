package pl.edu.uj.laciak.gesturedetector.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pl.edu.uj.laciak.gesturedetector.R;

/**
 * Created by darek on 02.09.17.
 */

public class GridViewAdapter extends ArrayAdapter<DrawingItem> {
    Context mContext;
    int resourceId;
    ArrayList<DrawingItem> data = new ArrayList<DrawingItem>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<DrawingItem> data) {
        super(context, layoutResourceId, data);
        this.mContext = context;
        this.resourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder = null;

        if (itemView == null) {
            final LayoutInflater layoutInflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = layoutInflater.inflate(resourceId, parent, false);

            holder = new ViewHolder();
            holder.imgItem = itemView.findViewById(R.id.imgItem);
            holder.txtItem = itemView.findViewById(R.id.txtItem);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        DrawingItem item = getItem(position);
        holder.imgItem.setImageBitmap(item.getImage());
        holder.txtItem.setText(item.getTitle());

        return itemView;
    }

    static class ViewHolder {
        ImageView imgItem;
        TextView txtItem;
    }

}