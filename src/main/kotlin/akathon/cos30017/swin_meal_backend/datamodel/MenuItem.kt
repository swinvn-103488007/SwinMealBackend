package akathon.cos30017.swin_meal_backend.datamodel

data class MenuItem(
    var itemID: Int,
    var imageUrl: String?,
    var itemName: String?,
    var itemPrice: Float?,
    var itemShortDesc: String?,
    var itemTag: String?,
    var itemStars: Float?,
    var calories: Float,
    var protein: Float,
    var carbohydrate: Float,
    var fat: Float,
    var quantity: Int = 0
)
