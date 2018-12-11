package demo.tunefork.equalizerdemo

import android.media.audiofx.Equalizer
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.item_settings.view.*
import org.greenrobot.eventbus.EventBus

class TFSettingsAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val settings : MutableList<TFSettingItem> = mutableListOf<TFSettingItem>()
    private val equalizer: Equalizer = EventBus.getDefault().getStickyEvent(Equalizer::class.java)

    private var lastChecked : RadioButton? = null
    private var lastCheckedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_settings, parent, false)
        val minEQLevel = equalizer.bandLevelRange[0]
        val maxEQLevel = equalizer.bandLevelRange[1]

        for (i in 0 until equalizer.numberOfBands) {
            val freqTextView = TextView(parent.context)
            freqTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            freqTextView.gravity = Gravity.CENTER_HORIZONTAL
            freqTextView.text = (equalizer.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
            view.settings_list.addView(freqTextView)

            val row = LinearLayout(parent.context)
            row.orientation = LinearLayout.HORIZONTAL

            val minDbTextView = TextView(parent.context)
            minDbTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            minDbTextView.text = (minEQLevel / 100).toString() + " dB"

            val maxDbTextView = TextView(parent.context)
            maxDbTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            maxDbTextView.text = (maxEQLevel / 100).toString() + " dB"

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1f
            val bar = SeekBar(parent.context)
            bar.layoutParams = layoutParams
            bar.max = maxEQLevel - minEQLevel

            row.addView(minDbTextView)
            row.addView(bar)
            row.addView(maxDbTextView)

            view.settings_list.addView(row)
        }

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    fun updateData(newList : List<TFSettingItem>) {
        settings.clear()
        settings.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = settings[position]
        val setting = Equalizer.Settings(item.setting.toString())
        holder.setSettings(setting)
        val isChanged = setting.toString() == item.setting.toString()
        holder.save.visibility = if (isChanged) View.GONE else View.VISIBLE
        holder.restore.visibility = if (isChanged) View.GONE else View.VISIBLE

        holder.title.text = item.title
        holder.active.isChecked = item.active
        holder.active.tag = position

        if(position == 0 && item.active && holder.active.isChecked)
        {
            lastChecked = holder.active
            lastCheckedPos = 0
        }

        holder.active.setOnClickListener { v ->
            val cb = v as RadioButton
            val clickedPos = (cb.tag as Int).toInt()

            if (cb.isChecked) {
                if (lastChecked != null) {
                    lastChecked?.isChecked = false
                    settings[lastCheckedPos].active = false
                }

                lastChecked = cb
                lastCheckedPos = clickedPos
            } else
                lastChecked = null

            //settings[clickedPos].active = cb.isChecked
            EventBus.getDefault().post(SettingChangedEvent(clickedPos, activeStateChanged = true))
        }

        holder.restore.setOnClickListener {
            EventBus.getDefault().post(SettingChangedEvent(position, item.setting.toString()))
        }
        val minEQLevel = TFSettingItem.minEqLevel
        val maxEQLevel = TFSettingItem.maxEqLevel

        for (i in 0 until setting.numBands) {
            val linear: LinearLayout = holder.container_list.getChildAt(i*2+1) as LinearLayout
            val bar: SeekBar = linear.getChildAt(1) as SeekBar

            bar.setOnSeekBarChangeListener(null)

            bar.progress = holder.setting.bandLevels[i].toInt() - minEQLevel

            bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    holder.setting.bandLevels[i] = (progress + minEQLevel).toShort()
                    holder.save.visibility = if (holder.setting.toString() == settings[holder.adapterPosition].setting.toString()) View.GONE else View.VISIBLE
                    holder.restore.visibility = if (holder.setting.toString() == settings[holder.adapterPosition].setting.toString()) View.GONE else View.VISIBLE
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
    }

}

class ViewHolder (view : View): RecyclerView.ViewHolder(view), View.OnClickListener {

    val title: TextView = view.title
    val active: RadioButton = view.checkbox
    val container: LinearLayout = view.settings_container
    val container_list: LinearLayout = view.settings_list
    val arrow: AppCompatImageView = view.arrow
    val save: AppCompatImageButton = view.save
    val restore: AppCompatImageButton = view.restore
    lateinit var setting: Equalizer.Settings

    init {
        save.setOnClickListener(this)
        view.delete.setOnClickListener(this)
        view.settings_header.setOnClickListener(this)
    }

    fun setSettings(settings: Equalizer.Settings) {
        this.setting = settings
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.save -> {
                EventBus.getDefault().post(SettingChangedEvent(adapterPosition, setting.toString()))
            }
            R.id.settings_header -> {
                arrow.setImageResource(if (container.visibility == View.VISIBLE) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up)
                container.visibility = if (container.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            R.id.delete -> {
                EventBus.getDefault().post(SettingChangedEvent(adapterPosition))
            }
        }
    }
}

