package com.example;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.is;
@QuarkusIntegrationTest
class ProductResourceIT extends ProductResourceTest {

    @Test
    public void testGetAllProductsEndpoint() {
        RestAssured.given()
                .when().get("/products")
                .then()
                .statusCode(200)
                .body("$.size()", is(0));  // Verifica que la lista está vacía inicialmente
    }

    @Test
    public void testCreateProductEndpoint() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"name\": \"TestProduct\", \"description\": \"TestDescription\", \"price\": 10.0}")
                .when().post("/products")
                .then()
                .statusCode(201); // Verifica que el recurso fue creado
    }
    @Test
    public void testUpdateProductEndpoint() {
        RestAssured.given()
                .contentType("application/json")
                .body("{\"name\": \"UpdatedProduct\", \"description\": \"UpdatedDescription\", \"price\": 20.0}")
                .when().put("/products/1")
                .then()
                .statusCode(200); // Verifica que el recurso fue actualizado
    }

    @Test
    public void testDeleteProductEndpoint() {
        RestAssured.given()
                .when().delete("/products/1")
                .then()
                .statusCode(204); // Verifica que el recurso fue eliminado
    }

    @Test
    public void testGetProductByIdEndpoint() {
        RestAssured.given()
                .when().get("/products/1")
                .then()
                .statusCode(200); // Verifica que el recurso fue obtenido
    }

    
    
    
}
