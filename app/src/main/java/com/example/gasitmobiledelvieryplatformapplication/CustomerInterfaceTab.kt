package com.example.gasitmobiledelvieryplatformapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.gasitmobiledelvieryplatformapplication.R
import com.example.gasitmobiledelvieryplatformapplication.fragments.CheckYourLocation
import com.example.gasitmobiledelvieryplatformapplication.fragments.OrderToGasIT
import com.example.gasitmobiledelvieryplatformapplication.fragments.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.*
import com.google.android.material.tabs.TabLayout.Tab as Tab1

class CustomerInterfaceTab : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_interface_tab)

        setUpTabs()
    }

    private fun setUpTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(OrderToGasIT(), "Order to GasIT")
        adapter.addFragment(CheckYourLocation(), "Check Your Location")

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_storefront_24)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_person_pin_24)
    }
}