package com.example.timekeeping.ui.account

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.utils.ObjectUtils
import com.example.timekeeping.models.Employee
import com.example.timekeeping.models.Name
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.example.timekeeping.ui.admin.employees.form.ImagePicker
import com.example.timekeeping.utils.CloudinaryConfig
import com.example.timekeeping.view_models.EmployeeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MyAccountInputScreen(
    employeeId: String,
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    onUpdate: (Employee) -> Unit,
    onBackClick: () -> Unit = {},
) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()

    var userId by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect (Unit) {
        employeeViewModel.getEmployeeById(employeeId, onSuccess = {
            firstName = it.name.firstName
            lastName = it.name.lastName
            phoneNumber = it.phone
            email = it.email
            userId = it.userId
            selectedImageUri = Uri.parse(it.avatarUrl)
            Log.d("MyAccountInputScreen", "Employee: $it")
        }, onFailure = {
            Log.e("MyAccountInputScreen", "Error loading employee", it)
        })
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Thông tin tài khoản",
                onBackClick = onBackClick,
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            ImagePicker {
                selectedImageUri = it
            }
            Row {
                selectedImageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Họ
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Họ",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Tên
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Tên",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                Text(
                    text = "Số điện thoại",
                    style = MaterialTheme.typography.labelLarge
                )

                TextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Email",
                    style = MaterialTheme.typography.labelLarge
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Lưu ý: Thay đổi Email không ảnh hưởng đến Email đăng nhập",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val inputStream = selectedImageUri?.let {
                                context.contentResolver.openInputStream(it)
                            }

                            val imageUrl = withContext(Dispatchers.IO) {
                                val uploadResult = CloudinaryConfig.getCloudinaryClient()
                                    .uploader()
                                    .upload(inputStream, ObjectUtils.emptyMap())
                                uploadResult["secure_url"] as String
                            }

                            onUpdate(
                                Employee(
                                    id = employeeId,
                                    name = Name(
                                        firstName = firstName,
                                        lastName = lastName
                                    ),
                                    phone = phoneNumber,
                                    email = email,
                                    userId = userId,
                                    avatarUrl = imageUrl
                                ),
                            )

                            inputStream?.close()
                        } catch (e: Exception) {
                            Log.e("UploadError", "Upload failed: ${e.message}")
                            Toast.makeText(context, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show()
                        }

                    }
//                    onBackClick(),
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Lưu")
            }
        }
    }
}