package akathon.cos30017.swin_meal_backend.services

import akathon.cos30017.swin_meal_backend.datamodel.Customer
import akathon.cos30017.swin_meal_backend.exception.EmailAlreadyUsedException
import akathon.cos30017.swin_meal_backend.repository.CustomerRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CustomerService(private val customerRepository: CustomerRepository) {
    fun addCustomer(customer: Customer) : ResponseEntity<Any>{
        if (customerRepository.haveUsedEmail(customer.email)) {
            throw EmailAlreadyUsedException("Email ${customer.email} is already used")
        }
        customerRepository.addNewCustomer(customer)
        return ResponseEntity.ok().build()
    }

    fun validateLoginRequest(email: String, password: String) : ResponseEntity<Any>{
        if (!customerRepository.validateCredentials(email, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong email address or password. Pleas check again")
        }
        return ResponseEntity.ok("Login successful")
    }
}