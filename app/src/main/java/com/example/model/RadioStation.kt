package com.example.model

data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val streamUrl: String,
    val category: String,
    val description: String,
    val website: String = ""
)
