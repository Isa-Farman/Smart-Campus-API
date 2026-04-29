import com.smartcampus.campus_rest_api.exceptions.LinkedResourceNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;


@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status",  422);
        body.put("error",   "Unprocessable Entity");
        body.put("message", ex.getMessage());
        body.put("hint",    "Ensure the roomId in your payload refers to an existing room.");
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(body).build();
    }
}