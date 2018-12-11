package demo.tunefork.equalizerdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment


class TFPlayerFragment: Fragment() {

    lateinit var adapter: TFPlayerAdapter
    lateinit var quickAdapter: TFQuickSettingAdapter
    var player: YouTubePlayer? = null

    private val playerList = listOf<TFPlayerItem>(
        TFPlayerItem("_5joTyy3CCo", "Mega Drive - I Am The Program"),
        TFPlayerItem("olHnyslc-OM", "The Prodigy | Omen |"),
        TFPlayerItem("WNeLUngb-Xg", "Linkin Park - In The End (Mellen Gi & Tommee Profitt Remix)"),
        TFPlayerItem("MkgR0SxmMKo", "Scandroid - Neo Tokyo (Dance With The Dead Remix)"),
        TFPlayerItem("EAYfJckSEN0", "Wice - Star Fighter (Official Video) - | Magnatron 2.0 is OUT NOW |"),
        TFPlayerItem("aG1rl6t0UUA","Sonic Mayhem - Futureland (feat. Power Glove)"),
        TFPlayerItem("CdOS_rjjUvQ","Red MarKer - DMC 12 Gauge"),
        TFPlayerItem("e6MCkspqtxo","Volkor X - Enclave")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeYoutubePlayer()
        adapter = TFPlayerAdapter(playerList)
        quickAdapter = context?.let { TFQuickSettingAdapter(it) }!!
        quickAdapter.updateData(App.localRepository.settingsList)
        view.recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        view.recycler.adapter = adapter
        view.recycler_settings.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        view.recycler_settings.adapter = quickAdapter
    }

    private fun initializeYoutubePlayer() {
        val fragment = YouTubePlayerSupportFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.player_view, fragment)
            .commitAllowingStateLoss()


        fragment.initialize(App.YOUTUBE_KEY, object : YouTubePlayer.OnInitializedListener {

            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer,
                wasRestored: Boolean
            ) {

                //if initialization success then load the video id to youtube player
                if (!wasRestored) {
                    //set the player style here: like CHROMELESS, MINIMAL, DEFAULT
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                    player = youTubePlayer
                    youTubePlayer.cueVideo(playerList[0].videoId)
                }
            }

            override fun onInitializationFailure(arg0: YouTubePlayer.Provider, arg1: YouTubeInitializationResult) {
                //print or show error if initialization failed

            }
        })
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onItemChosen(event: TFPlayerItem) {
        player?.loadVideo(event.videoId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onSettingChanged(event: SettingChangedEvent) {
        if (event.settings == null && !event.activeStateChanged) {
            return
        }
        quickAdapter.updateData(App.localRepository.settingsList)
        if (event.activeStateChanged) {
            recycler_settings.scrollToPosition(event.position)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onNewSetting(event: NewSettingEvent) {
        quickAdapter.updateData(App.localRepository.settingsList)
        recycler_settings.scrollToPosition(quickAdapter.itemCount-1)
    }
}