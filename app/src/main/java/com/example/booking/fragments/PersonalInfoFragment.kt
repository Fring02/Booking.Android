package com.example.booking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.booking.R
import com.example.booking.apis.UsersApi
import com.example.booking.dialogs.ChangePasswordDialog
import com.example.booking.models.User
import com.example.booking.utils.PreferencesFactory
import kotlinx.android.synthetic.main.fragment_personal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalInfoFragment(private var api: UsersApi) : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = PreferencesFactory.getPreferences(requireContext()).getString("userId", "")
        val token = PreferencesFactory.getPreferences(requireContext()).getString("token", "")
        api.getUserById(userId!!, "Bearer $token").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                userFirstname.text = user!!.firstName
                userLastname.text = user.lastName
                userEmail.text = user.email
                userPhone.text = user.mobilePhone
                updatePasswordBtn.setOnClickListener {
                    ChangePasswordDialog(api).show(activity!!.supportFragmentManager, "CHANGE_PASSWORD_DIALOG")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                throw t
            }

        })
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }
}