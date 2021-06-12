package com.example.booking.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.booking.R
import com.example.booking.activities.ServicesActivity
import com.example.booking.apis.CategoriesApi
import com.example.booking.models.Category
import com.example.booking.utils.BookingApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class FilterDialog : DialogFragment() {
    private lateinit var filterTime: EditText
    private lateinit var categories: Spinner
    private lateinit var rating: Spinner
    private lateinit var btn: Button
    private lateinit var categoriesApi: CategoriesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesApi = BookingApi.getInstance()!!.create(CategoriesApi::class.java)
        filterTime = view.findViewById(R.id.filterTime)
        categories = view.findViewById(R.id.filterCategories)
        rating = view.findViewById(R.id.filterRating)
        btn = view.findViewById(R.id.searchBtn)
        btn.setOnClickListener {
            val intent = Intent(context, ServicesActivity::class.java).putExtra("filtered", true).
            putExtra("workingTime", filterTime.text.toString()).
                    putExtra("categoryName", if(categories.selectedItem.toString() == "None")
                        "" else categories.selectedItem.toString()).
                    putExtra("rating", if(rating.selectedItem.toString() == "Rating") 0 else rating.selectedItem.toString().toInt())
            activity?.finish()
            activity?.overridePendingTransition(0, 0)
            startActivity(intent)
            activity?.overridePendingTransition(0, 0)
        }
        setCategories(categories)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    private fun setCategories(categories: Spinner) {
        categoriesApi.getCategories().enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(call: Call<ArrayList<Category>>, response: Response<ArrayList<Category>>) {
                if(response.isSuccessful){
                    var categoriesArr = LinkedList<String>()
                    categoriesArr.addFirst("None")
                    categoriesArr.addAll(response.body()!!.map { c -> c.name })
                    var adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item,
                            categoriesArr)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categories.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                throw t
            }

        })
    }
}