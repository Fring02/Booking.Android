package com.example.booking.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booking.R
import com.example.booking.adapters.BookingRequestsAdapter
import com.example.booking.apis.RequestsApi
import com.example.booking.apis.UsersApi
import com.example.booking.models.BookingRequest
import com.example.booking.models.User
import com.example.booking.utils.PreferencesFactory
import kotlinx.android.synthetic.main.fragment_personal.*
import kotlinx.android.synthetic.main.fragment_requests.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingRequestsFragment (private var api: RequestsApi) : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = PreferencesFactory.getPreferences(requireContext()).getString("userId", "")
        val token = PreferencesFactory.getPreferences(requireContext()).getString("token", "")
        api.getRequestsByUserId(userId!!, "Bearer $token").enqueue(object : Callback<List<BookingRequest>>{
            override fun onResponse(call: Call<List<BookingRequest>>, response: Response<List<BookingRequest>>) {
                if(response.isSuccessful){
                    val requests = response.body()
                    requestsRecyclerView.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = BookingRequestsAdapter(requests!!, activity!!, api)
                    }
                }
            }

            override fun onFailure(call: Call<List<BookingRequest>>, t: Throwable) {
                throw t
            }
        })
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }
}