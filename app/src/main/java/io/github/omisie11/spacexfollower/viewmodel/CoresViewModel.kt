package io.github.omisie11.spacexfollower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.omisie11.spacexfollower.data.SpaceRepository
import io.github.omisie11.spacexfollower.data.model.Core

class CoresViewModel(private val repository: SpaceRepository) : ViewModel() {

    private val mAllCores: LiveData<List<Core>> by lazy { repository.getCores() }
    private val mAreCoresLoading: LiveData<Boolean> by lazy { repository.getCoresLoadingStatus() }
    private val _snackBar: MutableLiveData<String> = repository.getCoresSnackbar()

    fun getCores(): LiveData<List<Core>> = mAllCores

    fun getCoresLoadingStatus(): LiveData<Boolean> = mAreCoresLoading

    // Wrapper for refreshing cores data
    fun refreshCores() = repository.refreshCores()

    // Wrapper for refreshing old data in onResume
    fun refreshIfCoresDataOld() = repository.refreshIfCoresDataOld()

    /**
     * Request a snackbar to display a string.
     */
    val snackbar: LiveData<String>
        get() = _snackBar

    /**
     * Called immediately after the UI shows the snackbar.
     */
    fun onSnackbarShown() {
        _snackBar.value = null
    }
}