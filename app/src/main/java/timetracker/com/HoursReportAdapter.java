package timetracker.com;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Recycler view adapter for presenting work hours table
 */
public class HoursReportAdapter extends RecyclerView.Adapter<HoursReportAdapter.ViewHolder> {

	private ArrayList<WorkEntry> workHoursList;
	private int bgColorGrey;

	public HoursReportAdapter(ArrayList<WorkEntry> items, Context context) {
		this.workHoursList = items;
		bgColorGrey = context.getResources().getColor(R.color.tableItemBGColor);
	}

	@Override
	public HoursReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
												   int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.hours_report_row, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {

		holder.textViewId.setText((position + 1) + "");
		holder.textViewCheckIn.setText(workHoursList.get(position).getCheckinTimeStr());
		holder.textViewCheckOut.setText(workHoursList.get(position).getCheckoutTimeStr());
		holder.textViewTotal.setText(workHoursList.get(position).getTotalWorkTimeStr());
		if (position % 2 == 0){
			holder.bgLayout.setBackgroundColor(bgColorGrey);
		}
		else
		{
			holder.bgLayout.setBackgroundColor(Color.WHITE);
		}
	}

	@Override
	public int getItemCount() {
		if (workHoursList == null){
			return 0;
		}
		return workHoursList.size();
	}

	public void setWorkHoursList(ArrayList<WorkEntry> newSearchResults){
		workHoursList = newSearchResults;
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView textViewId;
		public TextView textViewCheckIn;
		public TextView textViewCheckOut;
		public TextView textViewTotal;
		public LinearLayout bgLayout;

		public ViewHolder(View view) {
			super(view);
			textViewId = view.findViewById(R.id.textViewId);
			textViewCheckIn = view.findViewById(R.id.textViewCheckIn);
			textViewCheckIn = view.findViewById(R.id.textViewCheckIn);
			textViewCheckOut = view.findViewById(R.id.textViewCheckOut);
			textViewTotal = view.findViewById(R.id.textViewTotal);
			bgLayout = view.findViewById(R.id.linearLayoutBG);

		}
	}

}
