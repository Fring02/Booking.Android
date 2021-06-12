package com.example.booking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.booking.apis.UsersApi
import com.auth0.android.jwt.JWT
import com.example.booking.R
import com.example.booking.exceptions.ApiException
import com.example.booking.exceptions.ValidateException
import kotlinx.android.synthetic.main.activity_registration.*
import com.example.booking.models.RegistrationUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.booking.utils.BookingApi
import com.example.booking.utils.PreferencesFactory
import kotlinx.android.synthetic.main.activity_login.*

class RegistrationActivity : AppCompatActivity() {
    private lateinit var api: UsersApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        if(isUserAuthorized()){
            moveToServices()
        } else {
            api = BookingApi.getInstance()!!.create(UsersApi::class.java)
            regBtn.setOnClickListener {
                try {
                    val user = RegistrationUser(
                        firstName = firstname.text.toString(),
                        lastName = lastname.text.toString(),
                        email = email.text.toString(),
                        mobilePhone = phone.text.toString(),
                        password = password.text.toString()
                    )
                    signUp(user)
                } catch (e: ValidateException){
                    error.text = e.message
                }
                catch (e: ApiException) {
                    throw e
                }
            }
            signInBtn.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

    }


    private fun signUp(user: RegistrationUser) {
        if (!validateUser(user)){
            loginError.visibility = View.VISIBLE
            throw ValidateException("Fill all fields for registration")
        }
        val register = api.register(user)
        register.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(!response.isSuccessful) {
                    val message = response.errorBody()?.charStream()?.readText()
                    error.visibility = View.VISIBLE
                    error.text = if(response.code() < 500) message else "Server technical issues. Please try later."
                }  else {
                    val tokenString = response.body()!!
                    val parsedToken = JWT(tokenString)
                    val editor = PreferencesFactory.getPreferences(applicationContext).edit()
                    editor.putString("userId", parsedToken.claims["nameid"]?.asString())
                    editor.putString("token", tokenString)
                    editor.apply()
                    startActivity(Intent(this@RegistrationActivity, ServicesActivity::class.java))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                throw ApiException(t.message!!)
            }
        })
    }


    private fun validateUser(user: RegistrationUser) : Boolean{
        return user.firstName.any() && user.lastName.any() &&
                user.email.any() && user.password.any() && user.mobilePhone.any()
    }

    private fun isUserAuthorized() : Boolean {
        return PreferencesFactory.getPreferences(this).contains("userId")
    }


    private fun moveToServices(){
        finish()
        overridePendingTransition(0, 0)
        startActivity(Intent(this, ServicesActivity::class.java))
        overridePendingTransition(0, 0)
    }
}