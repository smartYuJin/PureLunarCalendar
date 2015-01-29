package com.lingsmm.purelunarcalendar.module;

import java.util.Calendar;

import android.content.res.Resources;

import com.lingsmm.purelunarcalendar.R;

public class DateFormatter {

	private Resources resources;

	public DateFormatter(Resources resources) {
		this.resources = resources;
	}

	private String getArrayString(int resid, int index) {
		return resources.getStringArray(resid)[index];
	}

	public CharSequence getDayName(LunarCalendar lc) {
		StringBuilder result = new StringBuilder();
		int day = lc.getLunar(LunarCalendar.LUNAR_DAY);
		if (day < 11) {
			result.append(getArrayString(R.array.chinesePrefix, 0));
			result.append(getArrayString(R.array.chineseDigital, day));
		} else if (day < 20) {
			result.append(getArrayString(R.array.chinesePrefix, 1));
			result.append(getArrayString(R.array.chineseDigital, day - 10));
		} else if (day == 20) {
			result.append(getArrayString(R.array.chineseDigital, 2));
			result.append(getArrayString(R.array.chineseDigital, 10));
		} else if (day < 30) {
			result.append(getArrayString(R.array.chinesePrefix, 2));
			result.append(getArrayString(R.array.chineseDigital, day - 20));
		} else {
			result.append(getArrayString(R.array.chineseDigital, 3));
			result.append(getArrayString(R.array.chineseDigital, 10));
		}

		return result;
	}

	public CharSequence[] getFullDateInfo(LunarCalendar lc) {
		CharSequence[] result = new CharSequence[2];

		int year = lc.getGregorianDate(Calendar.YEAR);
		int month = lc.getGregorianDate(Calendar.MONTH);
		int day = lc.getGregorianDate(Calendar.DAY_OF_MONTH);

		int lYear, lMonth, lDay, solarTerm = -1;

		// 取年柱,以春分为分界点
		int st_spring = LunarCalendar.getSolarTerm(year, 3); // 立春
		if ((month == 1 && st_spring > day) || month < 1) {
			lYear = -1;
		} else {
			lYear = 0;
		}
		lYear = year - 1900 + lYear + 36;

		// 月柱,月柱以节令为界
		int st_monthFirst = (month == 1 ? st_spring : LunarCalendar
				.getSolarTerm(year, month * 2 + 1));
		lMonth = (st_monthFirst > day ? -1 : 0);
		lMonth = (year - 1900) * 12 + +month + lMonth + 13;

		// 日柱,单纯的日循环
		lDay = (int) ((lc.getTimeInMillis() - LunarCalendar.LUNAR_BASE_MILLIS) / LunarCalendar.DAY_MILLIS) + 40;

		// 节气
		if (st_monthFirst == day) {
			solarTerm = month * 2;
		} else if (day > 15) {
			int st2 = LunarCalendar.getSolarTerm(year, month * 2 + 2);
			if (st2 == day) {
				solarTerm = month * 2 + 1;
			}
		}

		// 节气,节日
		StringBuilder cs = new StringBuilder();
		if (solarTerm > -1) {
			cs.append(getSolarTermName(solarTerm));
			cs.append(' ');
		}
		int tmp = lc.getGregorianFestival();
		if (tmp > -1) {
			cs.append(getGregorianFestivalName(tmp));
			cs.append(' ');
		}
		tmp = lc.getLunarFestival();
		if (tmp > -1) {
			cs.append(getLunarFestivalName(tmp));
		}
		result[0] = cs;

		// 农历年月日信息
		cs = new StringBuilder();
		cs.append(getYearName(lc));
		cs.append(' ');
		cs.append(getMonthName(lc));
		cs.append(' ');
		cs.append(getDayName(lc));
		cs.append("  ");

		cs.append(getArrayString(R.array.chinese_gan, lYear % 10));
		cs.append(getArrayString(R.array.chinese_zhi, lYear % 12));
		cs.append(getArrayString(R.array.chineseTime, 0)); // 年
		cs.append(' ');
		cs.append(getArrayString(R.array.chinese_gan, lMonth % 10));
		cs.append(getArrayString(R.array.chinese_zhi, lMonth % 12));
		cs.append(getArrayString(R.array.chineseTime, 1)); // 月
		cs.append(' ');
		cs.append(getArrayString(R.array.chinese_gan, lDay % 10));
		cs.append(getArrayString(R.array.chinese_zhi, lDay % 12));
		cs.append(getArrayString(R.array.chineseTime, 2)); // 日
		result[1] = cs;

		return result;
	}

	public CharSequence getGregorianFestivalName(int index) {
		return getArrayString(R.array.gregorianFestivals, index);
	}

	public CharSequence getLunarFestivalName(int index) {
		return getArrayString(R.array.lunarFestivals, index);
	}

	public CharSequence getMonthName(LunarCalendar lc) {
		StringBuilder result = new StringBuilder();
		if (lc.getLunar(LunarCalendar.LUNAR_IS_LEAP) == 1) {
			result.append(getArrayString(R.array.chinesePrefix, 6));
		}
		int month = lc.getLunar(LunarCalendar.LUNAR_MONTH);
		switch (month) {
		case 1:
			result.append(getArrayString(R.array.chinesePrefix, 3));
			break;
		case 11:
			result.append(getArrayString(R.array.chinesePrefix, 4));
			break;
		case 12:
			result.append(getArrayString(R.array.chinesePrefix, 5));
			break;
		default:
			result.append(getArrayString(R.array.chineseDigital, month));
			break;
		}
		result.append(getArrayString(R.array.chineseTime, 1));
		return result;
	}

	public CharSequence getSolarTermName(int index) {
		return getArrayString(R.array.solarTerm, index);
	}

	public CharSequence getYearName(LunarCalendar lc) {
		StringBuilder result = new StringBuilder();
		int year = lc.getLunar(LunarCalendar.LUNAR_YEAR);
		int resid = R.array.chineseDigital;
		result.append(getArrayString(resid, (year / 1000) % 10));
		result.append(getArrayString(resid, (year / 100) % 10));
		result.append(getArrayString(resid, (year / 10) % 10));
		result.append(getArrayString(resid, year % 10));
		result.append(getArrayString(R.array.chineseTime, 0));

		return result;
	}

}
