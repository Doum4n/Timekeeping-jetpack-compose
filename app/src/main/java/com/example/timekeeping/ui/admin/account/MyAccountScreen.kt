package com.example.timekeeping.ui.admin.account

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.timekeeping.models.Employee
import com.example.timekeeping.ui.admin.employees.components.SimpleDialogS
import com.example.timekeeping.utils.SessionManager
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.GroupViewModel

enum class SettingItem(val icon: ImageVector, val title: String) {
    CODE(Icons.Default.Info, "Mã nhân viên"),
    CHANGE_PASSWORD(Icons.Default.Lock, "Đổi mật khẩu"),
    DELETE(Icons.Default.Delete, "Xóa tài khoản"),
    LOGOUT(Icons.AutoMirrored.Filled.ExitToApp, "Đăng xuất")
}

fun convertSettingItemToEnum(title: String): SettingItem? {
    return SettingItem.entries.find { it.title == title }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAccountScreen(
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    groupViewModel: GroupViewModel = hiltViewModel(),
    onShowCode: (String) -> Unit = {},
    onLogout: () -> Unit,
    onEdit: () -> Unit
) {

    var employee by remember { mutableStateOf(Employee()) }

    var settingItem by remember { mutableStateOf(SettingItem.CODE) }
    var showDeleteDialog = remember { mutableStateOf(false) }
    var showLogoutDialog = remember { mutableStateOf(false) }

    val message = remember { mutableStateOf("") }

    val employeeId = SessionManager.getEmployeeId()

    LaunchedEffect(employeeId) {
        if (employeeId != null) {
            employeeViewModel.getEmployeeById(employeeId, onSuccess = {
                employee = it
            }, onFailure = {
                Log.e("MyAccountScreen", "Error loading employee", it)
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin tài khoản") },
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
        ){
            AccountInfoSection(employee.name.fullName, employee.email, onClick = onEdit)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Column {
                SettingItem.entries.forEach({ item ->
                    SettingItem(item.icon, item.title, {
                        when(item){
                            SettingItem.DELETE -> {
                                message.value = "Bạn có chắc chắn muốn xóa tài khoản không?"
                                showDeleteDialog.value = showDeleteDialog.value.not()
                            }
                            SettingItem.LOGOUT -> {
                                message.value = "Bạn có chắc chắn muốn đăng xuất không?"
                                showLogoutDialog.value = showLogoutDialog.value.not()
                            }
                            SettingItem.CODE -> {
                                onShowCode(employee.id)
                            }
                            else -> {}
                        }
                    })
                })
            }

            if (showDeleteDialog.value){
                SimpleDialogS(
                    title = "Thông báo",
                    question = message.value,
                    onConfirm = {
                        if (employeeId != null) {
                            groupViewModel.joinedGroups.value.forEach({
                                employeeViewModel.deleteEmployee(it.id, employeeId)
                            })
                            onLogout()
                        }
                        showDeleteDialog.value = false
                    },
                    onDismiss = {
                        showDeleteDialog.value = false
                    }
                )
            }

            if (showLogoutDialog.value) {
                SimpleDialogS(
                    title = "Thông báo",
                    question = message.value,
                    onConfirm = {
                        onLogout()
                        showLogoutDialog.value = false

                    },
                    onDismiss = {
                        showLogoutDialog.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun AccountInfoSection(
    name: String,
    email: String,
    onClick: () -> Unit
){
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                imageVector = Icons.Default.Info,
                contentDescription = "Thông tin tài khoản"
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(name)
                Text(email)
            }

            IconButton(
                onClick = onClick
            ) {
                Image(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: (SettingItem) -> Unit
){
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth().clickable {
            onClick(convertSettingItemToEnum(title)!!)
        },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                imageVector = icon,
                contentDescription = title
            )
            if(title == "Đăng xuất"){
                Text(
                    title,
                    color = Color.Red
                )
            }else if(title == "Xóa tài khoản"){
                Text(
                    title,
                    color = Color.Red
                )
            }else
                Text(title)
        }
    }
}