package com.example.examverylast

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pref = getPreferences(Context.MODE_PRIVATE)
        val editor = pref.edit()

        val res = pref.getString("res", "")
        Answer.text = res

        GlobalScope.launch {
            run("https://en.wikipedia.org/wiki/The_Cabinet_of_Dr._Caligari")
        }

        onSave.setOnClickListener {
            editor.putString("res", Answer.text.toString())
            editor.apply()
        }

        onClear.setOnClickListener {
            editor.clear()
            editor.apply()
            Answer.text = "0"
        }

        onImport.setOnClickListener {
            val intent = Intent(this@MainActivity, Second::class.java)
            intent.putExtra("r", Answer.text.toString())
            startActivity(intent)
        }

        onWeb.setOnClickListener {
            GlobalScope.launch {
                getHtmlToWeb()
            }
        }
    }

    fun getHtmlToWeb(){
        Thread(Runnable{
            val stringBuilder = StringBuilder()
            try{
                val doc: org.jsoup.nodes.Document? = Jsoup.connect("https://en.wikipedia.org/wiki/The_Cabinet_of_Dr._Caligari").get()
                val title = doc?.title()
                val links: Elements? = doc?.select(".infobox tr")
                stringBuilder.append(title).append("\n")
                for(link in links!!){
                    stringBuilder.
                            append("\n").append(link.getElementsByTag("th").text()).
                            append(": ").append(link.getElementsByTag("td").text())
                }
            }
            catch(e:IOException){
                stringBuilder.append("Error: ").append(e.message).append("\n")
            }
            runOnUiThread { Answer.text = stringBuilder.toString() }
        }).start()
    }

    fun run(url:String){
        val req = Request.Builder().url(url).build()
        client.newCall(req).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) =
                    println("aa" + response.body()?.string())
        })
    }
}
//https://en.wikipedia.org/wiki/The_Cabinet_of_Dr._Caligari
//.infobox tr th td