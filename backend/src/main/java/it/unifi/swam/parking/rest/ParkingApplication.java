package it.unifi.swam.parking.rest;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@DataSourceDefinition(name = "java:app/datasources/ParkingDS", className = "org.postgresql.ds.PGSimpleDataSource", url = "jdbc:postgresql://127.0.0.1:5434/parking_db", user = "postgres", password = "1234")
@ApplicationPath("/api")
public class ParkingApplication extends Application {
}
