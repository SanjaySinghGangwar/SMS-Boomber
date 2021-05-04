package com.sanjaysgangwar.smsboomber.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.sanjaysgangwar.smsboomber.R
import com.sanjaysgangwar.smsboomber.databinding.SettingBinding
import com.sanjaysgangwar.smsboomber.model.mSharedPreference

class Setting : AppCompatActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private lateinit var bind: SettingBinding
    private lateinit var sharedPreference: mSharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = SettingBinding.inflate(layoutInflater)
        setContentView(bind.root)

        initAllComponents()
    }

    private fun initAllComponents() {
        setUpToolbar()
        sharedPreference = mSharedPreference(this)
        bind.donate.setOnClickListener(this)
        bind.share.setOnClickListener(this)
        bind.otherApps.setOnClickListener(this)
        bind.ads.setOnCheckedChangeListener(this)

        bind.ads.isChecked = sharedPreference.ads

    }

    private fun setUpToolbar() {
        bind.toolbar.title = "Setting"
        setSupportActionBar(bind.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.donate -> {
                intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("https://www.buymeacoffee.com/TheAverageGuy")
                intent = Intent.createChooser(intent, "Donate us ♥")
                startActivity(intent)
            }
            R.id.share -> {
                intent = Intent()
                intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Desk Clock")
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, Just found really awesome game on app store,\n\nhttps://apps.samsung.com/appquery/appDetail.as?appId=$packageName") /*https://apps.samsung.com/appquery/appDetail.as?appId=\" + view.getContext().getPackageName()*//*https://www.amazon.com/dp/B08LGSK7WT/ref=apps_sf_sta*/
                startActivity(Intent.createChooser(intent, "Share with"))
            }
            R.id.otherApps -> {
                intent = Intent(this, OtherAppsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.ads -> {
                sharedPreference.ads = isChecked
            }
        }
    }
}