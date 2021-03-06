package io.github.omisie11.spacexfollower.ui.capsules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.repository.CapsulesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CapsulesViewModel(private val repository: CapsulesRepository) : ViewModel() {

    private val allCapsules: MutableLiveData<List<Capsule>> = MutableLiveData()
    private val _areCapsulesLoading: LiveData<Boolean> = repository.getCapsulesLoadingStatus()
    private val _snackBar: MutableLiveData<String> = repository.getCapsulesSnackbar()

    private val _sortingOrder: MutableLiveData<CapsulesSortingOrder> =
        MutableLiveData(CapsulesSortingOrder.BY_SERIAL_NEWEST)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllCapsulesFlow()
                .collect { capsules -> sortAndSetCapsules(capsules) }
        }
    }

    fun getCapsules(): LiveData<List<Capsule>> = allCapsules

    fun getCapsulesSortingOrder(): LiveData<CapsulesSortingOrder> = _sortingOrder

    fun setCapsulesSortingOrder(sortingOrder: CapsulesSortingOrder) {
        _sortingOrder.value = sortingOrder
        viewModelScope.launch(Dispatchers.IO) {
            if (allCapsules.value != null) sortAndSetCapsules(allCapsules.value!!)
        }
    }

    fun getCapsulesLoadingStatus(): LiveData<Boolean> = _areCapsulesLoading

    // Wrapper for refreshing capsules data
    fun refreshCapsules() = viewModelScope.launch { repository.refreshData(forceRefresh = true) }

    // Wrapper for refreshing old data in onResume
    fun refreshIfCapsulesDataOld() =
        viewModelScope.launch { repository.refreshData(forceRefresh = false) }

    fun deleteCapsulesData() = viewModelScope.launch { repository.deleteAllCapsules() }

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

    private fun sortAndSetCapsules(capsules: List<Capsule>) {
        allCapsules.postValue(when (_sortingOrder.value) {
            CapsulesSortingOrder.BY_SERIAL_NEWEST -> {
                capsules.sortedByDescending { it._id }
            }
            CapsulesSortingOrder.BY_SERIAL_OLDEST -> {
                capsules.sortedBy { it._id }
            }
            CapsulesSortingOrder.BY_STATUS_ACTIVE_FIRST -> {
                capsules.sortedBy { it.status }
            }
            CapsulesSortingOrder.BY_STATUS_ACTIVE_LAST -> {
                capsules.sortedByDescending { it.status }
            }
            else -> capsules.sortedByDescending { it._id }
        })
    }

    // Class representing all possible sort orders of capsules
    enum class CapsulesSortingOrder {
        BY_SERIAL_NEWEST, BY_SERIAL_OLDEST,
        BY_STATUS_ACTIVE_FIRST, BY_STATUS_ACTIVE_LAST
    }
}
