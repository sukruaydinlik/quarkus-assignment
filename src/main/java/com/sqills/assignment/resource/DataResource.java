package com.sqills.assignment.resource;

import com.sqills.assignment.dto.ReadResponse;
import com.sqills.assignment.dto.StoreRequest;
import com.sqills.assignment.dto.StoreResponse;
import com.sqills.assignment.entity.ProcessedData;
import com.sqills.assignment.service.DataProcessor;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DataResource {

    @Inject
    DataProcessor dataProcessor;

    @POST
    public Response store(StoreRequest request) {
        if (request == null || request.data == null || request.data.inputText == null || request.data.inputText.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // Requirement: "return a unique id so that it could be provided to the second API when reading a previously stored data."
        // BUT the processing is asynchronous (via JMS).
        // If I return an ID now, the data might not be in the DB yet.
        // However, usually "unique id" in these assignments refers to the DB ID.
        // To satisfy both asynchronous processing requirement AND returning a unique ID, 
        // I have two options:
        // 1. Generate a UUID now, send it via JMS, and use it as a key in DB.
        // 2. Do it synchronously for simplicity if allowed, but JMS is required.
        
        // Let's use a UUID for the response and send it to JMS.
        // Wait, the requirement says: "The value of input_text should be published to a JMS topic... A consumer... receive the input_text... stored in the database."
        // If I want to return the ID *now*, I need to know it before the consumer stores it.
        
        // Re-reading: "note that it needs to return a unique id so that it could be provided to the second API when reading a previously stored data."
        
        // I'll create a record in DB immediately with "PROCESSING" status or just an empty output, and return its ID.
        // Then the consumer updates it.
        
        Long id = createPendingRecord();
        dataProcessor.publishToTopic(id + ":" + request.data.inputText);
        
        return Response.status(Response.Status.ACCEPTED).entity(new StoreResponse(id)).build();
    }

    @Transactional
    public Long createPendingRecord() {
        ProcessedData entity = new ProcessedData();
        entity.persist();
        return entity.id;
    }

    @GET
    @Path("/{id}")
    public Response read(@PathParam("id") Long id) {
        ProcessedData entity = ProcessedData.findById(id);
        if (entity == null || entity.outputText == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(new ReadResponse(entity.outputText)).build();
    }
}
