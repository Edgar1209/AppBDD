package com.example.app_redes

import java.io.Serializable

data class Mascota(
    val color: String,
    val descripcion: String,
    val genero: String,
    val id: Int,
    val raza: String,
    val status: String,
    val id_usuario: Int,
    val nacimiento: String,
    val nombre: String
): Serializable