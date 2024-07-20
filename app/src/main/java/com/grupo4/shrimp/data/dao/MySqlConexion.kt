package com.grupo4.shrimp.data.dao

import com.mysql.jdbc.Connection
import java.sql.DriverManager

object MySqlConexion {
    fun getConexion(): java.sql.Connection? {
        Class.forName("com.mysql.jdbc.Driver")

        return  DriverManager.getConnection(
            "jdbc:mysql://srv1166.hstgr.io:3306/u816849082_ulatina",
            "u816849082_ulatina",
            "p9|gHY!qJ"
        )
    }
}