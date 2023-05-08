package akathon.cos30017.swin_meal_backend.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(EmailAlreadyUsedException::class)
    fun handleEmailAlreadyUsedException(ex: EmailAlreadyUsedException): ResponseEntity<Any> {
        val errorMsg = mapOf("message" to ex.message)
        return ResponseEntity.badRequest().body(errorMsg)
    }

    @ExceptionHandler(WrongLoginCredentialsException::class)
    fun handleWrongLoginCredentialsException(ex: WrongLoginCredentialsException): ResponseEntity<Any> {
        val errorMsg = mapOf("message" to ex.message)
        return ResponseEntity.badRequest().body(errorMsg)
    }
}