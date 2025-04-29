package com.example.timekeeping.ui.teams

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onEditTeamClick: (String) -> Unit,
    onDeleteTeamClick: (String) -> Unit
) {
    TeamManagementScreen(
        groupId = groupId,
        teams = viewModel.teams.value,
        onAddTeamClick = onAddTeamClick,
        onBackClick = onBackClick,
        onEditTeamClick = onEditTeamClick,
        onDeleteTeamClick = onDeleteTeamClick
    )
}

@Composable
fun TeamManagementScreen(
    groupId: String,
    teams: List<Team>, // giả sử bạn có model Team(name: String)
    onAddTeamClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onEditTeamClick: (String) -> Unit,
    onDeleteTeamClick: (String) -> Unit
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
                TeamItem(
                    team = team,
                    onEditClick = onEditTeamClick,
                    onDeleteClick = onDeleteTeamClick
                )
            }
        }
    }
}


@Composable
fun TeamItem(
    team: Team,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = team.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { onEditClick(team.id) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                }
                IconButton(onClick = { onDeleteClick(team.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
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
        onBackClick = {},
        onEditTeamClick = {},
        onDeleteTeamClick = {}
    )
}

