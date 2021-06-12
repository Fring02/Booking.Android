package com.example.booking.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.booking.R
import com.example.booking.apis.RequestsApi
import com.example.booking.apis.ServicesApi
import com.example.booking.config.ApiSettings.ICON_ID
import com.example.booking.config.ApiSettings.LAYER_ID
import com.example.booking.config.ApiSettings.SOURCE_ID
import com.example.booking.dialogs.OrderDialog
import com.example.booking.models.LeisureService
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
    private var symbolLayerIconFeatureList: MutableList<Feature> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        setContentView(R.layout.activity_service)
        serviceMap.onCreate(savedInstanceState)
        serviceMap.getMapAsync{ mapboxMap ->
            /*val symbolLayerIconFeatureList: MutableList<Feature> = mutableListOf(Feature.fromGeometry(
                Point.fromLngLat(-56.990533, -30.583266)
            ))*/
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

        returnToServicesBtn.setOnClickListener { finish() }
    }




    private fun loadService(){
        val servicesCall = intent.getStringExtra("serviceId")?.let { servicesApi.getServiceById(it) }
        servicesCall?.enqueue(object : Callback<LeisureService> {
            override fun onResponse(
                call: Call<LeisureService>,
                response: Response<LeisureService>
            ) {
                val service = response.body()
                serviceName.text = service?.name
                serviceLocation.text = "${service?.location}"
                val website = service?.website ?: "No website"
                serviceWebsite.text = website
                serviceRating.rating = service?.rating?.toFloat()!!
                serviceTime.text = service.workingTime
                serviceDesc.text = service.description
                serviceCategory.text = service.category.name
                symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(service.longitude, service.latitude)))
                val image = service.images.firstOrNull()
                val url: String
                url = image?.path ?: "https://ufodex.io/img/placeholder.png"
                LoadImageTask(serviceImage).execute(url)
                val userId =
                    PreferencesFactory.getPreferences(this@ServiceActivity.applicationContext)
                        .getString(
                            "userId",
                            ""
                        )
                this@ServiceActivity.checkRequest(userId!!, service.id)
            }

            override fun onFailure(call: Call<LeisureService>, t: Throwable) {
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