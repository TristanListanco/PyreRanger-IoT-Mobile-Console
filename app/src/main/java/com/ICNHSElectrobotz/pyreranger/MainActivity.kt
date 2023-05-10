package com.ICNHSElectrobotz.pyreranger

import ArticleAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var sensorRecyclerView: RecyclerView

    private lateinit var recyclerView: RecyclerView
    private lateinit var articleAdapter: ArticleAdapter

    private val BASE_URL = "https://io.adafruit.com/api/v2/tannyencina/feeds/gas/"
    private val FEED_KEY = "gas"

    // Define the API interface
    interface AdafruitIOAPI {
        @GET("feeds/{feed_key}")
        fun getFeed(@Path("feed_key") feedKey: String): Call<Feed>
    }

    // Define the Feed data class
    data class Feed(
        val id: String,
        val name: String,
        val last_value: String,
        val created_at: String
    )


    data class Article(val title: String, val description: String)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of the AdafruitIOAPI interface
        val adafruitIOAPI = retrofit.create(AdafruitIOAPI::class.java)

//        // Make a request to the API to get the feed with the key "my_feed_key"
//        adafruitIOAPI.getFeed(FEED_KEY).enqueue(object : Callback<Feed> {
//            override fun onResponse(call: Call<Feed>, response: Response<Feed>) {
//                if (response.isSuccessful) {
//                    val feed = response.body()
//                    // Use the feed data to update your UI
//                    sensor_value.text = "${feed?.last_value} ppm"
//                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(feed?.created_at)
//                    date_text_view.text = SimpleDateFormat("EEE, MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(date)
//                } else {
//                    // Handle the error
//                    Log.e("MainActivity", "Error: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<Feed>, t: Throwable) {
//                // Handle the failure
//                Log.e("MainActivity", "Error: ${t.message}")
//            }
//        })

        sensorRecyclerView = findViewById(R.id.sensor_list)

        val sensors = listOf(
            Sensor("MQ131", "Sensor for ozone (O3)"),
            Sensor("MP503", "Sensor for alcohol (C2H5OH)"),
            Sensor("PMS5003", "Sensor for particulate matter (PM)"),
            Sensor("MHZ19-B", "Sensor for carbon dioxide (CO2)")
        )

        val adapter = SensorAdapter(sensors) { sensor ->
            showSensorDialog(sensor)
        }

        sensorRecyclerView.layoutManager = LinearLayoutManager(this)
        sensorRecyclerView.adapter = adapter

        // Find the RecyclerView in the layout
        recyclerView = findViewById(R.id.recyclerview)

        // Set the layout manager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create an instance of the ArticleAdapter and set it on the RecyclerView
        articleAdapter = ArticleAdapter(getArticles())
        recyclerView.adapter = articleAdapter
    }


    private fun getArticles(): List<Article> {
        return listOf(
            Article("Managing Single-Use Plastics", "Single-use plastics, or disposable plastics, are used only once before they are thrown away or recycled. These items are things like plastic bags, straws, coffee stirrers, soda and water bottles and most food packaging. We produce roughly 300 million tons of plastic each year and half of it is disposable!"),
            Article("Air Quality Monitoring", "Air pollution is a major environmental risk to health. By reducing air pollution levels, we can help countries reduce the global burden of disease from respiratory infections, heart disease, and lung cancer."),
            Article("The Impact of Our Plastic Waste", "The earth's oceans are filling up with plastic debris, and it is not only unsightly. The plastic pollution harms marine life and poses a threat to human health as well. The damage that plastics cause to marine life is well documented, but scientists are now discovering that it is a threat to humans as well.")
        )
    }

    private fun showSensorDialog(sensor: Sensor) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Sensor Name")
            .setMessage(sensor.name)
            .setPositiveButton("OK", null)
            .create()

        dialog.show()
    }
}

class SensorAdapter(private val sensors: List<Sensor>, private val onClick: (Sensor) -> Unit) :
    RecyclerView.Adapter<SensorAdapter.SensorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sensor_item, parent, false)
        return SensorViewHolder(view)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val sensor = sensors[position]
        holder.bind(sensor)
        // Set the click listener for the card view
        holder.itemView.setOnClickListener {
            val intent = when (sensor.name) {
                "MQ131" -> {
                    // Create an intent to go to the MQ131 activity
                    Intent(holder.itemView.context, MQ131Activity::class.java)
                }
                "MP503" -> {
                    // Create an intent to go to the MP503 activity
                    Intent(holder.itemView.context, MP503Activity::class.java)
                }
                "PMS5003" -> {
                    // Create an intent to go to the PMS5003 activity
                    Intent(holder.itemView.context, PMS5003Activity::class.java)
                }
                "MHZ19-B" -> {
                    // Create an intent to go to the MHZ19-B activity
                    Intent(holder.itemView.context, MHZ19BActivity::class.java)
                }
                else -> {
                    // Do nothing if sensor other than MQ131, MP503, PMS5003, or MHZ19-B is clicked
                    return@setOnClickListener
                }
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return sensors.size
    }

    class SensorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.sensor_name)
        private val descriptionTextView: TextView =
            itemView.findViewById(R.id.sensor_description)
        private val sensorCardView: CardView = itemView.findViewById(R.id.sensor_cardview)

        fun bind(sensor: Sensor) {
            nameTextView.text = sensor.name
            descriptionTextView.text = sensor.description
        }
    }
}

data class Sensor(val name: String, val description: String)
