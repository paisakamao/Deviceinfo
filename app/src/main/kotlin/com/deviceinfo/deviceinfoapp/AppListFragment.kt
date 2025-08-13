package com.deviceinfo.deviceinfoapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deviceinfo.deviceinfoapp.R
import com.deviceinfo.deviceinfoapp.adapter.AppListAdapter
import com.deviceinfo.deviceinfoapp.model.AppInfo
import com.deviceinfo.deviceinfoapp.utils.AppInfoHelper

class AppListFragment : Fragment() {

    private lateinit var appType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appType = arguments?.getString(ARG_APP_TYPE) ?: "user"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.fragmentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val appInfoHelper = AppInfoHelper(requireContext())
        
        val appList: List<AppInfo> = when (appType) {
            "user" -> appInfoHelper.getUserAppsDetails()
            "system" -> appInfoHelper.getSystemAppsDetails()
            "all" -> appInfoHelper.getAllAppsDetails()
            "disabled" -> appInfoHelper.getDisabledAppsDetails()
            else -> emptyList()
        }
        
        recyclerView.adapter = AppListAdapter(appList)
        return view
    }

    companion object {
        private const val ARG_APP_TYPE = "app_type"

        @JvmStatic
        fun newInstance(appType: String) =
            AppListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_APP_TYPE, appType)
                }
            }
    }
}