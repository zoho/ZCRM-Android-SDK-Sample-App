package com.zoho.testconfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zoho.crm.library.common.CommonUtil;
import com.zoho.crm.library.crud.ZCRMRecord;
import com.zoho.crm.library.exception.ZCRMException;
import com.zoho.crm.library.exception.ZCRMLogger;

import java.util.Iterator;

public class EventsListHandler extends ListViewHandler
{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return super.onCreateView(inflater,container,savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	public void recordList() throws ZCRMException
	{
		ZCRMRecord zcrmRecord;
		Iterator itr = ListViewAdapter.records.iterator();
		while (itr.hasNext())
		{
			zcrmRecord = (ZCRMRecord) itr.next();
			super.addRecordToList(zcrmRecord,new RecordListAdapter());
		}
		super.setPageRefreshingOff();
	}

	class RecordListAdapter extends ArrayAdapter<ZCRMRecord>
	{
		public RecordListAdapter()
		{
			super(getActivity(), R.layout.records_list, ListViewAdapter.storeList);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent)
        {
			if (view == null)
			{
				view = getActivity().getLayoutInflater().inflate(R.layout.records_list, parent, false);
			}
			ZCRMRecord record = (ZCRMRecord) ListViewAdapter.storeList.get(position);
			try
            {
				TextView name = (TextView) view.findViewById(R.id.textView4);
				name.setText((CharSequence) record.getFieldValue("Event_Title"));

                String venue, time;
                if (record.getFieldValue("Venue") == null)
                {
                    venue = "No Venue";
                }
                else
                {
                    venue = record.getFieldValue("Venue").toString();
                }

                if(record.getFieldValue("Start_DateTime") == null || record.getFieldValue("End_DateTime") == null)
                {
                    time = "No proper time";
                }
                else
                {
                    try
                    {
                        time = CommonUtil.isoStringToGMTTimestamp(record.getFieldValue("Start_DateTime").toString()) + "  TO  " + CommonUtil.isoStringToGMTTimestamp(record.getFieldValue("End_DateTime").toString());
                    }
                    catch (Exception ex)
                    {
                        ZCRMLogger.logError(ex);
                        time = record.getFieldValue("Start_DateTime") + " TO " + record.getFieldValue("End_DateTime");
                    }
                }
				TextView locationView = (TextView) view.findViewById(R.id.textView5);
				locationView.setText(venue);
				TextView timeView = (TextView) view.findViewById(R.id.textView6);
				timeView.setText(time);
			}
            catch (ZCRMException e)
            {
				e.printStackTrace();
			}
			return view;
		}
	}

}
