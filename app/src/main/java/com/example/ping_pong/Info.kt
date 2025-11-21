package com.example.ping_pong

import androidx.room.*

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nome") var nome: String?,
    @ColumnInfo(name = "recorde1") var recorde1: Int?,
    @ColumnInfo(name = "recorde2") var recorde2: Int?,
    @ColumnInfo(name = "recorde4") var recorde4: Int?
)