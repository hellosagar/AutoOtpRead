package dev.sagar.autootpread

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class
SmsBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private lateinit var otpListener: (String?) -> Unit

        fun initOTPListener(otpListener: (String?) -> Unit) {
            this.otpListener = otpListener
        }
    }


    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    var otp: String = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    Log.d("OTP_Message", otp)
                    otp = otp.replace("<#> Your ExampleApp code is: ", "").split("\n".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    otpListener(otp)
                }
                CommonStatusCodes.TIMEOUT ->
                    otpListener(null)
            }
        }
    }
}