package ui;

import com.google.gson.Gson;
import models.AuthToken;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.SuccessResult;
import results.UserAuthResult;

import java.net.HttpURLConnection;

public class PreLoginFacade {
    private final ServerConnection serverConnection;
    private final Gson gson;

    public PreLoginFacade(String serverUrl) {
        this.serverConnection = new ServerConnection(serverUrl);
        this.gson = new Gson();
    }

    public boolean clearAll() {
        try {
            // Send the DELETE request to the /db endpoint
            String response = serverConnection.sendRequest("/db", null, "DELETE", null);

            // Check the response code from serverConnection
            int responseCode = serverConnection.getLastResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                SuccessResult result = gson.fromJson(response, SuccessResult.class);
                // Return true or false based on the success field of the result
                return result.success();
            } else {
                // Handle non-OK responses
                System.out.println("Failed to clear database.");
                return false;
            }
        } catch (Exception e) {
            // Handle exceptions
            System.out.println("Error clearing database: " + e.getMessage());
            return false;
        }
    }

    public AuthToken register(String username, String password, String email) {
        try {
            // Create a RegisterRequest object
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);

            // Send the request and receive the response
            String response = serverConnection.sendRequest("/user", registerRequest, "POST", null);

            // Check the response code from serverConnection
            int responseCode = serverConnection.getLastResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                UserAuthResult result = gson.fromJson(response, UserAuthResult.class);
                String authTokenString = result.authToken();
                String returnedUsername = result.username();
                return new AuthToken(authTokenString, returnedUsername);
            } else {
                // Handle failed registration
                System.out.println("Registration failed.");
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions
            System.out.println("Registration error: " + e.getMessage());
            return null;
        }
    }

    public AuthToken login(String username, String password) {
        try {
            // Create a LoginRequest object
            LoginRequest loginRequest = new LoginRequest(username, password);

            // Send the request and receive the response
            String response = serverConnection.sendRequest("/session", loginRequest, "POST", null);

            // Check the response code from serverConnection
            int responseCode = serverConnection.getLastResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                UserAuthResult result = gson.fromJson(response, UserAuthResult.class);
                String authTokenString = result.authToken();
                String returnedUsername = result.username();
                return new AuthToken(authTokenString, returnedUsername);
            } else {
                // Handle failed authentication
                System.out.println("Login failed: Invalid username or password.");
                return null;
            }
        } catch (Exception e) {
            // Handle exceptions
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

}

