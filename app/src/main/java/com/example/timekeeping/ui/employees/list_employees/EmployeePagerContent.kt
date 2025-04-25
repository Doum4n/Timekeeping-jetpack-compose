package com.example.timekeeping.ui.employees.list_employees

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.timekeeping.ui.employees.ApprovalScreen
import com.example.timekeeping.ui.employees.MembersScreen
import com.example.timekeeping.ui.employees.UnlinkedEmployeesScreen
import kotlinx.coroutines.launch

@Composable
fun EmployeePagerContent(
    pages: List<EmployeePage>,
    groupId: String,
    currentPage: Int,
    onTabSelected: (Int) -> Unit,
    onEmployeeClick: (String) -> Unit
) {
    val tabs = listOf("Chưa liên kết", "Thành viên", "Xét duyệt")

    EmployeePagerContentStateless(
        tabs = tabs,
        currentPage = currentPage,
        onTabSelected = onTabSelected,
        employeesByPage = pages.map
        { page ->
            {
                when (page) {
                    is EmployeePage.Unlinked -> UnlinkedEmployeesScreen(
                        page.employees,
                        groupId,
                        onLinkClick = { employeeId -> page.onLinkClick(employeeId) },
                        onClick = {onEmployeeClick(it)}
                    )
                    is EmployeePage.Members -> MembersScreen(page.employees, groupId){
                        onEmployeeClick(it)
                    }
                    is EmployeePage.Approval -> ApprovalScreen(
                        pendingEmployees = page.employees,
                        groupId = groupId,
                        onAcceptClick = { page.onAcceptClick() },
                    )
                }
            }
        }
    )
}

@Composable
fun EmployeePagerContentStateless(
    tabs: List<String>,
    currentPage: Int,
    onTabSelected: (Int) -> Unit,
    employeesByPage: List<@Composable () -> Unit>
) {
    val pagerState = rememberPagerState(initialPage = currentPage) { tabs.size }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            employeesByPage[page]()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmployeePagerContentPreview() {
    EmployeePagerContentStateless(
        tabs = listOf("Chưa liên kết", "Thành viên", "Xét duyệt"),
        currentPage = 0,
        onTabSelected = {},
        employeesByPage = listOf(
            { Text("Fake Unlinked") },
            { Text("Fake Members") },
            { Text("Fake Approvals") }
        )
    )
}
