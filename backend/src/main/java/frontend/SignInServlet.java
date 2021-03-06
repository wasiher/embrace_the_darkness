package frontend;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import main.AccountService;
import main.AccountServiceException;
import main.ResponseHandler;
import base.UserProfile;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import utils.JsonRequestParser;

/**
 * @author v.chibrikov
 */


public class SignInServlet extends HttpServlet {

    @NotNull
    private final AccountService accountService;

    public SignInServlet(@NotNull AccountService accountService) {
        this.accountService = accountService;
    }


    @Override
    public void doPost(@NotNull HttpServletRequest request,
                       @NotNull HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        JsonObject jsonResponse = new JsonObject();

        Long userId = (Long) session.getAttribute("userId");

        JsonObject requestData;

        try {
            requestData = JsonRequestParser.parse(request);
        } catch (IOException e) {
            jsonResponse.addProperty("Status", "Request is invalid. Can't parse json.");
            ResponseHandler.respondWithJSONAndStatus(response, jsonResponse,
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonElement requestEmail = requestData.get("email");
        JsonElement requestPassword = requestData.get("password");

        String email = requestEmail == null ? "" : requestEmail.getAsString();
        String password = requestPassword == null ? "" : requestPassword.getAsString();

        if (email.isEmpty()) {
            jsonResponse.addProperty("Status", "login is required");
        } else if (password.isEmpty()) {
            jsonResponse.addProperty("Status", "password is required");
        } else if (accountService.getSessions(String.valueOf(userId)) == null) {

            try {
                UserProfile profile = accountService.getUser(email);

                if (profile != null && profile.getPassword().equals(password)) {

                    userId = accountService.getAndIncrementID();
                    String key = String.valueOf(userId);

                    assert key != null;
                    session.setAttribute("userId", userId);

                    accountService.addSessions(key, profile);

                    jsonResponse.addProperty("Status", "Login passed");
                } else {
                    jsonResponse.addProperty("Status", "Wrong login/password");
                }
            } catch (AccountServiceException e) {
                jsonResponse.addProperty("Status", "Wrong login/password");
            }
        } else {
            jsonResponse.addProperty("Status", "You are alredy logged in");
        }

        ResponseHandler.respondWithJSON(response, jsonResponse);
    }
}
