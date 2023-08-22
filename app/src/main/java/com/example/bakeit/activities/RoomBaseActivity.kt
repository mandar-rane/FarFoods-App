package com.example.bakeit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bakeit.R
import kotlinx.coroutines.flow.Flow

private lateinit var cartDao: CartDao
open class RoomBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_base)

        cartDao = (application as CartApp).db.cartDao()
    }

    fun getCartTotalPrice(): Double {
        return cartDao.getTotalPrice()
    }

    suspend fun getCartQty(): Int{
        return cartDao.getCount()

    }

    suspend fun update(cartItem: CartEntity){
        cartDao.update(cartItem)
    }

    suspend fun getCartItem(id: String): CartEntity {
        return cartDao.getCartItem(id)
    }

    suspend fun addItemToCart(item: CartEntity){
        cartDao.insert(item)
    }

    suspend fun getAllCartItems(): Flow<List<CartEntity>>{
        return cartDao.getCart()
    }

    suspend fun deleteCartItem(item: CartEntity){
        cartDao.delete(item)
    }

    suspend fun clearCart(){
        cartDao.nukeTable()
    }

    suspend fun getCartItemNames(): List<String>{
        return cartDao.fetchCart()
    }

    suspend fun getStackedCartItems(): Flow<List<CartEntity>>{
        return cartDao.getStackCart()
    }

    fun getCartItemsForShop(): List<CartEntity>{
        return cartDao.getCartItemsForShop()
    }



}