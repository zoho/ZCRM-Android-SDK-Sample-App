package com.zoho.testconfig;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zoho.crm.library.exception.ZCRMException;
import com.zoho.crm.library.setup.restclient.ZCRMRestClient;
import com.zoho.crm.library.setup.users.ZCRMUser;
import com.zoho.crm.sdk.android.zcrmandroid.activity.ZCRMBaseActivity;

public class NavigationDrawer extends ZCRMBaseActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	public ZCRMUser user;
	Toolbar toolbar;
	private static CharSequence mTitle;
	public static int setTabno = 0;
	public int Tabno = setTabno;
	public int count =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation_drawer);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mTitle = "Zoho CRM";

		AsyncTaskRunner runner = new AsyncTaskRunner();
		runner.execute();

	}

	public void setLoginUserDetails()  {
		try {
			count++;
			user = (ZCRMUser)  ZCRMRestClient.getInstance().getCurrentUser().getData();
		} catch (ZCRMException e) {
			e.printStackTrace();
		}
	}

	public void setNavDrawer()
	{
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		View header=navigationView.getHeaderView(0);

		TextView username = header.findViewById(R.id.username);
		username.setText(String.valueOf(user.getFullName()));
		TextView usermail = header.findViewById(R.id.usermail);
		usermail.setText(String.valueOf(user.getEmailId()));
		select_item(Tabno);
	}

	public void logout_click(MenuItem item) {
		super.logout(new OnLogoutListener() {
			@Override
			public void onLogoutSuccess() {
				System.out.println(">>> Logout Success...");
			}

			@Override
			public void onLogoutFailed() {
				System.out.println(">>> Logout failed...");
			}
		});
	}

	public void select_item(int id) {
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		onNavigationItemSelected(navigationView.getMenu().getItem(id));
		setTabno = 0;
		Tabno = 0;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void setNavStartUpPage() {
		Class fragmentClass = HomePageActivity.class;
		Fragment fragment = null;
		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(null).commit();

	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.navigation_drawer, menu);
		restoreActionBar();
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item)
    {
		Fragment fragment = null;
		Class fragmentClass = null;
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.customers)
		{
			ListViewAdapter.moduleAPIname = "Contacts";
			mTitle = "Customers";
			restoreActionBar();
			fragmentClass = CustomersListHandler.class;
		}
		else if (id == R.id.homepage)
		{
			mTitle = "Home";
			restoreActionBar();
			fragmentClass = HomePageActivity.class;
		}
		else if (id == R.id.tasks)
		{
			ListViewAdapter.moduleAPIname = "Tasks";
			mTitle = "Tasks";
			restoreActionBar();
			fragmentClass = TasksListHandler.class;
		}
		else if (id == R.id.events)
		{
			ListViewAdapter.moduleAPIname = "Events";
			mTitle = "Events";
			restoreActionBar();
			fragmentClass = EventsListHandler.class;
		}
		else if (id == R.id.calls)
		{
			ListViewAdapter.moduleAPIname = "Calls";
			mTitle = "Calls";
			restoreActionBar();
			fragmentClass = CallsListHandler.class;
		}

		try {
			fragment = (Fragment) fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.flContent, fragment).addToBackStack(String.valueOf(mTitle)).commit();


		return true;

	}

	class AsyncTaskRunner extends AsyncTask<String, String, String> {
		private String resp;


		@Override
		protected String doInBackground(String... params) {
			setLoginUserDetails();
			return resp;
		}

		@Override
		protected void onPostExecute(String result) {
			setNavStartUpPage();
			setNavDrawer();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(String... text) {
		}
	}
}