package demo.tunefork.equalizerdemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.google.android.youtube.player.YouTubeThumbnailView

import kotlinx.android.synthetic.main.item_youtube.view.*
import org.greenrobot.eventbus.EventBus
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader



data class TFPlayerItem(val videoId: String, val title: String)

class TFPlayerAdapter(private val playerList: List<TFPlayerItem>): RecyclerView.Adapter<TFPlayerHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TFPlayerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_youtube, parent, false)
        val holder = TFPlayerHolder(view)
        view.btn_show.setOnClickListener {
            if (holder.adapterPosition != NO_POSITION) {
                EventBus.getDefault().post(playerList[holder.adapterPosition])
            }
        }
        return holder
    }

    override fun getItemCount(): Int {
        return playerList.size
    }

    override fun onBindViewHolder(holder: TFPlayerHolder, position: Int) {
        val item = playerList[position]
        holder.title.text = item.title
        holder.thumbnail.initialize(
            App.YOUTUBE_KEY,
            object : YouTubeThumbnailView.OnInitializedListener {
                override fun onInitializationSuccess(
                    youTubeThumbnailView: YouTubeThumbnailView,
                    youTubeThumbnailLoader: YouTubeThumbnailLoader
                ) {
                    //when initialization is sucess, set the video id to thumbnail to load
                    youTubeThumbnailLoader.setVideo(item.videoId)

                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(object :
                        YouTubeThumbnailLoader.OnThumbnailLoadedListener {
                        override fun onThumbnailLoaded(youTubeThumbnailView: YouTubeThumbnailView, s: String) {
                            //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                            youTubeThumbnailLoader.release()
                        }

                        override fun onThumbnailError(
                            youTubeThumbnailView: YouTubeThumbnailView,
                            errorReason: YouTubeThumbnailLoader.ErrorReason
                        ) {
                            //print or show error when thumbnail load failed
                            Log.e("TF YOUTUBE", "Youtube Thumbnail Error")
                        }
                    })
                }

                override fun onInitializationFailure(
                    youTubeThumbnailView: YouTubeThumbnailView,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {
                    //print or show error when initialization failed
                    Log.e("TF YOUTUBE", "Youtube Initialization Failure")

                }
            })
    }
}

class TFPlayerHolder (view : View): RecyclerView.ViewHolder(view) {
    val title: TextView = view.item_title
    val thumbnail: YouTubeThumbnailView = view.item_thumbnail
}