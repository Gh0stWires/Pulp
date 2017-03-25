package tk.samgrogan.pulp.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tk.samgrogan.pulp.Models.DrawerItem;
import tk.samgrogan.pulp.R;

/**
 * Created by ghost on 3/24/2017.
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {
    private List<DrawerItem> drawerItemList = new ArrayList<>();
    private OnItemSelectedListener mListener;

    public DrawerAdapter(List<DrawerItem> drawerItemList){
        this.drawerItemList = drawerItemList;
    }

    public void setOnItemClickListener(OnItemSelectedListener listener){
        this.mListener = listener;
    }

    public interface OnItemSelectedListener{
        public void onItemSelected(View view, int position);
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item,parent,false);
        return new DrawerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        holder.title.setText(drawerItemList.get(position).getmTitle());
        holder.icon.setImageResource(drawerItemList.get(position).getmIcon());
    }

    @Override
    public int getItemCount() {
        return drawerItemList.size();
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemSelected(v, getAdapterPosition());
                }
            });
        }
    }
}
