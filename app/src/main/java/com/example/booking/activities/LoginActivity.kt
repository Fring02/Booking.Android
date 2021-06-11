package com.example.booking.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.auth0.android.jwt.JWT
import com.example.booking.R
import com.example.booking.apis.UsersApi
import com.example.booking.config.ApiSettings
import com.example.booking.exceptions.ApiException
import com.example.booking.exceptions.ValidateException
import com.example.booking.models.LoginUser
import com.example.booking.utils.BookingApi
import com.example.booking.utils.PreferencesFactory
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import kotlin.error

class LoginActivity : AppCompatActivity() {
    private lateinit var api: UsersApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        api = BookingApi.getInstance()!!.create(UsersApi::class.java)
        loginBtn.setOnClickListener {
            try{
                val user = LoginUser(loginEmail.text.toString(), loginPassword.text.toString())
                signIn(user)
            } catch (e: ValidateException){
                loginError.text = e.message
            } catch (e: ApiException){
                throw e
            }
        }
        returnToRegisterBtn.setOnClickListener { finish() }
    }


    private fun signIn(user: LoginUser) {
        if(!validateUser(user)){
            loginError.visibility = View.VISIBLE
            throw ValidateException("Fill all fields for login")
        }
        val login = api.login(user)
        login.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(!response.isSuccessful) {
                    val message = response.errorBody()?.charStream()?.readText()
                    loginError.visibility = View.VISIBLE
                    if(response.code() < 500){
                        loginError.text = message
                    } else {
                        loginError.text = "Server technical issues. Please try later."
                    }
                }  else {
                    val tokenString = response.body()!!
                    val parsedToken = JWT(tokenString)
                    val editor = PreferencesFactory.getPreferences(applicationContext).edit()
                    editor.putString("userId", parsedToken.claims["nameid"]?.asString())
                    editor.putString("token", tokenString)
                    editor.apply()
                    finish()
                    startActivity(Intent(this@LoginActivity, ServicesActivity::class.java))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                throw ApiException(t.message!!)
            }
        })
    }

    private fun validateUser(user: LoginUser) : Boolean {
        return user.email.any() && user.password.any()
    }
}