package com.example.unsplashimage.api

import com.example.unsplashimage.data.UnsplashPhoto

data class UnSplashResponse(
    val results: List<UnsplashPhoto>
)
