package com.github.cheldontk.freshfood.marketplace.application.rest;

import com.github.cheldontk.freshfood.marketplace.application.request.AddressRequest;
import com.github.cheldontk.freshfood.marketplace.application.request.CartRequest;
import com.github.cheldontk.freshfood.marketplace.application.request.PaymentRequest;
import com.github.cheldontk.freshfood.marketplace.application.response.CartProductResponse;
import com.github.cheldontk.freshfood.marketplace.domain.DomainException;
import com.github.cheldontk.freshfood.marketplace.domain.service.DomainCartService;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
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

@Path("/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("customer")
@SecurityScheme(securitySchemeName = "freshfood-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/freshfood-web/protocol/openid-connect/token")))
public class CartResource {

    @Inject
    @Claim(standard = Claims.sub)
    String sub;

    @Inject
    @Claim(standard = Claims.email)
    String email;

    @Inject
    @Claim(standard = Claims.preferred_username)
    String fullName;

    @Inject
    PgPool pgPool;

    @Inject
    DomainCartService cartService;

    @GET
    @Counted(name = "counted by getCartProductsByCustomerId")
    @SimplyTimed(name = "Simple time to getCartProductsByCustomerId")
    @Timed(name = "Total time to getCartProductsByCustomerId")
    public Multi<CartProductResponse> getCartProductsByCustomerId() {
        return cartService.findCartProductsByCustomerId(pgPool, sub);
    }

    @POST
    @Counted(name = "counted by addProductToCart")
    @SimplyTimed(name = "Simple time to addProductToCart")
    @Timed(name = "Total time to addProductToCart")
    @APIResponse(responseCode = "416", description = "Stock low")
    public Response addProductToCart(CartRequest cart) {
        try {
            cartService.persist(pgPool, sub, email, fullName, cart);
            return Response.status(Response.Status.CREATED).build();
        } catch (DomainException e) {
            return Response.accepted(JsonbBuilder.create().toJson(e.getMessage())).status(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/{id}/item/{product_id}")
    @Counted(name = "counted by deleteProductFromCart")
    @SimplyTimed(name = "Simple time to deleteProductFromCart")
    @Timed(name = "Total time to deleteProductFromCart")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response removeProductFromCart(@PathParam("id") Long cartId, @PathParam("product_id") Long itemId) {
        try {
            cartService.remove(pgPool, sub, cartId, itemId);
            return Response.status(Response.Status.OK).build();
        }catch (DomainException de){
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        }catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/{id}/payment")
    @Counted(name = "counted by addPaymentToCart")
    @SimplyTimed(name = "Simple time to addPaymentToCart")
    @Timed(name = "Total time to addPaymentToCart")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response addPaymentToCart(@PathParam("id") Long cartId, PaymentRequest payment) {
        try {
            cartService.payment(pgPool, sub, cartId, payment);
            return Response.status(Response.Status.OK).build();
        }catch (DomainException ex){
            return Response.noContent().status(Response.Status.BAD_REQUEST).build();
        }catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
    @PUT
    @Path("/{id}/address")
    @Counted(name = "counted by addAddressToCart")
    @SimplyTimed(name = "Simple time to addAddressToCart")
    @Timed(name = "Total time to addAddressToCart")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response addAddressToCart(@PathParam("id") Long cartId, AddressRequest address) {
        try {
            cartService.address(pgPool, sub, cartId, address);
            return Response.status(Response.Status.OK).build();
        }catch (DomainException ex){
            return Response.noContent().status(Response.Status.BAD_REQUEST).build();
        }catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/{id}/checkout")
    @Counted(name = "counted by checkout")
    @SimplyTimed(name = "Simple time to checkout")
    @Timed(name = "Total time to checkout")
    @APIResponse(responseCode = "402", description = "Payment Required")
    public Response checkout(@PathParam("id") Long cartId) {
        try {
            cartService.checkout(pgPool, sub, cartId);
            return Response.status(Response.Status.OK).build();
        }catch (DomainException de){
            return Response.noContent().status(Response.Status.PAYMENT_REQUIRED).build();
        }catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
