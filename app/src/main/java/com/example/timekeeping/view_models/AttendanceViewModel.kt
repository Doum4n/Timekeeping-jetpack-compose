package com.example.timekeeping.view_models

import androidx.lifecycle.ViewModel
import com.example.timekeeping.models.Attendance
import com.example.timekeeping.repositories.AttendanceRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import org.checkerframework.checker.units.qual.A
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor (
    private val attendanceRepository: AttendanceRepo
) : ViewModel() {
    fun CheckIn(attendance: Attendance){
        attendanceRepository.CheckIn(attendance)
    }

    fun getAttendanceByShiftId(shiftId: String, dayCheckIn: Date, onResult: (List<Attendance>) -> Unit){
        attendanceRepository.getAttendanceByShiftId(shiftId, dayCheckIn, onResult)
    }

    fun updateAttendance(attendanceId: String, attendance: Attendance) {
        attendanceRepository.updateAttendance(attendanceId, attendance)
    }
}