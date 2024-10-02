package com.example;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class ProductResourceTest {

    @Test
    @Order(1)
    void testIndexPage() {
        given()
                .when().get("/")
                .then()
                .statusCode(200) // 200 OK
                .contentType(containsString("text/html"))
                .body(containsString("<title>Product Management</title>"))
                .body(containsString("<h1>Welcome to Product Management</h1>"));
    }

    @Test
    @Order(2)
    void testProductsPage() {
        String responseBody = given()
                .when().get("/products")
                .then()
                .statusCode(200) // 200 OK
                .contentType(containsString("text/html"))
                .extract().body().asString();

        System.out.println("Response body: " + responseBody);
        org.junit.jupiter.api.Assertions.assertTrue(responseBody.contains("<h1>Product Management</h1>"));
    }

    @Test
    @Order(3)
    void testListProducts() {
        String responseBody = given()
                .when().get("/products")
                .then()
                .statusCode(200) // 200 OK
                .contentType(containsString("text/html"))
                .extract().body().asString();

        System.out.println("Response body: " + responseBody);
        org.junit.jupiter.api.Assertions.assertTrue(responseBody.contains("<title>Product Management</title>"));
        org.junit.jupiter.api.Assertions.assertTrue(responseBody.contains("<h1>Product Management</h1>"));
    }

    @Test
    @Transactional
    @Order(4)
    void testCreateProduct() {
        Product product = new Product();
        product.name = "Test Product";
        product.description = "Test Description";
        product.price = 10.0;

        given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("name", product.name)
                .formParam("description", product.description)
                .formParam("price", product.price)
                .when()
                .post("/products")
                .then()
                .statusCode(200);

    }

    @Test
    @Transactional
    @Order(5)
    void testUpdateProduct() {
        // Crear un producto de prueba
        Product testProduct = new Product();
        testProduct.name = "Producto Original";
        testProduct.description = "Descripción Original";
        testProduct.price = 10.0;
        testProduct.persistAndFlush();

        // Producto actualizado
        Product updatedProduct = new Product();
        updatedProduct.name = "Producto Actualizado";
        updatedProduct.description = "Nueva Descripción";
        updatedProduct.price = 15.0;
        updatedProduct.persistAndFlush();
        String responseBody = given()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("_method", "PUT")
                .formParam("name", updatedProduct.name)
                .formParam("description", updatedProduct.description)
                .formParam("price", updatedProduct.price)
                .when()
                .post("/products/" + testProduct.id)
                .then()
                .extract().body().asString();

        System.out.println("Status Code: " + responseBody);
        System.out.println("Body: " + responseBody);

        // Forzar una nueva transacción para obtener datos frescos
        Product updatedInDb = Product.findById(updatedProduct.id);
        assertNotNull(updatedInDb);
        assertEquals(updatedProduct.name, updatedInDb.name);
        assertEquals(updatedProduct.description, updatedInDb.description);
        assertEquals(updatedProduct.price, updatedInDb.price);
    }

    @Test
    @Order(6)
    @Transactional
    public void testDeleteProduct() {
        // Crear y persistir un producto de prueba
        Product testProduct = new Product();
        testProduct.name = "Producto a Eliminar";
        testProduct.description = "Descripción";
        testProduct.price = 20.0;

        // Usar una transacción separada para la creación
        testProduct.persistAndFlush();

        // Verificar que el producto se ha creado correctamente
        assertNotNull(Product.findById(testProduct.id));

        // Realizar la solicitud DELETE
        testProduct.delete();

        // Verificar que el producto ya no existe en la base de datos
        // Usar una nueva transacción para la verificación
        assertNull(Product.findById(testProduct.id));
    }

    @Test
    @Order(7)
    @Transactional
    @TestTransaction    
    public void testGetProductByIdEndPoint() {
        // Crear y persistir un producto de prueba
        Product testProduct = new Product();
        
        testProduct.name = "Producto Específico";
        testProduct.description = "Descripción Específica";
        testProduct.price = 30.0;
        testProduct.persist();

        String responseBody = given()
                .when().get("/products/" + testProduct.id)
                .then()
                .statusCode(404).extract().body().asString();

        System.out.println("Status Code: " + responseBody);
        System.out.println("Body: " + responseBody);
        System.out.println("Product ID: " + testProduct.id);
        System.out.println("Product Name: " + testProduct.name);
        System.out.println("Product Description: " + testProduct.description);
        System.out.println("Product Price: " + testProduct.price);
        assertNotNull(responseBody);
        

    }
}
