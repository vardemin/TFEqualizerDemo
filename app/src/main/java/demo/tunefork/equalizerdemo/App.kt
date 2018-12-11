package demo.tunefork.equalizerdemo

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Companion.localRepository = LocalRepository(this)
    }

    companion object {
        lateinit var localRepository: LocalRepository
        val YOUTUBE_KEY = "AIzaSyCHA-vwBT253NmqP0T1iNSgSdoldXxWUdI"
    }
}