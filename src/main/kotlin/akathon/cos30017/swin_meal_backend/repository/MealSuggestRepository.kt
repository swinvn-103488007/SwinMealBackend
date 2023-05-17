package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.MenuItem
import akathon.cos30017.swin_meal_backend.datamodel.SuggestMeal
import com.datastax.oss.driver.api.core.CqlSession
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Repository
import java.nio.file.Paths

@Repository
class MealSuggestRepository (@Value("\${astra.secureBundlePath}") private var secureBundlePath: String,
                             @Value("\${astra.keyspace}") private var keyspace: String,
                             @Value("\${astra.clientId}") private var clientId: String,
                             @Value("\${astra.clientSecret}") private var clientSecret: String) {

    private val connectionBundle = ClassPathResource(secureBundlePath).inputStream
    private var session = CqlSession.builder()
        .withCloudSecureConnectBundle(connectionBundle)
        .withAuthCredentials(clientId, clientSecret)
        .build()

    private val suggestMealList = mutableListOf<SuggestMeal>()
    fun generateSuggestedMeal(lowerLimitPerMeal: Float, upperLimitPerMeal: Float): List<SuggestMeal> {

        runBlocking {
            findMeals(lowerLimitPerMeal, upperLimitPerMeal)
        }
        println(suggestMealList.size)
        for (meal in suggestMealList) {
            println("${meal.food1.itemTag} + ${meal.food2.itemTag}")
        }
        return suggestMealList
    }

    suspend fun findMeals(lowerLimitPerMeal: Float, upperLimitPerMeal: Float,) {
        suggestMealList.clear()
        val food1Category = listOf<String>("bbqs","burgers","steaks","breads","fried-chicken","sandwiches","pizzas")
        coroutineScope {
            launch {
                val food1UpperCalories = (upperLimitPerMeal + lowerLimitPerMeal) / 3
                val food1LowerCalories = (upperLimitPerMeal + lowerLimitPerMeal) / 4
//                println("Upper calories per meal: $upperLimitPerMeal, Lower calories per meal: $lowerLimitPerMeal")
                val subCategory = food1Category.shuffled().subList(0, 3)
                val query = "SELECT * FROM ${keyspace}.menu_full WHERE category IN " +
                        "('${subCategory[0]}', '${subCategory[1]}', '${subCategory[2]}') " +
                        "and calories > $food1LowerCalories" +
                        " and calories < $food1UpperCalories ALLOW FILTERING"
                val rs = session.execute(query)
                val matchedFirstFoods = rs.map{ row ->
                    MenuItem(
                        row.getInt("id"),
                        row.getString("img"),
                        row.getString("name"),
                        row.getFloat("price"),
                        row.getString("dsc"),
                        row.getString("category"),
                        row.getFloat("rate"),
                        row.getFloat("calories"),
                        row.getFloat("protein"),
                        row.getFloat("carbohydrate"),
                        row.getFloat("fat")
                    )
                }.toList().shuffled()
//                println("Total: ${matchedSteaks.size}")
//                println(matchedSteaks.toString())
                val subSetMatchedFirstFood =  if(matchedFirstFoods.size > 5) matchedFirstFoods.subList(0,5) else matchedFirstFoods
                for (item in subSetMatchedFirstFood) {
                    val upperCaloriesLeft = upperLimitPerMeal - item.calories
                    val lowerCaloriesLeft = lowerLimitPerMeal - item.calories
//                    println("Steaks match: ${item.itemName}, Calories: ${item.calories}")
//                    println("Calories range left: ($lowerCaloriesLeft, $upperCaloriesLeft)\n")
                    launch {
                       val food2Category = listOf<String>("drinks","ice-cream","desserts","chocolates")
                       val getSecondFoodQuery = "SELECT * FROM ${keyspace}.menu_full WHERE category='${food2Category.random()}'" +
                               " AND calories > $lowerCaloriesLeft" +
                                " AND calories < $upperCaloriesLeft limit 5 ALLOW FILTERING"
                       val secondFoodRows = session.execute(getSecondFoodQuery)
                       val matchedSecondFood = secondFoodRows.map{ row ->
                            MenuItem(
                                row.getInt("id"),
                                row.getString("img"),
                                row.getString("name"),
                                row.getFloat("price"),
                                row.getString("dsc"),
                                row.getString("category"),
                                row.getFloat("rate"),
                                row.getFloat("calories"),
                                row.getFloat("protein"),
                                row.getFloat("carbohydrate"),
                                row.getFloat("fat")
                            )
                       }.toList()
//                       println(matchedSecondFood.size)
                       for (food2 in matchedSecondFood) {
                           suggestMealList.add(SuggestMeal(item, food2))
                       }
                    }
                }
            }
        }
    }
}