package com.github.cheldontk.freshfood.marketplace.application.rest;

import com.github.cheldontk.freshfood.marketplace.application.response.FarmProductsResponse;
import com.github.cheldontk.freshfood.marketplace.application.response.ProductResponse;
import com.github.cheldontk.freshfood.marketplace.domain.service.DomainProductService;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/products")
@RolesAllowed("customer")
@SecurityScheme(securitySchemeName = "freshfood-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/freshfood-web/protocol/openid-connect/token")))
public class ProductResource {

    @Inject
    PgPool pgPool;

    @Inject
    DomainProductService productService;

    @GET
    @PermitAll
    @Counted(name = "counted by getProducts")
    @SimplyTimed(name = "Simple time to getProducts")
    @Timed(name = "Total time to getProducts")
    public Multi<ProductResponse> getProducts() {
        return productService.findAll(pgPool);
    }

    @GET
    @Path("/farm/{id}")
    @PermitAll
    @Counted(name = "counted by getProductsByFarmId")
    @SimplyTimed(name = "Simple time to getProductsByFarmId")
    @Timed(name = "Total time to getProductsByFarmId")
    public  Multi<FarmProductsResponse> getProductsByFarmId(@PathParam("id") Long id) {
        return productService.findProductsByFarmId(pgPool, id);
    }


    @GET
    @Path("/category/{name}")
    @PermitAll
    @Counted(name = "counted by getProductsByCategory")
    @SimplyTimed(name = "Simple time to getProductsByCategory")
    @Timed(name = "Total time to getProductsByCategory")
    public  Multi<FarmProductsResponse> getProductsByCategoryId(@PathParam("name") String categoryName) {
        return productService.getProductsByCategory(pgPool, categoryName);
    }
}
