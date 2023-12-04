package handlers;

import com.google.gson.Gson;
import results.ErrorResult;
import results.SuccessResult;
import services.ClearApplicationService;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * ClearApplicationHandler is used for handling the DELETE request method used to delete all
 * data in the database. It uses Gson to read the request and sends it to the ClearApplicationService.
 * Methods in this class return a Json string back to the server.
 */
public class ClearApplicationHandler implements Route {

    private final ClearApplicationService clearService;
    private final Gson gson = new Gson();
    public ClearApplicationHandler(ClearApplicationService clearService) {
        this.clearService = clearService;
    }

    /**
     * handle() calls the ClearApplicationService ClearDatabase function
     */
    @Override
    public Object handle(Request request, Response response) {
        try {
            clearService.ClearDatabase();
            return gson.toJson(new SuccessResult(true));
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(new ErrorResult("Error: " + e.getMessage()));
        }
    }

}
