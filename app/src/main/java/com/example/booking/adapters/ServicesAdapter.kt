package com.example.booking.adapters

import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.booking.R
import com.example.booking.activities.ServiceActivity
import com.example.booking.models.LeisureServiceElement
import com.example.booking.utils.LoadImageTask

class ServicesAdapter(private var services: List<LeisureServiceElement>, private var context: Context) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {
    class ServiceViewHolder(service: View) : RecyclerView.ViewHolder(service) {
         var image: ImageView
         var name: TextView
         var location: TextView
        var rating: RatingBar
         var learnMoreBtn: Button
        init {
            image = service.findViewById(R.id.image)
            name = service.findViewById(R.id.name)
            location = service.findViewById(R.id.location)
            rating = service.findViewById(R.id.rating)
            learnMoreBtn = service.findViewById(R.id.learnMoreBtn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ic_service_element, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val image = services[position].images.firstOrNull()
        val url: String
        url = image?.path
                ?: "https://ufodex.io/img/placeholder.png"
        LoadImageTask(holder.image).execute(url)
        holder.name.text = services[position].name
        holder.location.text = services[position].location
        holder.rating.rating = services[position].rating.toFloat()
        holder.learnMoreBtn.setOnClickListener {
            context.startActivity(Intent(context, ServiceActivity::class.java).putExtra("serviceId", services[position].id))
        }
    }

    override fun getItemCount(): Int {
        return services.size
    }
}