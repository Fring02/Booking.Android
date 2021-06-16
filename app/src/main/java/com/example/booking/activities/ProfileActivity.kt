package com.example.booking.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.booking.R
import com.example.booking.adapters.ProfilePagerAdapter
import com.example.booking.apis.RequestsApi
import com.example.booking.apis.UsersApi
import com.example.booking.fragments.BookingRequestsFragment
import com.example.booking.fragments.PersonalInfoFragment
import com.example.booking.utils.BookingApi
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var usersApi: UsersApi
    private lateinit var requestsApi: RequestsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        usersApi = BookingApi.getInstance()!!.create(UsersApi::class.java)
        requestsApi = BookingApi.getInstance()!!.create(RequestsApi::class.java)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = ProfilePagerAdapter(this, listOf(PersonalInfoFragment(usersApi), BookingRequestsFragment(requestsApi)))
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if(position == 0) tab.text = "Personal"
            else tab.text = "My requests"
        }.attach()

        returnToServicesBtn2.setOnClickListener { finish() }
    }
}