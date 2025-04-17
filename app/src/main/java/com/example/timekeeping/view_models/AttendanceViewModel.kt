package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.repositories.AttendanceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.checkerframework.checker.units.qual.A
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor (
    private val attendanceRepository: AttendanceRepo
) : ViewModel() {
    fun CheckIn(employeeId: String, shiftId: String, attendanceType: String){
        attendanceRepository.CheckIn(employeeId, shiftId, attendanceType)
    }

    fun getAttendanceByShiftId(shiftId: String, onResult: (List<Attendance>) -> Unit){
        attendanceRepository.getAttendanceByShiftId(shiftId, onResult)
    }

    fun updateAttendance(attendanceId: String, attendance: Attendance) {
        attendanceRepository.updateAttendance(attendanceId, attendance)
    }
}