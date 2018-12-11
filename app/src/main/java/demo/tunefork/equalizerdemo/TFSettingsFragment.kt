package demo.tunefork.equalizerdemo

import android.media.audiofx.Equalizer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_recycler.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TFSettingsFragment: BottomSheetDialogFragment() {
    companion object {
        val TAG = "TF_SETTINGS_DIALOG_FRAGMENT"
    }

    private lateinit var equalizer: Equalizer
    private lateinit var adapter: TFSettingsAdapter
    private lateinit var settings: Equalizer.Settings

    private var visible: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            visible = isVisibleToUser
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        equalizer = EventBus.getDefault().getStickyEvent(Equalizer::class.java)
        settings = equalizer.properties

        val settingsList = App.localRepository.settingsList
        if (settingsList.isEmpty()) {
            val set1 = Equalizer.Settings(settings.toString())
            for (i: Int in 0 until set1.numBands) {
                set1.bandLevels[i] = 0
            }
            settingsList.add(TFSettingItem("Unmodified sound", set1, true))
            val set2 = Equalizer.Settings(settings.toString())
            set2.bandLevels[0] = -1500
            set2.bandLevels[1] = -1500
            set2.bandLevels[2] = -1500
            set2.bandLevels[3] = 1500
            set2.bandLevels[4] = 1500
            settingsList.add(TFSettingItem("Set 2", set2, false))
            App.localRepository.saveTFSettings()
        }
        adapter = TFSettingsAdapter()
        adapter.updateData(settingsList)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter
        App.localRepository.getActive()?.let { applySettings(it.setting) }
    }

    private fun applySettings(setting: Equalizer.Settings) {
        TFSettingItem.applySettings(setting, equalizer)
        Log.d("Equalizer", "setting : " + equalizer.properties.toString())
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onNewSettings(newItem: NewSettingEvent) {
        val index : Int = App.localRepository.addTFSetting(TFSettingItem(newItem.title, newItem.setting, false))
        adapter.updateData(App.localRepository.settingsList)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onSettingChanged(event: SettingChangedEvent) {
        if (visible) {
            when {
                event.activeStateChanged -> {
                    App.localRepository.setActive(event.position)
                    adapter.updateData(App.localRepository.settingsList)
                    applySettings(App.localRepository.settingsList[event.position].setting)
                }
                event.settings != null -> {
                    App.localRepository.settingsList[event.position].setting = Equalizer.Settings(event.settings)
                    if (App.localRepository.settingsList[event.position].active) {
                        applySettings(App.localRepository.settingsList[event.position].setting)
                    }
                    adapter.notifyItemChanged(event.position)
                }
                else -> {
                    App.localRepository.removeTFSetting(event.position)
                    adapter.updateData(App.localRepository.settingsList)
                }
            }
        }
        else {
            adapter.updateData(App.localRepository.settingsList)
        }
    }



}