package com.example.timekeeping.ui.account

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.timekeeping.ui.admin.components.TopBarClassic
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
) {

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordError by remember { mutableStateOf(false) }
    var passwordErrorMessage by remember { mutableStateOf("") }

    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email

    val context = LocalContext.current

    Scaffold (
        topBar = {
            TopBarClassic(
                title = "Đổi mật khẩu",
                onBackClick = onBackClick
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text("Mật khẩu cũ")
            TextField(
                value = oldPassword,
                onValueChange = {
                    oldPassword = it
                    passwordError = false
                    passwordErrorMessage = ""
                },
                placeholder = { Text("Nhập mật khẩu cũ") },
                isError = passwordError
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    passwordError = false
                    passwordErrorMessage = ""
                },
                label = { Text("Mật khẩu mới") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                isError = passwordError
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordError = false
                    passwordErrorMessage = ""
                },
                label = { Text("Nhập lại mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                isError = passwordError
            )

            if (passwordError) {
                Text(
                    text = passwordErrorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nút xác nhận đổi mật khẩu, ví dụ:
            Button(onClick = {
                // Validate:
                if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                    passwordError = true
                    passwordErrorMessage = "Vui lòng điền đủ các trường"
                    return@Button
                }
                if (newPassword != confirmPassword) {
                    passwordError = true
                    passwordErrorMessage = "Mật khẩu mới và xác nhận không khớp"
                    return@Button
                }
                if (newPassword.length < 6) {
                    passwordError = true
                    passwordErrorMessage = "Mật khẩu phải dài ít nhất 6 ký tự"
                    return@Button
                }
                // Thực hiện gọi đổi mật khẩu Firebase
                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, oldPassword)

                    // 2. Xác thực lại user với mật khẩu cũ
                    user.reauthenticate(credential)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                // 3. Xác thực thành công, cập nhật mật khẩu mới
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                            onBackClick()
                                        } else {
                                            passwordError = true
                                            passwordErrorMessage = "Đổi mật khẩu thất bại"
                                        }
                                    }
                            } else {
                                Toast.makeText(context, "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }) {
                Text("Đổi mật khẩu")
            }
        }
    }
}