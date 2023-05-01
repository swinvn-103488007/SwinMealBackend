package akathon.cos30017.swin_meal_backend.controllers

import akathon.cos30017.swin_meal_backend.datamodel.Customer
import akathon.cos30017.swin_meal_backend.services.CustomerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/customer")
class CustomerController(private val customerService: CustomerService) {
    @PutMapping("/sign-up")
    fun addCustomer(@RequestBody customer: Customer) : ResponseEntity<Any>{
        return customerService.addCustomer(customer)
    }

    @GetMapping("/login")
    fun customerLogin(@RequestParam email: String, @RequestParam password: String) : ResponseEntity<Any>{

        // Debug message
        println("Received customer email: $email")
        println("Received customer password: $password")

        return customerService.validateLoginRequest(email, password)
    }
}