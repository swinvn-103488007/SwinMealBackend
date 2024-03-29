package akathon.cos30017.swin_meal_backend.controllers

import akathon.cos30017.swin_meal_backend.datamodel.RequestWrapper
import akathon.cos30017.swin_meal_backend.datamodel.CurrentOrderItem
import akathon.cos30017.swin_meal_backend.datamodel.OrderHistoryItem
import akathon.cos30017.swin_meal_backend.services.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/order")
class OrderController(private val orderService: OrderService) {
    @PutMapping("/new-order")
    fun handleNewOrder(@RequestBody requestWrapper: RequestWrapper<OrderHistoryItem, CurrentOrderItem>,
                       @RequestParam email: String, @RequestParam name: String): ResponseEntity<Any> {
        val orderHistoryItem = requestWrapper.object1
        val currentOrderItem = requestWrapper.object2
//        println(orderHistoryItem.toString())
//        println(currentOrderItem.toString())
        return orderService.handleNewOrder(email, name, orderHistoryItem, currentOrderItem)
    }

    @GetMapping("/order-history")
    fun getOrderHistoryList(@RequestParam email: String): List<OrderHistoryItem> {
        return orderService.getAllOrderHistory(email)
    }

    @PutMapping("/order-received")
    fun handleOrderMarkAsReceived(@RequestParam email: String, @RequestParam orderId: String): ResponseEntity<Any> {
        return orderService.handleOrderMarkAsReceived(email, orderId)
    }

    @GetMapping("/current-order")
    fun getCurrentOrderList(@RequestParam email: String): List<CurrentOrderItem> {
        return orderService.getAllCurrentOrder(email)
    }

    @DeleteMapping("/order-cancel")
    fun handleOrderCancel(@RequestParam email: String, @RequestParam orderId: String) {
        return orderService.handleOrderCancel(email, orderId)
    }

    @DeleteMapping("/history-delete")
    fun deleteHistoryData(@RequestParam email: String, @RequestParam orderId: String) {
        return orderService.deleteHistoryData(email, orderId)
    }
}