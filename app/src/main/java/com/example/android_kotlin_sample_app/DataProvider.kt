package com.example.android_kotlin_sample_app

import android.content.Context
import com.zoho.crm.sdk.android.api.handler.DataCallback
import com.zoho.crm.sdk.android.api.response.APIResponse
import com.zoho.crm.sdk.android.exception.ZCRMException
import com.zoho.crm.sdk.android.exception.ZCRMLogger
import com.zoho.crm.sdk.android.setup.metadata.ZCRMOrg
import com.zoho.crm.sdk.android.setup.sdkUtil.ZCRMSDKUtil
import com.zoho.crm.sdk.android.setup.users.ZCRMUser

class DataProvider private constructor() {
    private var userName: String? = null
    private var organisationName: String? = null

    fun getOrganisationName(handler: DataHandler) {
        if (organisationName == null) {
            ZCRMSDKUtil.getOrgDetails(object : DataCallback<APIResponse, ZCRMOrg>
            {
                override fun completed(response: APIResponse, org: ZCRMOrg) {
                    organisationName = org.name ?: "No Company name"
                    handler.setOrganizationName(organisationName!!)
                }

                override fun failed(exception: ZCRMException) {
                    organisationName = " Error "
                    handler.setOrganizationName(organisationName!!)
                    ZCRMLogger.logError("Unable to get org details - $exception")
                }

            })
        } else {
            handler.setOrganizationName(organisationName!!)
        }
    }

    fun getUserName(handler: DataHandler) {
        if (userName == null) {
            ZCRMSDKUtil.getCurrentUser(object : DataCallback<APIResponse, ZCRMUser>
            {
                override fun completed(response: APIResponse, user: ZCRMUser) {
                    userName = user.fullName
                    handler.setUserName(userName!!)
                }

                override fun failed(exception: ZCRMException) {
                    userName = " Error "
                    handler.setUserName(userName!!)
                    ZCRMLogger.logError("Unable to get user details - $exception")
                }

            })
        } else {
            handler.setUserName(userName!!)
        }
    }
    companion object {
        @JvmStatic
        val instance = DataProvider()
    }
}