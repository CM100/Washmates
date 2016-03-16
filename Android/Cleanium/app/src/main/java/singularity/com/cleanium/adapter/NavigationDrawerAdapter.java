package singularity.com.cleanium.adapter;

import android.content.Context;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.singularity.cleanium.R;

import singularity.com.cleanium.utils.Utils;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
	private String[] navTitles;
	private int[] icons, activeIcons;
	private int activePosition;

	private static final int TYPE_HEADER = 0;
	private static final int TYPE_ITEM = 1;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private TextView itemTitle;
		private ImageView headerIcon;
        private View wrapper;

		public ViewHolder(View itemView, int ViewType) {
			super(itemView);

            wrapper = itemView;
			if(ViewType == TYPE_ITEM) {
				itemTitle = (TextView) itemView.findViewById(R.id.item_title);
			}else{
				headerIcon = (ImageView) itemView.findViewById(R.id.headerIcon);
			}
		}
	}

	public NavigationDrawerAdapter(String[] titles, int[] icons, int[] activeIcons, int activePosition) {
		this.navTitles = titles;
		this.icons = icons;
        this.activeIcons = activeIcons;
        this.activePosition = activePosition;
	}

	@Override
	public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if(viewType == TYPE_ITEM){
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_drawer_row, parent, false);
			ViewHolder vhItem = new ViewHolder(v, viewType);
			return vhItem;
		}else if(viewType == TYPE_HEADER){
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_drawer_header_icon, parent, false);
			ViewHolder vhHeaderItem = new ViewHolder(v, viewType);
			return vhHeaderItem;
		}
		return null;
	}

	@Override
	public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder holder, int position) {
		if(getItemViewType(position) == TYPE_ITEM){
			holder.itemTitle.setText(this.navTitles[position]);

            if (activePosition != position) {
                holder.itemTitle.setCompoundDrawablesWithIntrinsicBounds(this.icons[position], 0, 0, 0);
                holder.itemTitle.setTextColor(Color.WHITE);
                holder.wrapper.setBackgroundColor(Color.parseColor("#68badb"));
            } else {
                holder.itemTitle.setCompoundDrawablesWithIntrinsicBounds(this.activeIcons[position], 0, 0, 0);
                holder.itemTitle.setTextColor(Color.parseColor("#68badb"));
                holder.wrapper.setBackgroundColor(Color.WHITE);
            }

		}else{
			holder.headerIcon.setImageResource(R.drawable.app_icon_white);
		}
	}

	@Override
	public int getItemCount() {
		return navTitles.length;
	}

	@Override
	public int getItemViewType(int position) {
		if (isPositionHeader(position))
			return TYPE_HEADER;

		return TYPE_ITEM;
	}

	private boolean isPositionHeader(int position) {
		return position == 0;
	}


}