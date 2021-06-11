package com.example.booking.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.booking.R
import com.example.booking.apis.UsersApi
import com.example.booking.models.UpdateUser
import com.example.booking.utils.BookingApi
import com.example.booking.utils.PreferencesFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordDialog(private var api: UsersApi) : DialogFragment() {
    private lateinit var newPassword: EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newPassword = view.findViewById(R.id.newPassword)
        val btn = view.findViewById<Button>(R.id.updatePassBtn)
        btn.setOnClickListener {
            if(!newPassword.text.any()) Toast.makeText(context, "Password should not be empty", Toast.LENGTH_SHORT).show()
            else {
                val userId = PreferencesFactory.getPreferences(requireContext()).getString("userId", "")
                val token = PreferencesFactory.getPreferences(requireContext()).getString("token", "")
                api.updateUserById(userId!!, "Bearer $token", UpdateUser(newPassword.text.toString())).enqueue(object : Callback<String>{
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if(response.isSuccessful) {
                            dialog?.cancel()
                            Toast.makeText(context, "Your password was updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to update your password", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        throw t
                    }

                })
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_change_password, container, false)
    }
}