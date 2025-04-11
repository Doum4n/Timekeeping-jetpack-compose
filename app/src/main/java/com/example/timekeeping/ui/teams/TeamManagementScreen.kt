package com.example.timekeeping.ui.teams

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Team
import com.example.timekeeping.ui.components.TopBarWithAddAction
import com.example.timekeeping.view_models.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
    groupId: String,
    viewModel: TeamViewModel = hiltViewModel(),
    onAddTeamClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    TeamManagementScreen(
        groupId = groupId,
        teams = viewModel.teams.value,
        onAddTeamClick = onAddTeamClick,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
    groupId: String,
    teams: List<Team>, // giả sử bạn có model Team(name: String)
    onAddTeamClick: (String) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBarWithAddAction(
                title = "Tổ làm việc",
                onBackClick = onBackClick,
                onAddShiftClick = { onAddTeamClick(groupId) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(teams) { team ->
                TeamItem(team.name)
            }
        }
    }
}


@Composable
fun TeamItem(
    name: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Thêm Column với padding
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeamManagementScreenPreview() {
    val sampleTeams = listOf(
        Team("1", "Tổ A"),
        Team("2", "Tổ B"),
        Team("3", "Tổ C")
    )
    TeamManagementScreen(
        groupId = "1",
        teams = sampleTeams,
        onAddTeamClick = {},
        onBackClick = {}
    )
}

