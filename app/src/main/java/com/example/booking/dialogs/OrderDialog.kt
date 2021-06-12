package com.example.booking.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.booking.R
import com.example.booking.activities.LoginActivity
import com.example.booking.apis.RequestsApi
import com.example.booking.models.BookingRequest
import com.example.booking.utils.PreferencesFactory
import kotlinx.android.synthetic.main.activity_service.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDialog(private var serviceId: String, private var api: RequestsApi) : DialogFragment() {
    private lateinit var days: EditText
    private lateinit var hours: EditText
    private lateinit var minutes: EditText
    private lateinit var btn: Button
    private lateinit var info: EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        days = view.findViewById(R.id.days)
        hours = view.findViewById(R.id.hours)
        minutes = view.findViewById(R.id.minutes)
        info = view.findViewById(R.id.info)
        btn = view.findViewById(R.id.submitOrderBtn)
        btn.setOnClickListener {
            val userId = PreferencesFactory.getPreferences(requireContext()).getString("userId", "")
            val token = PreferencesFactory.getPreferences(requireContext()).getString("token", "")
            if(userId == null || token == null) {
                dialog?.cancel()
                startActivity(Intent(context, LoginActivity::class.java))
            } else {
               createRequest(userId, token)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }


    private fun validate(days: String, hours: String, minutes: String) : Boolean{
        return days.any() || hours.any() || minutes.any()
    }


    private fun createRequest(userId: String, token: String) {
        var days = days.text.toString()
        var hours = hours.text.toString()
        var minutes = minutes.text.toString()
        val info = info.text.toString()
        if(validate(days, hours, minutes)){
            if(days.toIntOrNull() == null) {
                days = "00"
            } else {
                if(days.toInt() < 10) days = "0$days"
            }
            if(hours.toIntOrNull() == null) {
                hours = "00"
            } else {
                if(hours.toInt() < 10) hours = "0$hours"
            }
            if(minutes.toIntOrNull() == null) {
                minutes = "00"
            } else {
                if(minutes.toInt() < 10) minutes = "0$minutes"
            }
            val request = BookingRequest(null, serviceId, "0.$days:$hours:$minutes.0000", userId, info, null)
            api.createRequest(request, "Bearer $token").enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if(response.isSuccessful){
                        dialog?.cancel()
                        val orderBtn = activity!!.findViewById<Button>(R.id.orderBtn)
                        orderBtn.text = "Ordered"
                        orderBtn.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                        orderBtn.isClickable = false
                    } else {
                        if(response.code() == 401 || response.code() == 403){
                            dialog?.cancel()
                            startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    throw t
                }

            })
        }
    }
}