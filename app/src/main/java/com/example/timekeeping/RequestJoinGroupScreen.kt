package com.example.timekeeping

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.utils.QRCodeScannerScreen
import com.example.timekeeping.view_models.EmployeeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RequestJoinGroupScreen(
    onBackClick: () -> Unit,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        val currentEmployeeId = auth.currentUser?.uid ?: ""
        val employeeViewModel: EmployeeViewModel = hiltViewModel()
        QRCodeScannerScreen {
            result -> employeeViewModel.requestJoinGroup(currentEmployeeId)
        }
    }
}