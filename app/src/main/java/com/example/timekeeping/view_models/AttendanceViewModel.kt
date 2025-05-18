package com.example.timekeeping.view_models

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.repositories.AttendanceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.checkerframework.checker.units.qual.A
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor (
    private val attendanceRepository: AttendanceRepo,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val groupId = savedStateHandle.get<String>("groupId") ?: ""

    fun CheckIn(attendance: Attendance, groupId: String, isUpdate: Boolean = false){
        attendanceRepository.checkIn(attendance, groupId, isUpdate)
    }

    fun getAttendanceByShiftId(shiftId: String, dayCheckIn: Date, onResult: (List<Attendance>) -> Unit){
        attendanceRepository.getAttendanceByShiftId(shiftId, dayCheckIn, onResult)
    }

    fun getAttendanceByEmployeeId(employeeId: String, month: Int, year: Int, onResult: (List<Attendance>) -> Unit) {
        attendanceRepository.getAttendanceByEmployeeId(groupId, employeeId, month, year, onResult)
    }

    fun getAttendanceByEmployeeIdAndDate(employeeId: String, date: LocalDate, onResult: (List<Attendance>) -> Unit) {
        attendanceRepository.getAttendanceByEmployeeIdAndDate(groupId, employeeId, date, onResult)
    }

    fun CheckOut(attendance: Attendance) {
        attendanceRepository.CheckOut(attendance)
    }
}