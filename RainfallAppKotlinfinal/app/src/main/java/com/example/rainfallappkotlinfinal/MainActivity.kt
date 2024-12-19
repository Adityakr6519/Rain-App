package com.example.rainfallappkotlinfinal

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rainfallappkotlinfinal.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("delhi")
        searchCity()
    }
    private fun searchCity(){
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
    private fun fetchWeatherData(cityName: String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterFace::class.java)
        val response = retrofit.getWeatherData( city =cityName, appid= "", units="metric")
        response.enqueue(object : Callback<RainApp> {
            override fun onResponse(call: Call<RainApp>, response: Response<RainApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    //Log.d("TAG", "onResponse: $temperature")

                    binding.curTemp.text= "$temperature °C"
                    binding.weatherType.text= condition
                    binding.maxTempToday.text= "Max Temp: $maxTemp °C"
                    binding.minTempToday.text= "Min Temp: $minTemp °C"
                    binding.l2.curHumidity.text= "$humidity %"
                    binding.l5.wind.text= "$windSpeed m/s"
                    binding.l3.sunrise.text= "${time(sunRise)}"
                    binding.l4.Sunset.text= "${time(sunSet)}"
                    binding.l6.condition.text= condition
                    binding.Date.text= date()
                    binding.Day.text= dayName(System.currentTimeMillis())
                    binding.location.text="$cityName"
                    changeBgImage(condition)
                }
            }
            override fun onFailure(call: Call<RainApp>, t: Throwable) {
                TODO(reason = "Not yet implemented")
            }

        })
    }
    private fun changeBgImage(conditions: String){
        when(conditions){
            "Clouds","Partly Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Sunny","Clear Sky","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
    fun date(): CharSequence? {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

}