package akathon.cos30017.swin_meal_backend.services

import akathon.cos30017.swin_meal_backend.datamodel.MenuItem
import akathon.cos30017.swin_meal_backend.repository.MenuRepository
import org.springframework.stereotype.Service

@Service
class MenuService(private val menuRepository: MenuRepository) {

    fun getAllMenu(): List<MenuItem> {
        return menuRepository.getAllMenu()
    }
}