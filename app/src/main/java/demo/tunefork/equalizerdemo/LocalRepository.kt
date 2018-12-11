package demo.tunefork.equalizerdemo

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


class LocalRepository(context: Context) {
    private val APP_PREFERENCES = "TF_DEMO_PREFERENCES"
    private val SETTINGS_KEY = "TF_SETTINGS_KEY"
    private val preferences: SharedPreferences

    var settingsList : MutableList<TFSettingItem>

    init {
        preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        settingsList = fetchTFSettings()
    }

    fun fetchTFSettings() : MutableList<TFSettingItem> {
        val settingsList : MutableList<TFSettingItem> = arrayListOf()
        try {
            val array: JSONArray = JSONObject(preferences.getString(SETTINGS_KEY, "")).getJSONArray("settings")

            for (i: Int in 0 until array.length()) {
                TFSettingItem.fromJSON(array.getJSONObject(i))?.let { settingsList.add(it) }
            }
        }
        catch (e: Throwable) {
            e.printStackTrace()
        }
        return settingsList
    }

    fun addTFSetting(setting: TFSettingItem): Int {
        settingsList.add(setting)
        saveTFSettings()
        return settingsList.indexOf(setting)
    }

    fun removeTFSetting(setting: TFSettingItem) {
        settingsList.remove(setting)
        saveTFSettings()
    }

    fun removeTFSetting(position: Int) {
        if (settingsList.size > position) {
            settingsList.removeAt(position)
            saveTFSettings()
        }
    }

    fun setActive(position: Int) {
        for (i: Int in 0 until settingsList.size) {
            settingsList[i].active = i == position
        }
        saveTFSettings()
    }

    fun getActive() : TFSettingItem? {
        return settingsList.find { it.active }
    }

    fun saveTFSettings() {
        GlobalScope.launch {
            try {
                val obj = JSONObject()
                val arrray = JSONArray(settingsList.map { it.toJSONObject() }.toList())
                obj.put("settings", arrray)
                preferences.edit().putString(SETTINGS_KEY, obj.toString()).apply()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}