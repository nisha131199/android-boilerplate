package com.example.baseproject.data.domain.viewmodel

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.example.baseproject.data.api.Resource
import com.example.baseproject.data.domain.repository.Repository
import com.example.baseproject.data.model.ApiResponse
import com.example.baseproject.storage.PreferenceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ViewModel @Inject constructor(
        val app: Application,
        val repository: Repository,
        var handler: PreferenceHandler
    ): ViewModel() {

    private var _response = MutableStateFlow<Resource<ApiResponse?>>(Resource.Loading())
    val response: StateFlow<Resource<ApiResponse?>> = _response

    fun test() {
        viewModelScope.launch {
            repository.test()
                .catch {
                    _response.value = Resource.Error(it.message.toString())
                }
                .collect {
                    _response.value = Resource.Success(it.data)
                }
        }
    }

    //TODO (Usage in activity)
    //  lifecycleScope.launch {
    //            repeatOnLifecycle(Lifecycle.State.STARTED) {
    //                viewModel.response.collect {
    //                    when (it) {
    //                        is Resource.Success -> {
    //                        }
    //                        is Resource.Loading -> {
    //                        }
    //                        is Resource.Error -> {
    //                        }
    //                        is Resource.InvalidToken -> {
    //                        }
    //                    }
    //                }
    //            }
    //        }
}