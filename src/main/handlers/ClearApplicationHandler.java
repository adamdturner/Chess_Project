package handlers;

import com.google.gson.Gson;
import results.ErrorResult;
import results.SuccessResult;
import services.ClearApplicationService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearApplicationHandler implements Route {

    private final ClearApplicationService clearService;
    private final Gson gson = new Gson();

    public ClearApplicationHandler(ClearApplicationService clearService) {
        this.clearService = clearService;
    }

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
