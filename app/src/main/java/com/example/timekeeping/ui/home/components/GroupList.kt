package com.example.timekeeping.ui.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timekeeping.models.Group

@Composable
fun GroupList(groups: List<Group>, onItemClick: (Group) -> Unit, onCheckInClick: (Group) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        items(groups) { group ->
            GroupItem(
                group,
                onClick = { onItemClick(group) },
                onCheckInClick = { onCheckInClick(group) }
            )
        }
    }
}
