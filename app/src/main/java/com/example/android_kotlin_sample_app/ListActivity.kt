package com.example.android_kotlin_sample_app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zoho.crm.sdk.android.api.handler.DataCallback
import com.zoho.crm.sdk.android.api.response.BulkAPIResponse
import com.zoho.crm.sdk.android.common.CommonUtil
import com.zoho.crm.sdk.android.crud.ZCRMQuery
import com.zoho.crm.sdk.android.crud.ZCRMRecord
import com.zoho.crm.sdk.android.exception.ZCRMException
import com.zoho.crm.sdk.android.exception.ZCRMLogger
import com.zoho.crm.sdk.android.setup.sdkUtil.ZCRMSDKUtil
import kotlinx.android.synthetic.main.list_activity.*

class ListActivity : AppCompatActivity() {
    var emptyList: TextView? = null
    var loading: TextView? = null
    var mProgress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.module_tab)
        moduleApiName = getIntent().getStringExtra("module")
        getSupportActionBar()?.setTitle(moduleApiName)
        emptyList = findViewById(R.id.textView32) as TextView?
        emptyList!!.text = ""
        loading = findViewById(R.id.loading) as TextView?
        initiatePage()
    }

    fun initiatePage() {
        initiateList()

        mProgress = findViewById(R.id.moduleprogress)
        mProgress!!.visibility = ProgressBar.VISIBLE

        loading!!.text = "LOADING.. please wait." //No I18N
        loadRecords()
    }

    fun initiateList() {
        val listView = findViewById(R.id.listView) as ListView
        adapter = RecordListAdapter()
        listView.adapter = adapter
        clearList()
        listView.onItemClickListener = OnItemClickListener { adapterView, view, position, idy ->
            //load record's detail page here
        }
    }

    internal inner class RecordListAdapter : ArrayAdapter<ZCRMRecord>(
        applicationContext,
        R.layout.list_item,
        storeList as List<ZCRMRecord>
    ) {
        override fun getView(
            position: Int,
            view: View?,
            parent: ViewGroup
        ): View {
            var view = view
            if (view == null) {
                val inflater =
                    getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                view = inflater.inflate(R.layout.list_item, parent, false)
            }
            val record: ZCRMRecord =
                storeList[position]
            try {
                val primaryField = view!!.findViewById<TextView>(R.id.textView4)
                val secondaryField = view.findViewById<TextView>(R.id.textView5)
                val tertiaryField = view.findViewById<TextView>(R.id.textView6)
                var primaryFieldValue = ""
                var secondaryFieldValue = ""
                var tertiaryFieldValue = ""
                if (moduleApiName == "Contacts") {
                    if (record.getFieldValue("First_Name") != null) {
                        primaryFieldValue += record.getFieldValue("First_Name").toString() + " "
                    }
                    primaryFieldValue += record.getFieldValue("Last_Name")
                    secondaryFieldValue = if (record.getFieldValue("Email") == null) {
                        "No Email"
                    } else {
                        record.getFieldValue("Email").toString()
                    }
                    tertiaryFieldValue = if (record.getFieldValue("Phone") == null) {
                        "No Phone"
                    } else {
                        record.getFieldValue("Phone").toString()
                    }
                } else if (moduleApiName == "Tasks") {
                    primaryFieldValue = record.getFieldValue("Subject").toString()
                    secondaryFieldValue = record.getFieldValue("Status").toString()
                    tertiaryFieldValue = record.getFieldValue("Priority").toString()
                }
                primaryField.text = primaryFieldValue
                secondaryField.text = secondaryFieldValue
                tertiaryField.text = tertiaryFieldValue
            } catch (e: ZCRMException) {
                e.printStackTrace()
            }
            return view!!
        }
    }

    fun loadRecords()
    {
        val recordGetParams = ZCRMQuery.Companion.GetRecordParams()
        recordGetParams.page = 1
        recordGetParams.perPage = 50
        recordGetParams.sortOrder = CommonUtil.SortOrder.ASC
        ZCRMSDKUtil.getModuleDelegate(moduleApiName!!).getRecords(recordGetParams, object : DataCallback<BulkAPIResponse, List<ZCRMRecord>>
        {
            override fun completed(
                response: BulkAPIResponse,
                entityList: List<ZCRMRecord>
            ) {
                records = entityList as ArrayList<ZCRMRecord>
                try {
                    recordList()
                } catch (e: ZCRMException) {
                    e.printStackTrace()
                }
            }

            override fun failed(exception: ZCRMException) {
                ZCRMLogger.logError("get records failed - $exception")
            }

        })
    }

    fun recordList() {
        var zcrmRecord: ZCRMRecord
        val itr: Iterator<*> =
            records.iterator()
        while (itr.hasNext()) {
            zcrmRecord = itr.next() as ZCRMRecord
            addRecordToList(zcrmRecord, RecordListAdapter())
        }
        setPageRefreshingOff()
    }

    fun setPageRefreshingOff() {
        mProgress!!.visibility = ProgressBar.INVISIBLE
        loading!!.text = ""
        if (records.isEmpty()) {
            emptyList!!.text = "Seems you have nothing..."
        }
    }

    fun clearList() {
        records.clear()
        storeList.clear()
    }

    fun addRecordToList(zcrmRecord: ZCRMRecord, recordListHandler: ArrayAdapter<ZCRMRecord>) {
        storeList.add(zcrmRecord)
        adapter = recordListHandler
        runOnUiThread {
            // Stuff that updates the UI
            listView.adapter = adapter
        }
    }

    companion object {
        var adapter: ArrayAdapter<ZCRMRecord>? = null
        var records: ArrayList<ZCRMRecord> = ArrayList()
        var storeList: ArrayList<ZCRMRecord> = ArrayList()
        private var moduleApiName: String? = null
    }
}