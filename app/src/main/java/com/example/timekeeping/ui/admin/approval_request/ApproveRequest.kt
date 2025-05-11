package com.example.timekeeping.ui.admin.approval_request

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Request
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.view_models.RequestViewModel

@Composable
fun ApproveRequest(
    onBackClick: () -> Unit,
    requestViewModel: RequestViewModel = hiltViewModel(),
    onAccept: (Request) -> Unit,
    groupId: String
) {
    var requests by remember { mutableStateOf(listOf<Request>()) }

    LaunchedEffect(Unit) {
        requestViewModel.getRequestByGroupId(groupId) {
            requests = it
        }
    }

    Scaffold (
        topBar = {
            TopBarClassic(
                title = "Duyệt đơn yêu cầu",
                onBackClick = onBackClick
            )
        }
    ) {
        paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val pages = listOf(
                RequestPage.Pending(
                    requests = requests.filter { it.status == "Chờ duyệt" },
                    onAccept = onAccept,
                ),
                RequestPage.Approved(
                    requests = requests.filter { it.status == "Đã duyệt" }
                ),
                RequestPage.Rejected(
                    requests = requests.filter { it.status == "Từ chối" }
                )
            )

            RequestPagerContent(
                pages = pages,
                currentPage = 0,
                onTabSelected = {}
            )
        }
    }
}