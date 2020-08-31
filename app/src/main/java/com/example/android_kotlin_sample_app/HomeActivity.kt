package com.example.android_kotlin_sample_app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.zoho.crm.sdk.android.authorization.ZCRMSDKClient
import com.zoho.crm.sdk.android.common.CommonUtil
import com.zoho.crm.sdk.android.configuration.ZCRMSDKConfigs
import com.zoho.crm.sdk.android.exception.ZCRMException
import com.zoho.crm.sdk.android.exception.ZCRMLogger
import kotlinx.android.synthetic.main.home_activity.*
import java.util.logging.Level

class HomeActivity : AppCompatActivity(),
    DataHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        showLogin()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_page, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Take appropriate action for each action item click
        return when (item.itemId) {
            R.id.signout -> {
                AlertDialog.Builder(this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which -> logout() }
                    .setNegativeButton(
                        "No"
                    ) { dialog, which ->
                        // user doesn't want to logout
                    }
                    .show()
                true
            }
            else -> false
        }
    }

    private fun showLogin() {
        try {

            val configs = ZCRMSDKConfigs()
            configs.appType = CommonUtil.AppType.ZVCRM
            configs.apiBaseURL = "https://___VARIABLE_ApiBaseURL___"
            configs.oauthScopes = "ZohoCRM.users.ALL,ZohoCRM.org.ALL,profile.orguserphoto.READ,ZohoCRM.notifications.ALL,ZohoCRM.bulk.ALL,ZohoCRM.crmapi.ALL,ZohoCRM.settings.intelligence.ALL,ZohoCRM.modules.ALL,ZohoCRM.settings.ALL"
            configs.setClientDetails("___VARIABLE_ClientID___", "___VARIABLE_ClientSecret___")
//            configs.portalID = "___VARIABLE_PortalId___" // for ZCRMCP / ZVCRM app type
//            configs.customerPortalName = "___VARIABLE_PortalName___" // for ZCRMCP app type
            configs.httpRequestMode = CommonUtil.HttpRequestMode.ASYNC
            configs.userAgent = "ZCRM Android Sample App"
            configs.setLoggingPreferences(Level.ALL, true)

            ZCRMSDKClient.getInstance(applicationContext).init(configs, object : ZCRMSDKClient.Companion.ZCRMInitCallback {

                override fun onSuccess() {
                    runOnUiThread { loadViews() }
                }

                override fun onFailed(ex: ZCRMException) {
                    runOnUiThread {
                        ZCRMLogger.logError("Login failed - $ex")
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logout() {
        ZCRMSDKClient.getInstance(applicationContext).logout(object : ZCRMSDKClient.Companion.ZCRMLogoutCallback {

            override fun onSuccess() {
                runOnUiThread { showLogin() }
            }

            override fun onFailed(ex: ZCRMException) {
                runOnUiThread {
                    ZCRMLogger.logError("Login failed - $ex")
                }
            }
        })
    }

    private fun loadViews() {
        dataProvider.getUserName( this)
        dataProvider.getOrganisationName( this)
        contactsRecords.setOnClickListener { startActivity("Contacts") }
        tasksRecords.setOnClickListener { startActivity("Tasks") }
    }

    private fun startActivity(moduleApiName: String) {
        val intent = Intent(
            applicationContext,
            ListActivity::class.java
        )
        intent.putExtra("module", moduleApiName)
        startActivity(intent)
    }

    override fun setUserName(name: String) {
        runOnUiThread {
            val userNameWithMsg = "Welcome $name!"
            userName.setText(userNameWithMsg)
        }
    }

    override fun setOrganizationName(orgName: String) {
        runOnUiThread {
            if (supportActionBar != null) {
                supportActionBar!!.title = orgName
            }
        }
    }

    companion object {
        private val dataProvider = DataProvider.instance
    }
}