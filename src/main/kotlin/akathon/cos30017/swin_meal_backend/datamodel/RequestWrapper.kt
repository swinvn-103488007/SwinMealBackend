package akathon.cos30017.swin_meal_backend.datamodel

data class RequestWrapper<T1, T2>(
    val object1: T1,
    val object2: T2
)
