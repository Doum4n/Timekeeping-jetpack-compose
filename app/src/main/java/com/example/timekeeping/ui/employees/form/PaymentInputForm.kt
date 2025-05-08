package com.example.timekeeping.ui.employees.form

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.utils.ObjectUtils
import com.example.timekeeping.models.Payment
import com.example.timekeeping.ui.calender.CalendarState
import com.example.timekeeping.ui.components.TopBarClassic
import com.example.timekeeping.utils.CloudinaryConfig
import com.example.timekeeping.utils.DateTimeMap
import com.example.timekeeping.view_models.EmployeeViewModel
import com.example.timekeeping.view_models.PaymentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PaymentInputForm(
    state: CalendarState = CalendarState(),
    onPaymentClick: (Payment) -> Unit = {},
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        employeeViewModel.getName(paymentViewModel.employeeId, {
            name = it
        })
    }

    LaunchedEffect(paymentViewModel.paymentId) {
        if(paymentViewModel.paymentId != "") {
            paymentViewModel.getPaymentById(
                paymentViewModel.groupId,
                paymentViewModel.employeeId,
                paymentViewModel.paymentId,
                state.visibleMonth.monthValue,
                state.visibleMonth.year,
                {
                    amount = it.amount.toString()
                    note = it.note
                    selectedImageUri = Uri.parse(it.imageUrl)
                })
        }
    }

    Scaffold(
        topBar = {
            TopBarClassic(
                title = "Thanh toán",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Tên nhân viên",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                TextField(
                    value = name,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }

            item {
                Text(
                    text = "Số tiền",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "Ngày thanh toán",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Header(state)
            }

            item {
                Text(
                    text = "Hình ảnh",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )

                ImagePicker(onImageSelected = {
                    selectedImageUri = it
                })

                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

            item {
                Text(
                    text = "Ghi chú",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
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

                                onPaymentClick(
                                    Payment(
                                        amount = amount.toInt(),
                                        createdAt = DateTimeMap.from(LocalDateTime.now()),
                                        imageUrl = imageUrl,
                                        note = note,
                                        groupId = paymentViewModel.groupId,
                                        employeeId = paymentViewModel.employeeId
                                    )
                                )

                                inputStream?.close()
                            } catch (e: Exception) {
                                Log.e("UploadError", "Upload failed: ${e.message}")
                            }
                        }
                    },
                    modifier = Modifier
//                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                ) {
                    Text("Thanh toán")
                }
            }
        }
    }
}

@Composable
fun Header(
    state: CalendarState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { state.prevMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }

        Text(
            text = state.visibleDate.format(DateTimeFormatter.ofPattern("dd")),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = { state.nextMonth() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
        }
    }
}

@Composable
fun ImagePicker(
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it)
        }
    }

    Button(onClick = {
        launcher.launch("image/*")  // Chỉ chọn ảnh
    }) {
        Text("Chọn ảnh")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPayMentInputForm() {
    val calendarState = remember { CalendarState() }

    PaymentInputForm(
        state = calendarState,
        onBack = {}
    )
}
