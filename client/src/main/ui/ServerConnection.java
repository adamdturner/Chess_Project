package ui;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerConnection {
    private final String serverUrl;
    private final Gson gson;
    private int lastResponseCode;

    public ServerConnection(String serverUrl) {
        this.serverUrl = serverUrl;
        this.gson = new Gson();
        this.lastResponseCode = 200;
    }

    public String sendRequest(String endpoint, Object requestData, String method, String authToken) throws Exception {
        // For GET requests, append query parameters to the URL
        // For this example, I'm assuming no query parameters for GET requests
        // Modify this as per your API requirements
        URL url = new URI(serverUrl + endpoint).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Set the request method
            connection.setRequestMethod(method);

            // Set the common headers (e.g., for authorization)
            if (authToken != null && !authToken.isEmpty()) {
                connection.setRequestProperty("Authorization", authToken);
            }

            // Handle the request body for POST, PUT, etc.
            if (!method.equals("GET") && requestData != null) {
                String jsonRequestData = gson.toJson(requestData);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonRequestData.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // Read the response
            lastResponseCode = connection.getResponseCode();
            if (lastResponseCode == HttpURLConnection.HTTP_OK || lastResponseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                }
            } else {
                // Handle non-OK responses
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = "";
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString(); // Or throw an exception
                }
            }
        } finally {
            connection.disconnect();
        }
    }
    public int getLastResponseCode() {
        return lastResponseCode;
    }
}


