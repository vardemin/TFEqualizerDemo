package demo.tunefork.equalizerdemo

data class SettingChangedEvent(val position: Int, val settings: String? = null, val activeStateChanged: Boolean = false)