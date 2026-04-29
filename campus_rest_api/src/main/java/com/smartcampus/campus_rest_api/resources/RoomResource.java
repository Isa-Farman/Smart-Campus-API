    package com.smartcampus.campus_rest_api.resources;

import com.smartcampus.campus_rest_api.Storage;
import com.smartcampus.campus_rest_api.exceptions.RoomNotEmptyException;
import com.smartcampus.campus_rest_api.models.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // GET /api/v1/rooms.
    @GET
    public Response getAllRooms() {
        //gets all the rooms from the concurrent hashmap and stores in list
        List<Room> list = new ArrayList<>(Storage.getRooms().values());
        return Response.ok(list).build();
    }

    // POST /api/v1/rooms.
    @POST
    public Response createRoom(Room room) {
        //validation to check id
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error("Room 'id' is required.")).build();
        }
        if (Storage.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(error("Room '" + room.getId() + "' already exists.")).build();
        }
        
        // saves to database.
        Storage.getRooms().put(room.getId(), room);
        //return 
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // GET /api/v1/rooms/{roomId}.
    @GET
    @Path("/{roomId}") 
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = Storage.getRooms().get(roomId);
        // If it doesn't exist,send a 404 error.
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error("Room '" + roomId + "' not found.")).build();
        }
        return Response.ok(room).build();
    }

    @DELETE 
    @Path("/{roomId}") 
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = Storage.getRooms().get(roomId);
        //check if the room exists.
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error("Room '" + roomId + "' not found.")).build();
        }
        // Using RoomNotEmptyException to prevent deleting rooms that still hold sensors.
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room '" + roomId + "' still has " + room.getSensorIds().size()
                + " sensor(s) assigned. Remove them first.");
        }
        // if it passes both it deletes
        Storage.getRooms().remove(roomId);
        return Response.noContent().build();
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("error", msg);
        return m;
    }
}