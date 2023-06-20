package com.example.app_redes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import org.json.JSONObject

class MascotaDetallada : AppCompatActivity() {
    var urlUsuario= "http://192.168.43.225:3000/usuario/"
    var urlMail="http://192.168.43.225:3000/sendmail/"
    private lateinit var dueñoEmail:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascota_detallada)
        val id = intent.getStringExtra("ID")
        val usuario = intent.getStringExtra("User")
        val mascota=intent.getSerializableExtra("Mascota") as Mascota

        val nombre:TextView=findViewById(R.id.nombreDetallada)
        val raza:TextView=findViewById(R.id.razaDetallada)
        val nacimiento:TextView=findViewById(R.id.nacimientoDetallada)
        val descripcion:TextView=findViewById(R.id.descripcionDetallada)
        val color:TextView=findViewById(R.id.colorDetallado)
        val genero:TextView=findViewById(R.id.generoDetallado)
        val estado:TextView=findViewById(R.id.estadoDetallado)
        val info:TextView=findViewById(R.id.infoContacto)
        val email:Button=findViewById(R.id.contacto)
        val regresar:Button=findViewById(R.id.returnDetallado)
        urlUsuario+="${mascota.id_usuario}"

        when (mascota.genero) {
            "F" -> genero.text="Genero: Femenino"
            "M" -> genero.text="Genero: Masculino"
            else -> genero.text="Genero: ${mascota.genero}"
        }

        nombre.text="Nombre: ${mascota.nombre}"
        raza.text="Raza: ${mascota.raza}"
        nacimiento.text="Fecha de Nacimiento: ${mascota.nacimiento}"
        descripcion.text="Descripcion: ${mascota.descripcion}"
        color.text="Color: ${mascota.color}"
        estado.text="Estado: ${mascota.status}"
        info.text="Si deseas contactar a la persona encargada de esta mascota presiona el icono del correo y se lo haremos saber"

        lifecycleScope.launch {
            dueñoEmail=getDueño()
        }
        regresar.setOnClickListener {
            val intent = Intent(this, MyPets:: class.java)
            intent.putExtra("ID", id)
            intent.putExtra("User", usuario)
            startActivity(intent)
        }
        email.setOnClickListener {
            lifecycleScope.launch {
                if (usuario != null) {
                    if (id != null) {
                        enviarMail(mascota.status,mascota.nombre,usuario,id)
                    }
                }
            }
        }
    }
    suspend fun getDueño(): String {
        var email=""
        val request = StringRequest(
            Request.Method.GET,
            urlUsuario,
            {response->
                val data = response.toString()
                val jsonObject = JSONObject(data)
                email=jsonObject["email"].toString()
            },
            {error-> Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("Error", error.toString())
            }
        )
        val rQueue = Volley.newRequestQueue(this@MascotaDetallada)
        rQueue.add(request)
        delay(2000)
        return email
    }
    suspend fun enviarMail(estado:String,nombre:String,usuario:String,idUser:String){
        Log.d("Intentando Contacto","")
        val params = HashMap<String, String>()
        params["mail"] = dueñoEmail
        params["body"]="$usuario esta interesado en conocer a $nombre"
        val jsonObject = JSONObject(params as Map<*, *>)
        Log.d("Json",jsonObject.toString())
        val request = object : JsonObjectRequest(
            Method.POST, urlMail, jsonObject,
            Response.Listener { response ->
                // manejar respuesta
                Log.d("Respuesta",response.toString())
                val intent = Intent(this, MyPets:: class.java)
                intent.putExtra("ID", idUser)
                intent.putExtra("User", nombre)
                startActivity(intent)
                Toast.makeText(this, "Contacto Exitoso", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                // manejar error
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                Log.e("Error:", error.toString())
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        val rQueue = Volley.newRequestQueue(this@MascotaDetallada)
        rQueue.add(request)

        delay(2000L)

    }
}
