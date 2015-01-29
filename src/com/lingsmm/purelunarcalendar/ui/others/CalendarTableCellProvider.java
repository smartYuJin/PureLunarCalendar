package com.lingsmm.purelunarcalendar.ui.others;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingsmm.purelunarcalendar.R;
import com.lingsmm.purelunarcalendar.module.DateFormatter;
import com.lingsmm.purelunarcalendar.module.LunarCalendar;

public class CalendarTableCellProvider {

	private long firstDayMillis = 0;
	private int solarTerm1 = 0;
	private int solarTerm2 = 0;
	private DateFormatter fomatter;
	
	public CalendarTableCellProvider(Resources resources, int monthIndex){
		int year = LunarCalendar.getMinYear() + (monthIndex / 12);
		int month = monthIndex % 12;
		Calendar date = new GregorianCalendar(year, month, 1);
		int offset = 1 - date.get(Calendar.DAY_OF_WEEK);
		date.add(Calendar.DAY_OF_MONTH, offset);
		firstDayMillis = date.getTimeInMillis();
		solarTerm1 = LunarCalendar.getSolarTerm(year, month * 2 + 1);
		solarTerm2 = LunarCalendar.getSolarTerm(year, month * 2 + 2);
		fomatter = new DateFormatter(resources);
	}
	
	public View getView(int position, LayoutInflater inflater, ViewGroup container) {
		ViewGroup rootView;
		LunarCalendar date = new LunarCalendar(firstDayMillis + 
				(position - (position / 8) - 1) * LunarCalendar.DAY_MILLIS);
		// 周的年序号
		if (position % 8 == 0){
			rootView = (ViewGroup) inflater.inflate(R.layout.view_calendar_week_index, container, false);

			TextView txtWeekIndex = (TextView)rootView.findViewById(R.id.txtWeekIndex);
			txtWeekIndex.setText(String.valueOf(date.getGregorianDate(Calendar.WEEK_OF_YEAR)));

			return rootView;
		}

		// 开始日期处理
		boolean isFestival = false, isSolarTerm = false;
		rootView = (ViewGroup) inflater.inflate(R.layout.view_calendar_day_cell, container, false);
		TextView txtCellGregorian = (TextView)rootView.findViewById(R.id.txtCellGregorian);
		TextView txtCellLunar = (TextView)rootView.findViewById(R.id.txtCellLunar);
		
		int gregorianDay = date.getGregorianDate(Calendar.DAY_OF_MONTH);
		// 判断是否为本月日期
		boolean isOutOfRange = ((position % 8 != 0) && 
				(position < 8 && gregorianDay > 7) || (position > 8 && gregorianDay < position - 7 - 6));
		txtCellGregorian.setText(String.valueOf(gregorianDay));

		// 农历节日 > 公历节日 > 农历月份 > 二十四节气 > 农历日
		int index = date.getLunarFestival();
		if (index >= 0){
			// 农历节日
			txtCellLunar.setText(fomatter.getLunarFestivalName(index));
			isFestival = true;
		}else{
			index = date.getGregorianFestival();
			if (index >= 0){
				// 公历节日
				txtCellLunar.setText(fomatter.getGregorianFestivalName(index));
				isFestival = true;
			}else if (date.getLunar(LunarCalendar.LUNAR_DAY) == 1){
				// 初一,显示月份
				txtCellLunar.setText(fomatter.getMonthName(date));
			}else if(!isOutOfRange && gregorianDay == solarTerm1){
				// 节气1
				txtCellLunar.setText(fomatter.getSolarTermName(date.getGregorianDate(Calendar.MONTH) * 2));
				isSolarTerm = true;
			}else if(!isOutOfRange && gregorianDay == solarTerm2){
				// 节气2
				txtCellLunar.setText(fomatter.getSolarTermName(date.getGregorianDate(Calendar.MONTH) * 2 + 1));
				isSolarTerm = true;
			}else{
				txtCellLunar.setText(fomatter.getDayName(date));
			}
		}
		
		// set style
		Resources resources = container.getResources();
		if (isOutOfRange){
			rootView.setBackgroundResource(R.drawable.shape_calendar_cell_outrange);
			txtCellGregorian.setTextAppearance(txtCellGregorian.getContext(), R.style.style_calendar_gregorian_outrange);
			txtCellLunar.setTextAppearance(txtCellLunar.getContext(), R.style.style_calendar_lunar_outrange);
		}else if(isFestival){
			txtCellLunar.setTextColor(resources.getColor(R.color.color_calendar_festival));
		}else if(isSolarTerm){
			txtCellLunar.setTextColor(resources.getColor(R.color.color_calendar_solarterm));
		}
		if (position % 8 == 1 || position % 8 == 7){
			rootView.setBackgroundResource(R.drawable.shape_calendar_cell_weekend);
		}
		if (date.isToday()){
			ImageView imgView = (ImageView) rootView.findViewById(R.id.imgCellHint);
			imgView.setBackgroundResource(R.drawable.img_hint_today);
			rootView.setBackgroundResource(R.drawable.shape_calendar_cell_today);
		}
		
		// store date into tag
		rootView.setTag(date);
		
		return rootView;
	}

}

