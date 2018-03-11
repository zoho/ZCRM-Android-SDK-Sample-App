package com.zoho.sample_app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;

public class HomePageActivity extends Fragment
{
	TextClock clock;
	android.widget.CalendarView calendar;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.home_calendar, container, false);
		loadFragements(rootView);
		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	public void loadFragements(View view)
	{
		clock = (TextClock) view.findViewById(R.id.textClock);
		calendar = (android.widget.CalendarView) view.findViewById(R.id.calendarView);
	}
}
