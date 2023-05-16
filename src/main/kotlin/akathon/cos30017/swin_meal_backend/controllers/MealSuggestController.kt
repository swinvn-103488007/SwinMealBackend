package akathon.cos30017.swin_meal_backend.controllers

import akathon.cos30017.swin_meal_backend.datamodel.SuggestMeal
import akathon.cos30017.swin_meal_backend.services.MealSuggestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MealSuggestController(private val mealSuggestService: MealSuggestService) {
    @GetMapping("/suggest-meals")
    fun handleMealSuggestRequest(): List<SuggestMeal> {
        return mealSuggestService.suggestMealForCustomer(2000.0f)
    }
}