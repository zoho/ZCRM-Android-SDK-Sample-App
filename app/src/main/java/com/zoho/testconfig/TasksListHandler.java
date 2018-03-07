package com.zoho.testconfig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.zoho.crm.library.crud.ZCRMRecord;
import com.zoho.crm.library.exception.ZCRMException;
import java.util.Iterator;

public class TasksListHandler extends ListViewHandler
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
				name.setText((CharSequence) record.getFieldValue("Subject"));
                String duedate, priority;
                if(record.getFieldValue("Due_Date") == null)
                {
                    duedate = "No Due Date";
                }
                else
                {
                    duedate = record.getFieldValue("Due_Date").toString();
                }
                if(record.getFieldValue("Priority") == null)
                {
                    priority = "No Priority";
                }
                else
                {
                    priority = record.getFieldValue("Priority").toString();
                }
                TextView dueDateView = (TextView) view.findViewById(R.id.textView5);
				dueDateView.setText(duedate);
				TextView priorityView = (TextView) view.findViewById(R.id.textView6);
				priorityView.setText(priority);
			}
            catch (ZCRMException e)
            {
				e.printStackTrace();
			}

			return view;
		}
	}

}
