package java2.lab06;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/")
public class TrainingEndPoint {

    @Inject
    EntityManager em;

    @GET
    @Path("/trainings")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Training> getTrainings() {
        return em.createQuery("SELECT t FROM Training AS t", Training.class).getResultList();
    }

    @GET
    @Path("/training/{id}")
    public Response getTraining(@PathParam("id") Long id)
    {
        Training result = em.find(Training.class, id);
        if (result == null)
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Training with id = " + id + " not found").build();
        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }

    @Transactional
    @POST
    @Path("/training")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Long createTraining(Training training)
    {
        em.persist(training);
        return training.getId();
    }



    @Transactional
    @PUT
    @Path("/training")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTraining(Training training)
    {
        Training result = em.find(Training.class, training.getId());
        if (result == null)
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Training with id = " + training.getId() + " not found").build();

        result.setExercise(training.getExercise());
        result.setToday(training.getToday());
        result.setWeight(training.getWeight());
        result.setEntry(training.getEntry());
        return Response.ok().build();
    }

    @Transactional
    @DELETE
    @Path("/training/{id}")
    public Response removeTraining(@PathParam("id") Long id)
    {
        Training result = em.find(Training.class, id);
        if (result == null)
            return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity("Training with id = " + id + " not found").build();
        em.remove(result);
        return Response.ok().build();
    }
}
