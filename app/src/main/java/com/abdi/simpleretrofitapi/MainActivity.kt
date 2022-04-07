package com.abdi.simpleretrofitapi

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.abdi.simpleretrofitapi.api.ApiRequest
import com.abdi.simpleretrofitapi.api.BASE_URL
import com.abdi.simpleretrofitapi.databinding.ActivityMainBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backgroundAnimation()
        makeApiRequest()

        binding.floatingActionButton.setOnClickListener {
            binding.floatingActionButton.animate().apply {
                rotationBy(360f)
                duration = 1000
            }.start()
            makeApiRequest()
            binding.ivRandomPict.visibility = View.GONE
        }
    }

    private fun backgroundAnimation() {
        val animationDrawable : AnimationDrawable = binding.rlLayout.background as AnimationDrawable

        animationDrawable.apply {
            setEnterFadeDuration(1000)
            setExitFadeDuration(3000)
            start()
        }
    }

    private fun makeApiRequest() {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getRandomDog()
                Log.d("Main", "Sise : ${response.fileSizeBytes}")

                if (response.fileSizeBytes < 400_000) {
                    withContext(Dispatchers.Main){
                        Glide.with(applicationContext).load(response.url).into(binding.ivRandomPict)
                        binding.ivRandomPict.visibility = View.VISIBLE
                    }
                }else{
                    makeApiRequest()
                }
            }catch (e : Exception){
                Log.d("Main", "Error: ${e.message}")
            }
        }

    }
}