package demo.tunefork.equalizerdemo

import android.media.audiofx.Equalizer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_settings.*

import kotlinx.android.synthetic.main.dialog_settings.view.*
import org.greenrobot.eventbus.EventBus

class NewTFSettingsDialog : BottomSheetDialogFragment(), View.OnClickListener {
    private var title: String = ""
    private lateinit var settings: Equalizer.Settings

    companion object {
        val TAG = "NEW_TF_STTING_DIALOG"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.dialog_settings, container, false)

        val equalizer = EventBus.getDefault().getStickyEvent(Equalizer::class.java)
        settings = Equalizer.Settings(equalizer.properties.toString())

        v.btn_new.setOnClickListener(this)

        v.input_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                title = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        val linearLayout: LinearLayout = v.findViewById(R.id.settings_container)

        val minEQLevel = TFSettingItem.minEqLevel
        val maxEQLevel = TFSettingItem.maxEqLevel

        for (i in 0 until settings.numBands) {

            val freqTextView = TextView(context)
            freqTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            freqTextView.gravity = Gravity.CENTER_HORIZONTAL
            freqTextView.text = (equalizer.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
            linearLayout.addView(freqTextView)

            val row = LinearLayout(context)
            row.orientation = LinearLayout.HORIZONTAL

            val minDbTextView = TextView(context)
            minDbTextView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            minDbTextView.text = (minEQLevel / 100).toString() + " dB"

            val maxDbTextView = TextView(context)
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
            val bar = SeekBar(context)
            bar.layoutParams = layoutParams
            bar.max = maxEQLevel - minEQLevel
            bar.progress = equalizer.getBandLevel(i.toShort()).toInt() - minEQLevel

            bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar, progress: Int,
                    fromUser: Boolean
                ) {
                    settings.bandLevels[i] = (progress + minEQLevel).toShort()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })

            row.addView(minDbTextView)
            row.addView(bar)
            row.addView(maxDbTextView)

            linearLayout.addView(row)
        }
        linearLayout.invalidate()


        return v

    }

    override fun onClick(v: View?) {
        val id = v?.id
        when (id) {
            R.id.btn_new -> {
                if (input_title.length() <= input_layout.counterMaxLength) {
                    EventBus.getDefault().post(NewSettingEvent(title, settings))
                    dismissAllowingStateLoss()
                }
            }
        }
    }

}