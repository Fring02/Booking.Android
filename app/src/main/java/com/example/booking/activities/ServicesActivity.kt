package com.example.booking.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booking.R
import com.example.booking.adapters.ServicesAdapter
import com.example.booking.apis.ServicesApi
import com.example.booking.config.ApiSettings
import com.example.booking.dialogs.FilterDialog
import com.example.booking.models.LeisureServiceElement
import com.example.booking.utils.BookingApi
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_service.*
import kotlinx.android.synthetic.main.activity_services.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ServicesActivity : AppCompatActivity() {
    private lateinit var api: ServicesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        api = BookingApi.getInstance()!!.create(ServicesApi::class.java)
        if(intent.hasExtra("filtered")) {
            loadFilteredServices(intent.getStringExtra("workingTime")!!, intent.getStringExtra("categoryName")!!,
            intent.getIntExtra("rating", 0))
        } else {
            loadAllServices()
        }
        filterBtn.setOnClickListener {
            FilterDialog().show(supportFragmentManager, "FILTER_DIALOG")
        }
        profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }


    private fun loadAllServices(){
        val servicesCall = api.getServices()
        servicesCall.enqueue(object : Callback<ArrayList<LeisureServiceElement>> {
            override fun onResponse(
                call: Call<ArrayList<LeisureServiceElement>>,
                response: Response<ArrayList<LeisureServiceElement>>
            ) {
                if(response.isSuccessful){
                    render(response.body())
                }
            }

            override fun onFailure(call: Call<ArrayList<LeisureServiceElement>>, t: Throwable) {
                throw t
            }
        })
    }


    private fun loadFilteredServices(workingTime: String, categoryName: String, rating: Int) {
        api.filterServices(workingTime, rating, categoryName).enqueue(object : Callback<ArrayList<LeisureServiceElement>> {
            override fun onResponse(call: Call<ArrayList<LeisureServiceElement>>, response: Response<ArrayList<LeisureServiceElement>>) {
                if(response.isSuccessful){
                    render(response.body())
                }
            }

            override fun onFailure(call: Call<ArrayList<LeisureServiceElement>>, t: Throwable) {
                throw t
            }

        })
    }



    private fun render(servicesArr: List<LeisureServiceElement>?){
        if(servicesArr == null || !servicesArr.any()) {
            warning.visibility = View.VISIBLE
        } else {
            services.apply {
                layoutManager = LinearLayoutManager(this@ServicesActivity)
                adapter = ServicesAdapter(servicesArr, this@ServicesActivity)
            }
        }
    }
}