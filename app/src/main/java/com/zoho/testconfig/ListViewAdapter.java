package com.zoho.testconfig;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.zoho.crm.library.crud.ZCRMRecord;
import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter {
    public static ArrayAdapter<ZCRMRecord> adapter;
    public static ListView RecordList;
    public static List records = new ArrayList();
    public static List storeList = new ArrayList();
    public static String moduleAPIname;
    public static long idClicked;
    public static String nameClicked;

    public static void setContactList(ListView contactList) {
        RecordList = contactList;
    }

    public static ListView getContactList() {
        return RecordList;
    }

    public static ArrayAdapter getAdapter() {
        return adapter;
    }
}
