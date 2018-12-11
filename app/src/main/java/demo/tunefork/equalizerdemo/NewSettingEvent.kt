package demo.tunefork.equalizerdemo

import android.media.audiofx.Equalizer

data class NewSettingEvent (val title: String, val setting: Equalizer.Settings)