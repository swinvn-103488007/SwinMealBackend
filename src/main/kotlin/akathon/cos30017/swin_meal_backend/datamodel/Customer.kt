package akathon.cos30017.swin_meal_backend.datamodel

import java.util.UUID

data class Customer(
    var email: String,
    var password: String,
    var name: String?,
    var gender: String?,
    var age: Int?,
    var height: Float?,
    var weight: Float?,
    var activityLevel: String?
)