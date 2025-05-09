package com.example.timekeeping.ui.approval_request

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Request

@Composable
fun RequestPagerContent(
    pages: List<RequestPage>,
    currentPage: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Chờ duyệt", "Đã duyệt", "Đã từ chối")
    RequestPagerContentStateless(
        tabs = tabs,
        currentPage = currentPage,
        onTabSelected = onTabSelected,
        requestsByPage = pages.map { page ->
            {
                when (page) {
                    is RequestPage.Pending -> PendingRequestsScreen(page, onAccept = page.onAccept)
                    is RequestPage.Approved -> ApprovedRequestsScreen(page)
                    is RequestPage.Rejected -> RejectedRequestsScreen(page)
                }
            }
        }
    )
}

@Composable
fun PendingRequestsScreen(requestPage: RequestPage.Pending, onAccept: (Request) -> Unit){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    )  {
        items(requestPage.requests) { request ->
            RequestItem(
                request = request,
                onAccept = onAccept,
//                rejectReason = requestPage.rejectReason,
//                onRejectReason = { requestPage.rejectReason = it }
            )
        }
    }
}

@Composable
fun ApprovedRequestsScreen(requestPage: RequestPage.Approved){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        items(requestPage.requests) { request ->
            RequestItem(
                request = request,
                onAccept = {},
            )
        }
    }
}

@Composable
fun RejectedRequestsScreen(requestPage: RequestPage.Rejected) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    )  {
        items(requestPage.requests) { request ->
            RequestItem(
                request = request,
                onAccept = {},
            )
        }
    }
}

@Preview
@Composable
fun RequestPagerContentPreview() {
    RequestPagerContent(
        pages = listOf(
            RequestPage.Pending(
                requests = listOf(
                    Request(
                        id = "1",
                        employeeId = "1",
                        groupId = "1",
                        reason = "Sick leave",
                        status = "chờ duyệt"
                    ),
                    Request(
                        id = "2",
                        employeeId = "2",
                        groupId = "1",
                        reason = "Vacation",
                        status = "chờ duyệt"
                    )
                ),
                onAccept = {},
            ),
            RequestPage.Approved(
                requests = listOf(
                    Request(
                        id = "3",
                        employeeId = "3",
                        groupId = "1",
                        reason = "Sick leave",
                        status = "Đã duyệt"
                    ),
                    Request(
                        id = "4",
                        employeeId = "4",
                        groupId = "1",
                        reason = "Vacation",
                        status = "Đã duyệt"
                    )
                )
            ),
            RequestPage.Rejected(
                requests = listOf(
                    Request(
                        id = "5",
                        employeeId = "5",
                        groupId = "1",
                        reason = "Sick leave",
                        status = "Từ chối"
                    ),
                    Request(
                        id = "6",
                        employeeId = "6",
                        groupId = "1",
                        reason = "Vacation",
                        status = "Từ chối"
                    )
                )
            )
        ),
        currentPage = 1,
        onTabSelected = {},
    )
}