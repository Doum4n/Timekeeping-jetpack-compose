package com.example.timekeeping.ui.approval_request

import com.example.timekeeping.models.Request

sealed class RequestPage(val requests: List<Request>) {
    class Pending(
        requests: List<Request>,
        val onAccept: (Request) -> Unit,
//        var rejectReason: String,
//        val onRejectReason: (String) -> Unit
    ) : RequestPage(requests)
    class Approved(requests: List<Request>, ) : RequestPage(requests)
    class Rejected(requests: List<Request>, ) : RequestPage(requests)
}