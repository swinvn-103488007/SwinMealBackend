package akathon.cos30017.swin_meal_backend.controllers

import akathon.cos30017.swin_meal_backend.datamodel.MenuItem
import akathon.cos30017.swin_meal_backend.services.MenuService
import com.datastax.oss.driver.api.core.CqlSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream

@RestController
@RequestMapping("/menu")
class MenuController(private val menuService: MenuService) {

    @GetMapping("/")
    fun getAllMenu(): List<MenuItem> {
        return menuService.getAllMenu()
    }
}