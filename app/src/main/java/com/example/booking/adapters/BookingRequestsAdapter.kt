package com.example.booking.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.booking.R
import com.example.booking.activities.ProfileActivity
import com.example.booking.activities.ServiceActivity
import com.example.booking.apis.RequestsApi
import com.example.booking.models.BookingRequest
import com.example.booking.utils.PreferencesFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingRequestsAdapter(private var requests: List<BookingRequest>, private var activity: Activity,
private var api: RequestsApi) :
        RecyclerView.Adapter<BookingRequestsAdapter.RequestsViewHolder>() {
    class RequestsViewHolder(request: View) : RecyclerView.ViewHolder(request) {
        var name: TextView
        var categoryName: TextView
        var rating: RatingBar
        var learnMoreBtn: Button
        var bookingTime: TextView
        var deleteBtn: Button
        var infoBtn: Button
        init {
            name = request.findViewById(R.id.reqServName)
            rating = request.findViewById(R.id.reqServRating)
            learnMoreBtn = request.findViewById(R.id.reqMoreBtn)
            categoryName = request.findViewById(R.id.reqServCategory)
            bookingTime = request.findViewById(R.id.bookingTime)
            deleteBtn = request.findViewById(R.id.deleteRequestBtn)
            infoBtn = request.findViewById(R.id.reqServInfo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {
        val itemView =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.ic_booking_request, parent, false)
        return RequestsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        holder.name.text = requests[position].service!!.name
        holder.categoryName.text = requests[position].service!!.category.name
        holder.rating.rating = requests[position].service!!.rating.toFloat()
        holder.bookingTime.text = requests[position].bookingTime
        holder.learnMoreBtn.setOnClickListener {
            activity.startActivity(Intent(activity, ServiceActivity::class.java).putExtra("serviceId", requests[position].serviceId))
        }
        holder.deleteBtn.setOnClickListener {
            val token = PreferencesFactory.getPreferences(activity).getString("token", "")!!
            api.deleteRequestById(requests[position].id!!, "Bearer $token").enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if(response.isSuccessful){
                        activity.finish()
                        activity.overridePendingTransition(0, 0)
                        activity.startActivity(Intent(activity, ProfileActivity::class.java))
                        activity.overridePendingTransition(0, 0)
                    } else {

                        Toast.makeText(activity, "Failed to delete request: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    throw t
                }

            })
        }

        holder.infoBtn.setOnClickListener {
            val info = requests[position].info
            AlertDialog.Builder(activity).setMessage(if(info.any()) info else "You didn't wish anything").setTitle("Request wishes").setPositiveButton("Close") { dialog, id ->
                dialog.cancel()
            }.show()
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}