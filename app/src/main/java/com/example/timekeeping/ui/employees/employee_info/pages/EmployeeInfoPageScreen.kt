package com.example.timekeeping.ui.employees.employee_info.pages

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
import kotlinx.coroutines.launch

@Composable
fun EmployeeInfoPageScreen(
    pages: List<EmployeeInfoPage>,
    currentPage: Int,
    onTabSelected: (Int) -> Unit = {}
) {
    val tabs = listOf("Thông tin cá nhân", "Thông tin lương")
    EmployeeInfoPagerContent(
        tabs = tabs,
        currentPage = currentPage,
        onTabSelected = onTabSelected,
        employeeInfoByPage = pages.map { page ->
            when (page) {
                is EmployeeInfoPage.PersonalInfo -> { { PersonalInfoScreen(page.employee) } }
                is EmployeeInfoPage.SalaryInfo -> { { SalaryInfoScreen(page.salary) } }
            }
        }
    )
}

@Composable
fun EmployeeInfoPagerContent(
    tabs: List<String>,
    currentPage: Int,
    onTabSelected: (Int) -> Unit,
    employeeInfoByPage: List<@Composable () -> Unit>
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
            employeeInfoByPage[page]()
        }
    }
}