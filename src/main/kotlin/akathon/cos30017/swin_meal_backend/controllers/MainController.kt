package akathon.cos30017.swin_meal_backend.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class MainController {

    @GetMapping
    fun helloWorld() = "Hello world"

    @GetMapping("/tri")
    fun helloTri() = "Hello Tri"
}