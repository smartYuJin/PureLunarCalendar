package com.lingsmm.purelunarcalendar.ui.others;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.lingsmm.purelunarcalendar.R;

public class CalendarPagerFragment extends Fragment {
	
	public static final String ARG_PAGE = "page";

	private int mMonthIndex;
	
	public static CalendarPagerFragment create(int monthIndex){
		CalendarPagerFragment fragment = new CalendarPagerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, monthIndex);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMonthIndex = getArguments().getInt(ARG_PAGE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TableRow tableRow;
		View cellView;
		TableLayout tableView = (TableLayout) inflater.inflate(R.layout.view_calendar_table, container, false);
		CalendarTableCellProvider adpt = new CalendarTableCellProvider(getResources(), mMonthIndex);
		for(int row = 0; row < 6; row++){
			tableRow = new TableRow(tableView.getContext());
			for(int column = 0; column < 8; column++){
				cellView = adpt.getView(row * 8 + column, inflater, tableRow);
				cellView.setOnFocusChangeListener((View.OnFocusChangeListener)container.getContext());
				tableRow.addView(cellView);
			}
			tableView.addView(tableRow);
		}
		
		return tableView;
	}

}

