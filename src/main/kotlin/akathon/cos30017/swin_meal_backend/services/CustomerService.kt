package akathon.cos30017.swin_meal_backend.services

import akathon.cos30017.swin_meal_backend.datamodel.Customer
import akathon.cos30017.swin_meal_backend.exception.EmailAlreadyUsedException
import akathon.cos30017.swin_meal_backend.exception.WrongLoginCredentialsException
import akathon.cos30017.swin_meal_backend.repository.CustomerRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CustomerService(private val customerRepository: CustomerRepository) {
    fun addCustomer(customer: Customer) : ResponseEntity<Any>{
        if (customerRepository.haveUsedEmail(customer.email)) {
            val errorMessage = "Email ${customer.email} is already used"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage)
        }
        customerRepository.addNewCustomer(customer)
        return ResponseEntity.ok("Register successful")
    }

    fun validateLoginRequest(email: String, password: String) : ResponseEntity<Any>{
        if (!customerRepository.validateCredentials(email, password)) {
            val errorMessage = "Wrong email address or password. Please check again"
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage)
        }
        return ResponseEntity.ok("Login successful")
    }

    fun getCustomerProfile(email: String) : ResponseEntity<Any>{
        val customer = customerRepository.getCustomerData(email)
        return ResponseEntity.ok(customer)
    }

    fun editHealthProfile(email: String, gender:String, age: Int,
                          height: Float, weight: Float, activityLevel: String) : ResponseEntity<Any>
    {
        customerRepository.updateCustomerHealthData(email, gender, age, height, weight, activityLevel)
        return ResponseEntity.ok("Health Profile updated")
    }
}