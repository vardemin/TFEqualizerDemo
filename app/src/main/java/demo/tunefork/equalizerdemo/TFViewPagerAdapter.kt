package demo.tunefork.equalizerdemo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class TFViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        const val NUM_PAGES = 2
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return TFSettingsFragment()
            }
            1 -> {
                return TFPlayerFragment()
            }
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return NUM_PAGES
    }
}