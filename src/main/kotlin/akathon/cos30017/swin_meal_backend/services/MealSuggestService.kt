package akathon.cos30017.swin_meal_backend.services

import akathon.cos30017.swin_meal_backend.datamodel.SuggestMeal
import akathon.cos30017.swin_meal_backend.repository.MealSuggestRepository
import org.springframework.stereotype.Service

@Service
class MealSuggestService(private val mealSuggestRepository: MealSuggestRepository) {
    fun suggestMealForCustomer(lower: Float, upper: Float): List<SuggestMeal> {
        return mealSuggestRepository.generateSuggestedMeal(lower, upper)
    }
}