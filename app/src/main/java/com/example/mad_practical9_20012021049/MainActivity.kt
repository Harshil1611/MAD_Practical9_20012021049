package com.example.mad_practical9_20012021049

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mad_practical9_20012021049.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    public lateinit var binding: ActivityMainBinding
    private lateinit var lv: ListView
    private lateinit var al: ArrayList<SMSView>
    private lateinit var smsreceive: MyReceiver
    private val SMS_PERMISSION_CODE = 1606


    private fun requestSMSPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_SMS
            )
        ) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
            ),
            SMS_PERMISSION_CODE
        )

    }


    //main activity permission methods
    private val isSMSReadPermission: Boolean
        get() = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
    private val isSMSWritePermission: Boolean
        get() = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED

    //
    private fun checkRequestPermission(): Boolean {
        return if (!isSMSReadPermission || !isSMSWritePermission) {
            requestSMSPermission()
            false
        } else true
    }

    private fun loadSMSInbox() {
        if (!checkRequestPermission()) return
        val uriSMS = Uri.parse("content://sms/inbox")
        val c = contentResolver.query(uriSMS, null, null, null, null)
        al.clear()
        while (c!!.moveToNext()) {
            al.add(SMSView(c.getString(2),c.getString(12)))
        }
        lv.adapter = SMSViewAdapter(this,al)
    }

    override fun onDestroy() {
        unregisterReceiver(smsreceive)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lv= binding.text1
        al= ArrayList()
        if (checkRequestPermission()){
            loadSMSInbox()
        }
        smsreceive = MyReceiver()
        registerReceiver(smsreceive, IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
    }
inner class ListnerImplement:MyReceiver.Listner{
    override fun onTextReceived(sPhoneNo: String?, sMsg: String?) {
        val builder :AlterDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("New SMS RECEIVED")
        builder.setMessage("$sPhoneNo\n$sMsg")
        builder.setCancelable(true)
        builder.show()
        loadSMSInbox()
    }
}



}
