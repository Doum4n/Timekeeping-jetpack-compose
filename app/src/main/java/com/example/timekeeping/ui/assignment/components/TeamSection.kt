package com.example.timekeeping.ui.assignment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Team

@Composable
fun TeamSection(
    teams: List<Team>,
    onChooseTeamClick: () -> Unit,
    onTeamClick: (String) -> Unit
) {
    Column {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Chọn tổ", modifier = Modifier.weight(1f))
            Button(onClick = onChooseTeamClick) {
                Text("Quản lý tổ")
            }
        }

        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            teams.forEach { team ->
                TeamItem(team = team, onTeamClick = { onTeamClick(team.id) })
            }
        }
    }
}
