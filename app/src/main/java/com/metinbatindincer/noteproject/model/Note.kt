package com.metinbatindincer.noteproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
@Entity
data class Note(
    @ColumnInfo(name="title")
    var title: String?,
    @ColumnInfo(name="content")
    var content: String?,
): Serializable {
    @PrimaryKey(autoGenerate = true)
    var id = 0

}