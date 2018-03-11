package com.zoho.sample_app;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zoho.crm.library.crud.ZCRMModule;
import com.zoho.crm.library.crud.ZCRMRecord;
import com.zoho.crm.library.exception.ZCRMException;
import com.zoho.crm.library.setup.restclient.ZCRMRestClient;

import java.util.List;

public class ListViewHandler extends Fragment {

    public View view;
    ProgressBar mProgress;
    SwipeRefreshLayout refreshLayout;
    TextView emptylist;
    TextView loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.module_tab, container, false);
        view = rootView;
        emptylist = (TextView) view.findViewById(R.id.textView32);
        emptylist.setText("");
        loading = (TextView) view.findViewById(R.id.loading);
        initiatePage(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void initiatePage(View viewArg)
    {
        initiateList(viewArg);

        refreshLayout = (SwipeRefreshLayout) viewArg.findViewById(R.id.modulerefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                clearList();
                ApiModeRunner runner = new ApiModeRunner();
                runner.execute();
            }
        });

        mProgress = (ProgressBar) viewArg.findViewById(R.id.moduleprogress);
        mProgress.setVisibility(ProgressBar.VISIBLE);
        loading.setText("LOADING.. please wait."); //No I18N

        ApiModeRunner runner = new ApiModeRunner();
        runner.execute();
    }

    protected void setPageRefreshingOff()
    {
        refreshLayout.setRefreshing(false);
        mProgress.setVisibility(ProgressBar.INVISIBLE);
        loading.setText("");

        if(ListViewAdapter.records.isEmpty())
        {
            emptylist.setText("Seems you have no records..");
        }
    }

    public void initiateList(View viewArg)
    {
        ListViewAdapter.setContactList((ListView) viewArg.findViewById(R.id.listView));
        ListViewAdapter.getContactList().setAdapter(ListViewAdapter.getAdapter());
        ListViewAdapter.records.clear();
        ListViewAdapter.storeList.clear();

        ListViewAdapter.RecordList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView adapterView, View view, final int position, long idy)
            {
                ZCRMRecord record = (ZCRMRecord) ListViewAdapter.storeList.get(position);
                ListViewAdapter.idClicked = record.getEntityId();
                try
                {
                    if(record.getModuleAPIName().equals("Calls"))
                    {
                        ListViewAdapter.nameClicked = String.valueOf(record.getFieldValue("Subject"));
                    }
                    else if(record.getModuleAPIName().equals("Tasks"))
                    {
                        ListViewAdapter.nameClicked = String.valueOf(record.getFieldValue("Subject"));
                    }
                    else if(record.getModuleAPIName().equals("Events"))
                    {
                        ListViewAdapter.nameClicked = String.valueOf(record.getFieldValue("Event_Title"));
                    }
                    else if(record.getModuleAPIName().equals("Contacts"))
                    {
                        String fullName = "";
                        if(record.getFieldValue("First_Name") != null)
                        {
                            fullName += record.getFieldValue("First_Name") + " ";
                        }
                        fullName += record.getFieldValue("Last_Name");
                        ListViewAdapter.nameClicked = fullName;
                    }
                    Intent intent = new Intent(getActivity().getApplicationContext(), RecordViewHandler.class);
                    startActivity(intent);
                }
                catch (ZCRMException e)
                {
                    e.printStackTrace();
                }
            }
        });

    }

    public void clearList()
    {
        ListViewAdapter.records.clear();
        ListViewAdapter.storeList.clear();
    }

    public void addRecordToList(ZCRMRecord zcrmRecord, Object recordListHandler)
    {
        ListViewAdapter.storeList.add(zcrmRecord);
        ListViewAdapter.adapter = (ArrayAdapter<ZCRMRecord>) recordListHandler;
        ListViewAdapter.RecordList.setAdapter(ListViewAdapter.adapter);
    }

    protected void recordList() throws ZCRMException {

    }

    class ApiModeRunner extends AsyncTask<String, String, String> {
        private String resp;


        @Override
        protected String doInBackground(String... params) {
            try {
                ZCRMModule module = ZCRMRestClient.getInstance().getModuleInstance(ListViewAdapter.moduleAPIname);
                ListViewAdapter.records = (List<ZCRMRecord>) module.getRecords().getData();
                resp = "success";
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                recordList();
            } catch (ZCRMException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }
    }
}
