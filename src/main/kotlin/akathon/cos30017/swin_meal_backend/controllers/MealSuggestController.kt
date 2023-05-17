package akathon.cos30017.swin_meal_backend.controllers

import akathon.cos30017.swin_meal_backend.datamodel.SuggestMeal
import akathon.cos30017.swin_meal_backend.services.MealSuggestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class MealSuggestController(private val mealSuggestService: MealSuggestService) {
    @GetMapping("/suggest-meals")
    fun handleMealSuggestRequest(@RequestParam("lower_calo") lowerCalo: Float,
                                 @RequestParam("upper_calo") upperCalo: Float): List<SuggestMeal> {
        return mealSuggestService.suggestMealForCustomer(lowerCalo, upperCalo)
    }
}