package com.deviceinfo.deviceinfoapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.deviceinfo.deviceinfoapp.ui.AppListFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val tabTypes = listOf("user", "system", "all", "disabled")

    override fun getItemCount(): Int = tabTypes.size

    override fun createFragment(position: Int): Fragment {
        // Create a new fragment instance for the given tab
        return AppListFragment.newInstance(tabTypes[position])
    }
}