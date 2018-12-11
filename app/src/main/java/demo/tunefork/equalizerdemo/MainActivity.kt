package demo.tunefork.equalizerdemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.media.audiofx.Equalizer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager

import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TFViewPagerAdapter

    private lateinit var equalizer: Equalizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager : AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        equalizer = Equalizer(Int.MAX_VALUE, 0)
        TFSettingItem.minEqLevel = equalizer.bandLevelRange[0]
        TFSettingItem.maxEqLevel = equalizer.bandLevelRange[1]
        EventBus.getDefault().postSticky(equalizer)
        checkbox_equalizer.setOnCheckedChangeListener { button, isChecked ->
            if (equalizer.setEnabled(isChecked) != AudioEffect.SUCCESS) {
                button.isChecked = !isChecked
            }
            Log.d("Equalizer", "Changed state : " + equalizer.enabled)
            Log.d("Equalizer", "setting : " + equalizer.properties.toString())
            Log.d("Equalizer", "Presets" + equalizer.numberOfPresets)
            Log.d("Equalizer", "Preset name: " + equalizer.getPresetName(equalizer.currentPreset))
            var str: String = ""
            for (i: Int in 0 until equalizer.numberOfPresets-1) {
                str+=equalizer.getPresetName(i.toShort()) + " "
            }
            Log.d("Equalizer", "Preset names: $str")
        }
        bar_button.setOnClickListener(clickListener)
        adapter = TFViewPagerAdapter(supportFragmentManager)
        view_pager.adapter = adapter

        bottom_app_bar.replaceMenu(R.menu.settings_menu)
        bottom_app_bar.setOnMenuItemClickListener {
            when (it!!.itemId) {
                R.id.menu_equalizer -> view_pager.setCurrentItem(0, true)
                R.id.menu_player -> view_pager.setCurrentItem(1, true)
            }

            false
        }
        setupPermissions()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.MODIFY_AUDIO_SETTINGS)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission need for stable work", Toast.LENGTH_SHORT).show()
            makeRequest()
        }
    }

    private val RECORD_REQUEST_CODE: Int = 77

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.MODIFY_AUDIO_SETTINGS),
            RECORD_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("TFEQUALIZER", "Permission has been denied by user")
                } else {
                    Log.i("TFEQUALIZER", "Permission has been granted by user")
                }
            }
        }
    }

    private val clickListener : View.OnClickListener = View.OnClickListener {
        val id: Int = it.id
        when (id) {
            R.id.bar_button -> {
                NewTFSettingsDialog().show(supportFragmentManager, NewTFSettingsDialog.TAG)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        equalizer.enabled = false
        equalizer.release()
    }

}
