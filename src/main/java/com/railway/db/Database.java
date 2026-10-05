package com.railway.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    /*
     * Local MySQL URL is used when running the project
     * on your own computer.
     *
     * For cloud hosting, RAILWAY_DB_URL can be set
     * as an environment variable.
     */
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/railway_ticket_system"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Asia/Kolkata"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8";

    private static final String URL =
            System.getenv().getOrDefault(
                    "RAILWAY_DB_URL",
                    DEFAULT_URL
            );

    private static final String USER =
            System.getenv().getOrDefault(
                    "RAILWAY_DB_USER",
                    "root"
            );

    private static final String PASSWORD =
            System.getenv("RAILWAY_DB_PASSWORD");


    public static Connection getConnection()
            throws Exception {

        if (PASSWORD == null
                || PASSWORD.isBlank()) {

            throw new Exception(
                    "MySQL password is not configured."
            );
        }

        Class.forName(
                "com.mysql.cj.jdbc.Driver"
        );

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}