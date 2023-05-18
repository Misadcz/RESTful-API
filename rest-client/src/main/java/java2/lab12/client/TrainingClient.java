package java2.lab12.client;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collection;
import java.util.List;


public interface TrainingClient {

    @POST
    @Path("/training")
    @Consumes(MediaType.APPLICATION_JSON)
    Long createTraining(Training training);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/trainings")
    List<Training> getTrainings();

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/training")
    void updateTrainings(Training training);


    @DELETE
    @Path("/training/{id}")
    void removeTraining(@PathParam("id") Long id);


}
