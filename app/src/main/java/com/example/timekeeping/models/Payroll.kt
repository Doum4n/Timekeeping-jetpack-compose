package com.example.timekeeping.models

data class Payroll(
    val id: String = "",
    val groupId: String = "",
    val employeeId: String = "",
    val month: Int = 0,
    val year: Int = 0,
    var totalWage: Int = 0,
    var totalPayment: Int = 0,
){
//    companion object {
//        fun payrollDocId(groupId: String, employeeId: String, month: Int, year: Int): String {
//            return "$groupId-$employeeId-$month-$year"
//        }
//    }
}