package com.github.cheldontk.freshfood.catalog.application.rest;


import com.github.cheldontk.freshfood.catalog.application.request.FarmCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.FarmUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.FarmResponse;
import com.github.cheldontk.freshfood.catalog.domain.DomainException;
import com.github.cheldontk.freshfood.catalog.domain.service.DomainFarmService;
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

@Path("/farms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin-farms")
@SecurityScheme(securitySchemeName = "freshfood-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/freshfood/protocol/openid-connect/token")))
public class FarmResource {


    @Inject
    @Claim(standard = Claims.sub)
    String sub;

    @Inject
    DomainFarmService service;

    @GET
    @Path("/all")
    @Counted(name = "counted by getFarms")
    @SimplyTimed(name = "Simple time to getFarms")
    @Timed(name = "Total time to getFarms")
    @APIResponse(responseCode = "204", description = "No Content")
    public Response getAllFarms() {
        List<FarmResponse> farms = service.getAllFarms();
        if (farms.isEmpty()) return Response.noContent().build();
        return Response.accepted(farms).status(Response.Status.OK).build();

    }

    @GET
    @Path("/my")
    @Counted(name = "counted by getMyFarms")
    @SimplyTimed(name = "Simple time to getMyFarms")
    @Timed(name = "Total time to getMyFarms")
    @APIResponse(responseCode = "204", description = "No Content")
    public Response getMyFarms() {
        List<FarmResponse> farms = service.getMyFarms(sub);
        if (farms.isEmpty()) return Response.noContent().build();
        return Response.accepted(farms).status(Response.Status.OK).build();
    }

    @GET
    @Path("/{id}")
    @Counted(name = "counted by getFarmById")
    @SimplyTimed(name = "Simple time to getFarmById")
    @Timed(name = "Total time to getFarmById")
    @APIResponse(responseCode = "204", description = "No Content")
    public Response getFarmById(@PathParam("id") Long id) {
        FarmResponse farm = service.getFarmById(id);
        if (farm == null) return Response.noContent().build();
        return Response.accepted(farm).status(Response.Status.OK).build();
    }

    @POST
    @Transactional
    @Counted(name = "counted by createFarm")
    @SimplyTimed(name = "Simple time to createFarm")
    @Timed(name = "Total time to createFarm")
    @APIResponse(responseCode = "201", description = "Created")
    @APIResponse(responseCode = "409", description = "Conflict")
    public Response createFarm(FarmCreateRequest farmRequest) {
        try {
            FarmResponse farm = service.createFarm(farmRequest, sub);
            return Response.accepted(farm).status(Response.Status.CREATED).build();
        } catch (DomainException ex) {
            return Response.noContent().status(Response.Status.CONFLICT).build();
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Counted(name = "counted by updateFarm")
    @SimplyTimed(name = "Simple time to updateFarm")
    @Timed(name = "Total time to updateFarm")
    @APIResponse(responseCode = "403", description = "Forbidden")
    @APIResponse(responseCode = "404", description = "Not Found")
    public Response updateFarm(FarmUpdateRequest farmRequest, @PathParam("id") Long id) {
        try {
            FarmResponse farm = service.updateFarm(sub, id, farmRequest);
            return Response.accepted(farm).status(Response.Status.OK).build();
        } catch (DomainException e) {
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException ex) {
            return Response.noContent().status(Response.Status.FORBIDDEN).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Counted(name = "counted by deleteFarm")
    @SimplyTimed(name = "Simple time to deleteFarm")
    @Timed(name = "Total time to deleteFarm")
    @APIResponse(responseCode = "404", description = "Not Found")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response deleteFarm(@PathParam("id") Long id) {
        try {
            FarmResponse farm = service.deleteFarm(sub, id);
            return Response.accepted(farm).status(Response.Status.OK).build();
        } catch (DomainException e) {
            return Response.noContent().status(Response.Status.NOT_FOUND).build();
        } catch (ForbiddenException ex) {
            return Response.noContent().status(Response.Status.FORBIDDEN).build();
        }
    }
}
