package com.nativenote.locationproviderclient

import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import com.nativenote.locationproviderclient.service.TrackerService
import com.nativenote.locationproviderclient.service.TrackerUtil
import kotlinx.android.synthetic.main.switch_layout.*

class MainActivity : AppCompatActivity() {

    var service: TrackerService? = null
    private val receiver: MyReceiver = MyReceiver()

    private var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder: TrackerService.LocalBinder = p1 as TrackerService.LocalBinder
            service = binder.service

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            service = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchInActionBar.setOnClickListener { view ->
            if ((view as SwitchCompat).isChecked) {
                service?.requestLocationUpdates()
            } else {
                service?.removeLocationUpdates()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        bindService(Intent(this, TrackerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(TrackerService.ACTION_BROADCAST))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    class MyReceiver : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            val location: Location = p1!!.getParcelableExtra(TrackerService.EXTRA_LOCATION)
            location.let {
                println("XXX1: " + TrackerUtil.getLocationText(location) + " : " + TrackerUtil.getLongToDate(System.currentTimeMillis()))
            }
        }

    }
}
