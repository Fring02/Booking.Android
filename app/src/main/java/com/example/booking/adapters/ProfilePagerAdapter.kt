package com.example.booking.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.booking.apis.RequestsApi
import com.example.booking.apis.UsersApi
import com.example.booking.fragments.BookingRequestsFragment
import com.example.booking.fragments.PersonalInfoFragment
import com.example.booking.utils.BookingApi

class ProfilePagerAdapter(fa: FragmentActivity, private var fragments: List<Fragment>) :FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return fragments.size

    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}