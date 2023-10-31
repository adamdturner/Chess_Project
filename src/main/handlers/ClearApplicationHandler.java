package handlers;

import results.ErrorResult;
import results.Result;
import services.ClearApplicationService;
import spark.Request;
import spark.Response;

public class ClearApplicationHandler {
    private final ClearApplicationService clearService;

    public ClearApplicationHandler(ClearApplicationService clearService) {
        this.clearService = clearService;
    }

    public Result handleClearRequest(Request req, Response res) {
        try {
            Result result = clearService.clearDatabase();
            res.status(200);
            return result;
        } catch (Exception e) {
            Result error = new ErrorResult("Error: " + e.getMessage());
            res.status(500);
            return error;
        }
    }
}
