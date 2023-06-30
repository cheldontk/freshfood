package com.github.cheldontk.freshfood.catalog.application.rest;


import com.github.cheldontk.freshfood.catalog.application.request.CategoryCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.ProductCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.ProductUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.CategoryResponse;
import com.github.cheldontk.freshfood.catalog.application.response.ProductResponse;
import com.github.cheldontk.freshfood.catalog.domain.DomainException;
import com.github.cheldontk.freshfood.catalog.domain.service.DomainProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import java.util.List;

@Path("/catalog")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin-catalog")
@SecurityScheme(securitySchemeName = "freshfood-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/freshfood/protocol/openid-connect/token")))
public class CatalogResource {

    @Inject
    @Claim(standard = Claims.sub)
    String sub;

    @Inject
    DomainProductService service;

    @GET
    @Path("/farms/{id}/categories")
    @Counted(name = "counted by getCategoriesById")
    @SimplyTimed(name = "Simple time to getCategoriesById")
    @Timed(name = "Total time to getCategoriesById")
    @APIResponse(responseCode = "403", description = "Forbidden")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response getCategoriesById(@PathParam("id") Long id) {
        try {
            List<CategoryResponse> categories = service.getAllCategoriesByFarmId(id, sub);
            return Response.accepted(categories).status(Response.Status.OK).build();
        } catch (DomainException de) {
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException fe) {
            return Response.noContent().status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("/farms/{id}/categories")
    @Counted(name = "counted by createCategory")
    @SimplyTimed(name = "Simple time to createCategory")
    @Timed(name = "Total time to createCategory")
    @Transactional
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "409", description = "Conflict")
    public Response createCategory(@PathParam("id") Long farmId, CategoryCreateRequest category) {
        try {
            CategoryResponse category1 = service.createCategory(farmId, sub, category.name);
            return Response.accepted(category1).status(Response.Status.OK).build();
        } catch (DomainException de) {
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException fe) {
            return Response.noContent().status(Response.Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("/products")
    @Counted(name = "counted by getProducts")
    @SimplyTimed(name = "Simple time to getProducts")
    @Timed(name = "Total time to getProducts")
    @APIResponse(responseCode = "204", description = "No Content")
    public Response getProducts() {
        List<ProductResponse> products = service.getAllProducts();
        if (products.isEmpty()) return Response.noContent().build();
        return Response.accepted(products).status(Response.Status.OK).build();
    }

    @GET
    @Path("/farms/{id}/products")
    @Counted(name = "counted by getProductsByFarmId")
    @SimplyTimed(name = "Simple time to getProductsByFarmId")
    @Timed(name = "Total time to getProductsByFarmId")
    @APIResponse(responseCode = "204", description = "No Content")
    public Response getProductsByFarmId(@PathParam("id") Long farmId) {
        List<ProductResponse> products = service.getProductsByFarmId(farmId);
        if (products.isEmpty()) Response.noContent().build();
        return Response.accepted(products).status(Response.Status.OK).build();
    }

    @GET
    @Path("/products/{id}")
    @Counted(name = "counted by getProductById")
    @SimplyTimed(name = "Simple time to getProductById")
    @Timed(name = "Total time to getProductById")
    @APIResponse(responseCode = "204", description = "No Content")
    public Response getProductById(@PathParam("id") Long id) {
        ProductResponse product = service.getProductById(id);
        if (product == null) return Response.noContent().build();
        return Response.accepted(product).status(Response.Status.OK).build();
    }

    @POST
    @Path("/farms/{id}/products")
    @Transactional
    @Counted(name = "counted by createProduct")
    @SimplyTimed(name = "Simple time to createProduct")
    @Timed(name = "Total time to createProduct")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "409", description = "Conflict")
    public Response createProduct(@PathParam("id") Long farmId, ProductCreateRequest productRequest) {
        try {
            ProductResponse product = service.createProduct(farmId, sub, productRequest);
            return Response.accepted(product).status(Response.Status.OK).build();
        } catch (DomainException ex) {
            return Response.noContent().status(Response.Status.CONFLICT).build();
        } catch (ForbiddenException e) {
            throw new ForbiddenException();
        }catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @PUT
    @Path("/farms/{farmId}/products/{id}")
    @Transactional
    @Counted(name = "counted by updateProduct")
    @SimplyTimed(name = "Simple time to updateProduct")
    @Timed(name = "Total time to updateProduct")
    @APIResponse(responseCode = "403", description = "Forbidden")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response updateProduct(@PathParam("farmId") Long farmId, @PathParam("id") Long id, ProductUpdateRequest productRequest) {
        try {
            ProductResponse product = service.updateProduct(farmId, sub, id, productRequest);
            return Response.accepted(product).status(Response.Status.OK).build();
        } catch (DomainException e) {
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException ex) {
            return Response.noContent().status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @Path("/farms/{farmId}/products/{id}")
    @Transactional
    @Counted(name = "counted by deleteProduct")
    @SimplyTimed(name = "Simple time to deleteProduct")
    @Timed(name = "Total time to deleteProduct")
    @APIResponse(responseCode = "404", description = "Not Found")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response deleteProduct(@PathParam("farmId") Long farmId, @PathParam("id") Long id) {
        try {
            ProductResponse product = service.deleteProduct(farmId, sub, id);
            return Response.accepted(product).status(Response.Status.OK).build();
        } catch (DomainException e) {
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException ex) {
            return Response.noContent().status(Response.Status.FORBIDDEN).build();
        }
    }
}
