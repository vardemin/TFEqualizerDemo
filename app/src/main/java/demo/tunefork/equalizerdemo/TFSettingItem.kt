package demo.tunefork.equalizerdemo

import android.media.audiofx.Equalizer
import org.json.JSONObject
import java.lang.Exception

class TFSettingItem (val title: String, var setting: Equalizer.Settings, var active: Boolean) {
    companion object {
        var minEqLevel: Short = -1500
        var maxEqLevel: Short = 1500
    }
    override fun toString(): String {
        val json: JSONObject = JSONObject()
        json.put("title", title)
        json.put("setting", setting.toString())
        json.put("active", active)
        return json.toString()
    }

    fun toJSONObject(): JSONObject {
        val json: JSONObject = JSONObject()
        json.put("title", title)
        json.put("setting", setting.toString())
        json.put("active", active)
        return json
    }
}

fun TFSettingItem.Companion.fromJSON(json: JSONObject) : TFSettingItem? {
    return try {
        TFSettingItem(json.getString("title"), Equalizer.Settings(json.getString("setting")), json.getBoolean("active"))
    }
    catch (e: Exception) {
        null
    }
}

fun TFSettingItem.Companion.applySettings(setting: Equalizer.Settings, equalizer: Equalizer){
    equalizer.properties = setting
    for(i: Int in 0 until equalizer.numberOfBands) {
        equalizer.setBandLevel(i.toShort(), setting.bandLevels[i])
    }
}