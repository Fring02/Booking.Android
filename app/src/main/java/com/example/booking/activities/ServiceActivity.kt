package com.example.booking.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booking.R
import com.example.booking.adapters.CommentsAdapter
import com.example.booking.adapters.ServiceImagesAdapter
import com.example.booking.apis.CommentsApi
import com.example.booking.apis.RequestsApi
import com.example.booking.apis.ServicesApi
import com.example.booking.config.ApiSettings.ICON_ID
import com.example.booking.config.ApiSettings.LAYER_ID
import com.example.booking.config.ApiSettings.SOURCE_ID
import com.example.booking.dialogs.OrderDialog
import com.example.booking.fragments.ServiceImageFragment
import com.example.booking.models.Comment
import com.example.booking.models.CreateComment
import com.example.booking.models.LeisureService
import com.example.booking.models.UpdateService
import com.example.booking.utils.BookingApi
import com.example.booking.utils.LoadImageTask
import com.example.booking.utils.PreferencesFactory
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_service.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceActivity : AppCompatActivity() {
    private lateinit var servicesApi: ServicesApi
    private lateinit var requestsApi: RequestsApi
    private lateinit var commentsApi: CommentsApi
    private var symbolLayerIconFeatureList: MutableList<Feature> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_service)
        serviceMap.onCreate(savedInstanceState)
        serviceMap.getMapAsync{ mapboxMap ->
            mapboxMap.setStyle(
                Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                    .withImage(
                        ICON_ID,
                        BitmapFactory.decodeResource(resources, R.drawable.mapbox_marker_icon_default)
                    )
                    .withSource(
                        GeoJsonSource(
                            SOURCE_ID,
                            FeatureCollection.fromFeatures(symbolLayerIconFeatureList)
                        )
                    )
                    .withLayer(SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(
                        iconImage(ICON_ID), iconAllowOverlap(true),
                        iconIgnorePlacement(true)
                    )))
        }
        servicesApi = BookingApi.getInstance()!!.create(ServicesApi::class.java)
        loadService()
        requestsApi = BookingApi.getInstance()!!.create(RequestsApi::class.java)
        commentsApi = BookingApi.getInstance()!!.create(CommentsApi::class.java)
        returnToServicesBtn.setOnClickListener { finish() }

    }




    private fun loadService(){
        val servicesCall = intent.getStringExtra("serviceId")?.let { servicesApi.getServiceById(it) }
        servicesCall?.enqueue(object : Callback<LeisureService> {
            override fun onResponse(
                    call: Call<LeisureService>,
                    response: Response<LeisureService>
            ) {
                val service = response.body()!!
                serviceName.text = service.name
                serviceLocation.text = service.location
                val website = service.website ?: "No website"
                serviceWebsite.text = website
                serviceRating.rating = service.rating.toFloat()
                serviceTime.text = service.workingTime
                serviceDesc.text = service.description
                serviceCategory.text = service.category.name
                symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(service.longitude, service.latitude)))
                serviceRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    if (fromUser) {
                        val token = PreferencesFactory.getPreferences(this@ServiceActivity).getString("token", "")
                        servicesApi.updateServiceRating(service.id, UpdateService(rating.toInt()), "Bearer $token")
                                .enqueue(object : Callback<String> {
                                    override fun onResponse(call: Call<String>, response: Response<String>) {
                                        if (!response.isSuccessful) {
                                            Toast.makeText(this@ServiceActivity, "Failed to put rating", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                        throw t
                                    }
                                })
                    }
                }
                loadComments(service.id)
                val images = service.images
                val imageFragments = mutableListOf<Fragment>()
                if (!images.any()) {
                    imageFragments.add(ServiceImageFragment("https://ufodex.io/img/placeholder.png"))
                } else {
                    for (i in images) {
                        imageFragments.add(ServiceImageFragment(i.path))
                    }
                }
                imagePager.adapter = ServiceImagesAdapter(this@ServiceActivity, imageFragments)
                val userId =
                        PreferencesFactory.getPreferences(this@ServiceActivity.applicationContext)
                                .getString(
                                        "userId",
                                        ""
                                )
                commentBtn.setOnClickListener {
                    val text = commentText.text.toString()
                    val token = PreferencesFactory.getPreferences(this@ServiceActivity.applicationContext).getString("token","")!!
                    commentsApi.createComment(CreateComment(userId!!, service.id, text), "Bearer $token").enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            if(response.isSuccessful) {
                                finish()
                                overridePendingTransition(0, 0)
                                startActivity(Intent(this@ServiceActivity, ServiceActivity::class.java))
                                overridePendingTransition(0,0)
                            }
                        }
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            throw t
                        }
                    })
                }
                this@ServiceActivity.checkRequest(userId!!, service.id)

            }

            override fun onFailure(call: Call<LeisureService>, t: Throwable) {
                throw t
            }
        })
    }

    private fun loadComments(serviceId: String) {
        commentsApi.getComments(serviceId).enqueue(object : Callback<List<Comment>>{
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                val comments = response.body()!!
                commentsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(this@ServiceActivity)
                    adapter = CommentsAdapter(comments)
                }
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                throw t
            }
        })
    }

    private fun checkRequest(userId: String, serviceId: String){
        val token = PreferencesFactory.getPreferences(this).getString("token", "")
        requestsApi.checkRequest(userId, serviceId, "Bearer $token").enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val ordered = response.body()
                    if (ordered == true) {
                        orderBtn.text = "Ordered"
                        orderBtn.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
                        orderBtn.isClickable = false
                    } else {
                        orderBtn.setOnClickListener {
                            OrderDialog(serviceId, requestsApi).show(
                                supportFragmentManager,
                                "ORDER_FRAGMENT"
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                throw t
            }
        })
    }

    override fun onResume() {
        super.onResume()
        serviceMap.onResume()
    }

    override fun onStart() {
        super.onStart()
        serviceMap.onStart()
    }


    override fun onStop() {
        super.onStop()
        serviceMap.onStop()
    }

    override fun onPause() {
        super.onPause()
        serviceMap.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        serviceMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceMap.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        serviceMap.onSaveInstanceState(outState)
    }

}