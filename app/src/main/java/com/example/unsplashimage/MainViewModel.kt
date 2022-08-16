package com.example.unsplashimage

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.unsplashimage.data.UnSplashRepository

class MainViewModel: ViewModel() {

    private val repository: UnSplashRepository= UnSplashRepository()

    private val searchQuery= MutableLiveData<String>(DEFAULT_QUERY)

    val photos = searchQuery.switchMap { currentQuery ->
        Log.d(TAG, "viewmodel: inside switchmap, current query is ${currentQuery}")
        //repository.getSearchResults(currentQuery).cachedIn(viewModelScope)
        repository.getSearchResultsWithoutRetrofit(currentQuery)
    }

    fun searchPhotos(query: String){
     searchQuery.value = query
    }


    companion object {
        private const val DEFAULT_QUERY = "Chennai"
    }
}