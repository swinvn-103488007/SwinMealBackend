package akathon.cos30017.swin_meal_backend.repository

import akathon.cos30017.swin_meal_backend.datamodel.MenuItem
import akathon.cos30017.swin_meal_backend.datamodel.SuggestMeal
import com.datastax.oss.driver.api.core.CqlSession
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.nio.file.Paths

@Repository
class MealSuggestRepository {
    @Value("\${astra.keyspace}")
    private lateinit var keyspace: String
    private var session = CqlSession.builder()
        .withCloudSecureConnectBundle(Paths.get("E:/spring2023/connection_bundles/secure-connect-swinmealdb.zip"))
        .withAuthCredentials("AcRCBgaqoLkRzpruuQKQWpdS", "lXLR2SszZgnO4JiF,,ugKfELqmXwP74jR4_-Qq-T,f9P4OKRnoWeB0zOos,T0qg0+85KcW3MUdmZY1Zj3sC4x3dSTZZM8vXz0Ki08J_R4MHfQS9rGHR8twLI7NWfql2Q")
        .build()
    private val suggestMealList = mutableListOf<SuggestMeal>()
    fun generateSuggestedMeal(totalCalories: Float): List<SuggestMeal> {
//        val upperLimit = 1.05 * totalCalories
//        val lowerLimit = 0.95 * totalCalories
        val upperLimit = 2300
        val lowerLimit = 2090

        val upperLimitPerMeal = (2300 / 3)
        val lowerLimitPerMeal = (2090 / 3)
        runBlocking {
            findMeals(upperLimitPerMeal.toFloat(), lowerLimitPerMeal.toFloat())
        }
        println(suggestMealList.size)
        for (meal in suggestMealList) {
            println("${meal.food1.itemTag} + ${meal.food2.itemTag}")
        }
        return suggestMealList
    }

    suspend fun findMeals(upperLimitPerMeal: Float, lowerLimitPerMeal: Float) {
        suggestMealList.clear()
        val food1Category = listOf<String>("bbqs","burgers","steaks","breads","fried-chicken")
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
                       val food2Category = listOf<String>("drinks","ice-cream","desserts")
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