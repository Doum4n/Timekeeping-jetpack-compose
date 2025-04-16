package com.example.timekeeping.ui.assignment.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import java.time.DayOfWeek
import java.time.YearMonth

data class CalendarDay(
    val day: String,
    var isSelected: Boolean = false,
    val isAssigned: Boolean = false
)

sealed class Calendar {
    class Shared(
        val selectedDays: MutableList<String>,
        val selectedWeekdays: MutableList<DayOfWeek>,
        val calendarDay: List<CalendarDay>,
    ) : Calendar() {
        fun selectDay(day: CalendarDay) {
            if (day.isSelected.xor(day.isAssigned)) selectedDays.remove(day.day)
            else selectedDays.add(day.day)
        }

        fun toggleWeekday(weekday: DayOfWeek) {
            if (selectedWeekdays.contains(weekday)) selectedWeekdays.remove(weekday)
            else selectedWeekdays.add(weekday)
        }
    }

    class Individual(
        val employeeId: String = "",
        val weekdayByEmployee: MutableMap<String, SnapshotStateList<DayOfWeek>>,
        val calendarByEmployee: MutableMap<String, SnapshotStateList<CalendarDay>>
    ) : Calendar() {

        fun selectDay(day: CalendarDay) {
            val calendar = calendarByEmployee[employeeId] ?: return

            val index = calendar.indexOfFirst { it.day == day.day }
            if (index != -1) {
                val current = calendar[index]
                calendar[index] = current.copy(
                    isSelected = !current.isSelected,
                    isAssigned = !current.isSelected
                )
            }
        }

        fun toggleWeekday(weekday: DayOfWeek, sharedDays: List<CalendarDay>) {
            weekdayByEmployee[employeeId]?.let { weekdays ->
                if (weekdays.contains(weekday)) weekdays.remove(weekday)
                else weekdays.add(weekday)
            }

            // Sau đó đồng bộ lại lịch cá nhân với sharedDays
            syncEmployeeCalendar(sharedDays)
        }

        fun syncEmployeeCalendar(sharedDays: List<CalendarDay>) {
            // Lấy danh sách các weekday của nhân viên (nếu chưa có thì dùng empty list)
            val selectedWeekdays = weekdayByEmployee[employeeId] ?: mutableStateListOf()
            // Giả sử bạn có một cách xác định thứ trong tuần của 1 ngày,
            // ví dụ, nếu CalendarDay chỉ lưu "day" dưới dạng chuỗi số (ngày của tháng),
            // bạn có thể chuyển nó về số rồi tính ngày trong tuần bằng YearMonth hiện tại.
            // Lưu ý: Bạn cần đảm bảo rằng chuỗi day có thể chuyển sang số (Int).
            calendarByEmployee[employeeId] = sharedDays.map { day ->
                // Tạm sử dụng YearMonth hiện tại để xác định thứ của ngày
                val dayInt = day.day.toIntOrNull() ?: 1
                val currentMonth = YearMonth.now() // hoặc lấy từ trạng thái hiển thị
                val dayOfWeek = currentMonth.atDay(dayInt).dayOfWeek
                // Nếu ngày có thuộc danh sách weekday đã chọn thì đánh dấu là selected
                day.copy(isSelected = dayOfWeek in selectedWeekdays)
            }.toMutableStateList()
        }

    }
}
