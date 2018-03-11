package com.zoho.sample_app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zoho.crm.library.crud.ZCRMField;
import com.zoho.crm.library.crud.ZCRMLayout;
import com.zoho.crm.library.crud.ZCRMModule;
import com.zoho.crm.library.crud.ZCRMRecord;
import com.zoho.crm.library.crud.ZCRMSection;
import com.zoho.crm.library.exception.ZCRMException;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecordViewHandler extends AppCompatActivity
{
	ProgressBar mProgress;
	SwipeRefreshLayout refreshLayout;
	GridLayout layout;
	private  Boolean isRefreshed = false;
	private ZCRMRecord zcrmRecord;
	private ZCRMLayout zcrmlayout;
	ProgressDialog dialog;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_view_page);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(ListViewAdapter.nameClicked);
		setForm();
	}

	public void setForm()
	{
		this.layout = (GridLayout) findViewById(R.id.grid_layout);
		this.layout.setRowCount(300);
		this.layout.setColumnCount(100);

		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.viewrefresh);
		refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
		{
			@Override
			public void onRefresh()
			{
				isRefreshed = true;
				APImodeRunner runner = new APImodeRunner();
				runner.execute();
			}
		});

		mProgress = (ProgressBar) findViewById(R.id.progress);
		mProgress.setVisibility(ProgressBar.VISIBLE);

		dialog = ProgressDialog.show(RecordViewHandler.this, "",
				"Loading. Please wait...", true); //No I18N

		APImodeRunner runner = new APImodeRunner();
		runner.execute();
	}

	@SuppressLint("ResourceAsColor")
	public void loadForm() throws JSONException, ZCRMException {
		System.out.println(">> record : "+zcrmRecord.getEntityId());
		System.out.println(">> layout : "+zcrmlayout.getId());
		int row=0,col=0,count=0;
		List<ZCRMSection> sections = zcrmlayout.getSections();
		List<ZCRMField> fields = new ArrayList<>();
		int id = 2000;

		System.out.println(">> section count : "+sections.size());
		Iterator itr = sections.iterator();
		while (itr.hasNext())
		{
			ZCRMSection zcrmSection = (ZCRMSection) itr.next();
			col = 0;
			TextView Sectiontext = setSection(zcrmSection.getName(),id,count,col,row);
			this.layout.addView(Sectiontext);
			row++;
			count++;

			fields = (ArrayList<ZCRMField>) zcrmSection.getAllFields();
			Iterator itr_arr = fields.iterator();

			while (itr_arr.hasNext()) {
				ZCRMField field = (ZCRMField) itr_arr.next();

				col = 1;
				TextView textview = setFieldName(field.getDisplayName(), id, count, col, row);
				textview.setTextColor(R.color.colorPrimary);
				this.layout.addView(textview);

				count++;

				col = 20;
				String field_value = getValue(field.getApiName());
				TextView textview2 = setFieldValue(field_value, id, count, col, row);
				this.layout.addView(textview2);

				row = row + 2;
				count++;
			}
		}
		dialog.dismiss();
		refreshLayout.setRefreshing(false);
		mProgress.setVisibility(ProgressBar.INVISIBLE);
	}

	private String getValue(String fieldAPIname) throws JSONException, ZCRMException {

		if (fieldAPIname.equals("Owner"))
		{
			return zcrmRecord.getOwner().getFullName();
		}
		else if (fieldAPIname.equals("Created_By"))
		{
			return zcrmRecord.getCreatedBy().getFullName();
		}
		else if (fieldAPIname.equals("Created_Time"))
		{
			return zcrmRecord.getCreatedTime();
		}
		else if (fieldAPIname.equals("Modified_By"))
		{
			return zcrmRecord.getModifiedBy().getFullName();
		}
		else if (fieldAPIname.equals("Modified_Time"))
		{
			return zcrmRecord.getModifiedTime();
		}
		else if(fieldAPIname.equals("Layout"))
		{
			return zcrmlayout.getName();
		}else if(!zcrmRecord.getData().containsKey(fieldAPIname))
		{
			return "";
		}
		else if(zcrmRecord.getFieldValue(fieldAPIname) instanceof ZCRMRecord)
		{
			ZCRMRecord record = (ZCRMRecord) zcrmRecord.getFieldValue(fieldAPIname);
			return record.getLookupLabel();
		}
		else if(zcrmRecord.getFieldValue(fieldAPIname) == null || "null".equals(String.valueOf(zcrmRecord.getFieldValue(fieldAPIname)))) {
			return "";
		}
		else {
			return String.valueOf(zcrmRecord.getFieldValue(fieldAPIname));
		}
	}

	private TextView setSection(String sectionName, int id, int count, int col, int row)
	{
		System.out.println(">> Section : "+sectionName);
		TextView Sectiontext = new TextView(this);
		Sectiontext.clearComposingText();
		Sectiontext.setText(null);
		Sectiontext.setText(sectionName);
		Sectiontext.setId(id + count);
		Sectiontext.setTextAppearance(this, android.R.style.TextAppearance_Large);
		GridLayout.Spec rowspan3 = GridLayout.spec(row, 1);
		GridLayout.Spec colspan3 = GridLayout.spec(col, 25);
		GridLayout.LayoutParams lp_Sectiontext = new GridLayout.LayoutParams(rowspan3, colspan3);
		lp_Sectiontext.height = GridLayout.LayoutParams.WRAP_CONTENT;
		lp_Sectiontext.width = GridLayout.LayoutParams.MATCH_PARENT;
		Sectiontext.setLayoutParams(lp_Sectiontext);

		return Sectiontext;
	}

	private TextView setFieldName(String fieldName, int id, int count, int col, int row)
	{
		System.out.println(">> field name : "+fieldName);
		TextView textview = new TextView(getApplicationContext());
		textview.clearComposingText();
		textview.setText(null);
		textview.setText(fieldName);
		textview.setId(id + count);
		textview.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		GridLayout.Spec rowspan1 = GridLayout.spec(row, 1);
		GridLayout.Spec colspan1 = GridLayout.spec(col, 9);
		GridLayout.LayoutParams lp = new GridLayout.LayoutParams(rowspan1, colspan1);
		lp.height = 100;
		lp.width = 500;
		textview.setLayoutParams(lp);

		return textview;
	}

	private TextView setFieldValue(String fieldValue, int id, int count, int col, int row)
	{
		System.out.println(">> field value : "+fieldValue);
		TextView textview2 = new TextView(getApplicationContext());
		textview2.clearComposingText();
		textview2.setText(null);
		textview2.setText(fieldValue);
		textview2.setId(id + count);
		textview2.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		GridLayout.Spec rowspan2 = GridLayout.spec(row, 1);
		GridLayout.Spec colspan2 = GridLayout.spec(col, 3);
		GridLayout.LayoutParams lpp = new GridLayout.LayoutParams(rowspan2, colspan2);
		lpp.height = 100;
		lpp.width = 500;
		textview2.setLayoutParams(lpp);

		return textview2;
	}


	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void onBackPressed()
	{
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.record_view_page_drawer, menu);
		return true;
	}

	class APImodeRunner extends AsyncTask<String, String, String>
	{
		private String resp;
		@Override
		protected String doInBackground(String... params)
		{
			try
			{

				zcrmRecord = (ZCRMRecord) ZCRMModule.getInstance(ListViewAdapter.moduleAPIname).getRecord(ListViewAdapter.idClicked).getData();
				if(zcrmRecord.getLayout() != null) {
					zcrmlayout = (ZCRMLayout) ZCRMModule.getInstance(ListViewAdapter.moduleAPIname).getLayoutDetails(zcrmRecord.getLayout().getId()).getData();
				}else {
					zcrmlayout = ((List<ZCRMLayout>) ZCRMModule.getInstance(ListViewAdapter.moduleAPIname).getLayouts().getData()).get(0);
				}
				resp = "Success"; //no I18N
			} catch (Exception e)
			{
				e.printStackTrace();
				resp = e.getMessage();
			}
			return resp;
		}

		@Override
		protected void onPostExecute(String result)
		{
				try
				{
					loadForm();
				} catch (ZCRMException | JSONException e)
				{
					e.printStackTrace();
				}
		}
	}
}
