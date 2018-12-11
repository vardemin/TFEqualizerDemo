package demo.tunefork.equalizerdemo

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_quick.view.*
import org.greenrobot.eventbus.EventBus

class TFQuickSettingAdapter(context: Context) : RecyclerView.Adapter<QuickViewHolder>() {
    private val settingList = mutableListOf<TFSettingItem>()
    private val activeColor: Int = context.resources.getColor(R.color.design_default_color_primary)
    private val inactiveColor: Int = context.resources.getColor(R.color.colorAccent)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quick, parent, false)
        return QuickViewHolder(view)
    }

    override fun getItemCount(): Int {
        return settingList.size
    }

    private var lastChecked: RadioButton? = null

    private var lastCheckedPos = 0

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int) {
        val item = settingList[position]
        holder.title.text = item.title
        holder.radioButton.isChecked = item.active
        holder.radioButton.tag = position

        if(position == 0 && item.active && holder.radioButton.isChecked)
        {
            lastChecked = holder.radioButton
            lastCheckedPos = 0
        }

        holder.radioButton.setOnClickListener { v ->
            val cb = v as RadioButton
            val clickedPos = (cb.tag as Int).toInt()

            if (cb.isChecked) {
                if (lastChecked != null) {
                    lastChecked?.isChecked = false
                    settingList[lastCheckedPos].active = false
                }

                lastChecked = cb
                lastCheckedPos = clickedPos
            } else
                lastChecked = null

            //settingList[clickedPos].active = cb.isChecked
            EventBus.getDefault().post(SettingChangedEvent(clickedPos, activeStateChanged = true))
        }

        holder.card.strokeColor = if (item.active) activeColor else inactiveColor
    }
    fun updateData(newList : List<TFSettingItem>) {
        settingList.clear()
        settingList.addAll(newList)
        notifyDataSetChanged()
    }

}

class QuickViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val card = view.card
    val radioButton = view.checkbox
    val title = view.item_title
}