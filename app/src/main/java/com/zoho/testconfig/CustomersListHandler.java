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

public class CustomersListHandler extends ListViewHandler
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
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
			{
				view = getActivity().getLayoutInflater().inflate(R.layout.records_list, parent, false);
			}
			ZCRMRecord record = (ZCRMRecord) ListViewAdapter.storeList.get(position);
			try
            {
				TextView name = (TextView) view.findViewById(R.id.textView4);
                String fullName = "";
                if(record.getFieldValue("First_Name") != null)
                {
                    fullName += record.getFieldValue("First_Name") + " ";
                }
                fullName += record.getFieldValue("Last_Name");
				name.setText(fullName);

                String email, mobile;
                if(record.getFieldValue("Email") == null)
                {
                    email = "No Email";
                }
                else
                {
                    email = record.getFieldValue("Email").toString();
                }
                if(record.getFieldValue("Mobile") == null)
                {
                    mobile = "No Mobile";
                }
                else
                {
                    mobile = record.getFieldValue("Mobile").toString();
                }
                TextView phone = (TextView) view.findViewById(R.id.textView5);
				phone.setText(mobile);
				TextView emailView = (TextView) view.findViewById(R.id.textView6);
				emailView.setText(email);
			} catch (ZCRMException e) {
				e.printStackTrace();
			}

			return view;
		}
	}

}
