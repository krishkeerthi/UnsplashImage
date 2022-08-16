package com.example.unsplashimage.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.unsplashimage.api.UnsplashApi
import com.example.unsplashimage.data.withoutlibrary.UnSplashPagingSourceWithoutLibrary

class UnSplashRepository {

    fun getSearchResults(query: String): LiveData<PagingData<UnsplashPhoto>>{
        return Pager(
            config = PagingConfig(
                pageSize =20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {UnSplashPagingSource(UnsplashApi.service, query)}
        ).liveData
    }

    fun getSearchResultsWithoutRetrofit(query: String): LiveData<PagingData<UnsplashPhoto>>{
        return Pager(
            config = PagingConfig(
                pageSize =20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {UnSplashPagingSourceWithoutLibrary(query)}
        ).liveData
    }
}