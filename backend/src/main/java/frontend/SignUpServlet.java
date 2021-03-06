package frontend;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.AccountService;
import base.UserProfile;
import main.AccountServiceException;
import org.jetbrains.annotations.NotNull;
import utils.JsonRequestParser;
import main.ResponseHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by v.chibrikov on 13.09.2014.
 */


public class SignUpServlet extends HttpServlet {
    @NotNull
    private final AccountService accountService;

    public SignUpServlet(@NotNull AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void doPost(@NotNull HttpServletRequest request,
                       @NotNull HttpServletResponse response) throws ServletException {

        JsonObject jsonResponse = new JsonObject();

        JsonObject requestJsonData;

        try {
            requestJsonData = JsonRequestParser.parse(request);
        } catch (IOException e) {
            jsonResponse.addProperty("Status", "Request is invalid. Can't parse json.");
            ResponseHandler.respondWithJSONAndStatus(response, jsonResponse,
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonElement requestEmail = requestJsonData.get("email");
        JsonElement requestPassword = requestJsonData.get("password");

        String name = requestEmail == null ? "" : requestEmail.getAsString();
        String password = requestPassword == null ? "" : requestPassword.getAsString();

        if (name.isEmpty()) {
            jsonResponse.addProperty("Status", "login is required");
        } else if (password.isEmpty()) {
            jsonResponse.addProperty("Status", "password is required");
        } else {

            try {
                accountService.addUser(new UserProfile(name, password, ""));

                jsonResponse.addProperty("Status", "New user created");
            } catch (AccountServiceException e) {
                //jsonResponse.addProperty("Status", e.getMessage());

                jsonResponse.addProperty("Status", "User with name: " + name + " already exists");
            }
        }

        ResponseHandler.respondWithJSON(response, jsonResponse);
    }
}
