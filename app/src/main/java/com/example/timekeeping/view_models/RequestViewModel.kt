package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Request
import com.example.timekeeping.repositories.RequestRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestViewModel @Inject constructor(
    val requestRepository: RequestRepo
) : ViewModel() {

    fun getAllRequests() = requestRepository.getAllRequest{}

    fun getRequestById(id: String, groupId: String, onResult: (List<Request>) -> Unit) = requestRepository.getRequestByEmployeeId(id, groupId, onResult)

    fun getRequestByGroupId(groupId: String, onResult: (List<Request>) -> Unit) = requestRepository.getRequestByGroupId(groupId, onResult)

    fun createRequest(request: Request, onResult: () -> Unit) = requestRepository.createRequest(request, onResult)

    fun updateRequest(requestId: String, status: String) = requestRepository.updateStatusRequest(requestId, status){}

    fun deleteRequest(requestId: String, onResult: () -> Unit, onError: (Exception) -> Unit) = requestRepository.deleteRequest(requestId, onResult, onError)
}