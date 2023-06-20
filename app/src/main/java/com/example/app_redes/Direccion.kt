package com.example.app_redes

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class Direccion : AppCompatActivity() {
    var urlDireccion = "http://192.168.43.225:3000/direccion/"
    var urlGet = "http://192.168.43.225:3000/estados/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direccion)
        val continuar : Button =findViewById(R.id.direccionContinue)
        val calle: EditText = findViewById(R.id.direccionCalle)
        val colonia: EditText = findViewById(R.id.direccionColonia)
        val estado: EditText = findViewById(R.id.direccionEstado)
        val exterior: EditText = findViewById(R.id.direccionExterior)
        val interior: EditText = findViewById(R.id.direccionInterior)
        val postal: EditText = findViewById(R.id.direccionPostal)
        val estados: TextView = findViewById(R.id.Estados)

        lifecycleScope.launch {
           estados.text= getEstados()
        }
        continuar.setOnClickListener {
            lifecycleScope.launch {
                registrar(calle.text.toString(),colonia.text.toString(),interior.text.toString(),exterior.text.toString(),postal.text.toString(),estado.text.toString())
            }
        }
    }

    suspend fun getEstados(): String {
        var lista=""
        val request = StringRequest(
            Request.Method.GET,
            urlGet,
            {response->
                val data = response.toString()
                var JArray = JSONArray(data)
                for (i in 0 until JArray.length()) {
                    val jsonObject = JArray.getJSONObject(i)
                    val id = jsonObject.getInt("id")
                    val nombre = jsonObject.getString("nombre")
                    lista+="ID: $id, Nombre: $nombre\n"
                }
                Log.d("Respuesta",lista)
            },
            {error-> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("Error", error.toString())
            }
        )
        val rQueue = Volley.newRequestQueue(this@Direccion)
        rQueue.add(request)
        delay(1000L)
        return lista
    }

    suspend fun registrar(nombre:String,colonia:String,interior:String,exterior:String,postal:String,estado:String){
        val params = HashMap<String, String>()
        params["calle"] = nombre
        params["colonia"] = colonia
        params["num_interior"] = interior
        params["num_exterior"] = exterior
        params["codigo_postal"] = postal
        params["id_estado"] = estado
        val jsonObject = JSONObject(params as Map<*, *>)
        Log.d("Json: ", jsonObject.toString())

        val request = object : JsonObjectRequest(
            Method.PUT, urlDireccion, jsonObject,
            Response.Listener { response ->
                // manejar respuesta
                Log.d("Respuesta",response.toString())
                Toast.makeText(this, "Direccion Numero ${response["id"]}", Toast.LENGTH_SHORT).show()
                val direccion = "${response["id"]}"
                Log.d("Direccion",direccion)
                val intent = Intent(this, Registro::class.java)
                intent.putExtra("Direccion", direccion)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                // manejar error
                Log.e("Error:", error.toString())
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        val rQueue = Volley.newRequestQueue(this@Direccion)
        rQueue.add(request)

        delay(2000)
    }
}