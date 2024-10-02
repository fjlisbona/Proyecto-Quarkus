package com.example;


import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import java.net.URI;


import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.transaction.Transactional;

@Path("/products")
public class ProductResource {
      
   
    @Inject
    @Location("products.html")
    Template products;

    // GET: Obtener todos los productos
    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listProducts() {
        return products.data("products", Product.listAll())
                       .data("editProduct", null);  // Añadimos esto
    }

    // GET: Obtener un producto por ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response getProduct(@PathParam("id") Long id) {
        Product product = Product.findById(id);
        if (product != null) {
            return Response.ok(product).build(); // 200 OK con el producto
        }
        return Response.status(Response.Status.NOT_FOUND).build(); // 404 Not Found
    }

    // POST: Crear un nuevo producto
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(@FormParam("name") String name, 
                              @FormParam("description") String description, 
                              @FormParam("price") Double price) {
        Product product = new Product();
        product.name = name;
        product.description = description;
        product.price = price;
        product.persist();
        return Response.seeOther(URI.create("/products")).build();
    }

    // PUT: Actualizar un producto existente
    @POST
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(@PathParam("id") Long id,
                                  @FormParam("name") String name, 
                                  @FormParam("description") String description, 
                                  @FormParam("price") Double price,
                                  @FormParam("_method") String method) {
        if (!"PUT".equalsIgnoreCase(method)) {
            return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
        }
        Product product = Product.findById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        product.name = name;
        product.description = description;
        product.price = price;
        return Response.seeOther(URI.create("/products")).build();
    }


    // GET: Obtener formulario de edición para un producto
    @GET
    @Path("/{id}/edit")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance editProductForm(@PathParam("id") Long id) {
        Product editProduct = Product.findById(id);
        return products.data("editProduct", editProduct)
                       .data("products", Product.listAll());
    }

   

    @POST
    @Path("/{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response handleProductAction(@PathParam("id") Long id, 
                                        @FormParam("_method") String method,
                                        @FormParam("name") String name, 
                                        @FormParam("description") String description, 
                                        @FormParam("price") Double price) {
        if ("DELETE".equalsIgnoreCase(method)) {
            return deleteProduct(id);
        } else if ("PUT".equalsIgnoreCase(method)) {
            return updateProduct(id, name, description, price);
        }
        return Response.status(Response.Status.METHOD_NOT_ALLOWED).build();
    }

    private Response deleteProduct(Long id) {
        Product product = Product.findById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        product.delete();
        return Response.seeOther(URI.create("/products")).build();
    }

    private Response updateProduct(Long id, String name, String description, Double price) {
        Product product = Product.findById(id);
        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        product.name = name;
        product.description = description;
        product.price = price;
        return Response.seeOther(URI.create("/products")).build();
    }
}

