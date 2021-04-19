package dev.sagar.autootpread

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever


class MainActivity : AppCompatActivity() {

    private val RESOLVE_HINT = 101;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestHint()
        val appSignature = AppSignatureHelper(this)
        Log.d("Hash code", appSignature.appSignatures.toString())

        // Debug - aYugJVoU001
        // Release - CRjh69dfQ
    }

    private val otpListener = fun(otp: String?) {
        if (otp != null) {
            Toast.makeText(this, otp, Toast.LENGTH_SHORT).show()
            Log.d("Otp Listener", otp)
        } else {
            Toast.makeText(this, "OTP Timeout", Toast.LENGTH_SHORT).show()
            Log.d("Otp Listener", "OTP Timeout")
        }
    }

    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()
        // Kotlin
        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(
            intent.intentSender,
            RESOLVE_HINT, null, 0, 0, 0
        )
    }

    private fun startSmsListener() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.d("addOnSuccessListener", "Waiting for the OTP")
        }

        task.addOnFailureListener {
            Log.d("addOnFailureListener", "Cannot Start SMS Retriever")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)

                Toast.makeText(this, credential?.id, Toast.LENGTH_SHORT).show()
                SmsBroadcastReceiver.initOTPListener(otpListener)
                startSmsListener()
            } else {
                requestHint()
            }
        }
    }
}