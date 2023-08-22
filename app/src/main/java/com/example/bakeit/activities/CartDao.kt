package com.example.bakeit.activities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert
    suspend fun insert(cartEntity: CartEntity)

    @Update
    suspend fun update(cartEntity: CartEntity)

    @Delete
    suspend fun delete(cartEntity: CartEntity)

    @Query("DELETE FROM `cart-table`")
    fun nukeTable()

    @Query("SELECT * FROM `cart-table` where pid = :id")
    fun getCartItem(id: String): CartEntity

    @Query("SELECT * FROM `cart-table`")
    fun getCart() : Flow<List<CartEntity>>

    @Query("SELECT COUNT(pid) FROM `cart-table`")
    fun getCount() : Int

    @Query("SELECT sum(totalItemPrice) FROM `cart-table`")
    fun getTotalPrice(): Double

    @Query("SELECT itemName FROM `cart-table`")
    fun fetchCart(): List<String>

    @Query("SELECT * FROM `cart-table`")
    fun getCartItemsForShop(): List<CartEntity>

    @Query("SELECT pid, quantity, itemName, itemPrice, totalItemPrice, itemImg, itemShopId FROM `cart-table`")
    fun getStackCart(): Flow<List<CartEntity>>
}