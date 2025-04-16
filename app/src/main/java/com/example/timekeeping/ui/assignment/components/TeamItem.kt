package com.example.timekeeping.ui.assignment.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Team

@Composable
fun TeamItem(
    team: Team,
    onTeamClick: (String) -> Unit
){
    Card(
        modifier = Modifier.padding(16.dp),
        onClick = { onTeamClick(team.id) }
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ){
            Text(text = team.name)
        }
    }
}