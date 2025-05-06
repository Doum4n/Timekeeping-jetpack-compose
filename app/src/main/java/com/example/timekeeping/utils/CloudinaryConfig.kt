package com.example.timekeeping.utils

import com.cloudinary.Cloudinary

class CloudinaryConfig {
    companion object {
        fun getCloudinaryClient(): Cloudinary {
            val config: HashMap<String, String> = hashMapOf(
                "cloud_name" to "dkqiuydlm",
                "api_key" to "291274381687389",
                "api_secret" to "BwIdgM5HaShoTYVQQHljyv6h_Dg"
            )

            return Cloudinary(config)
        }
    }
}
