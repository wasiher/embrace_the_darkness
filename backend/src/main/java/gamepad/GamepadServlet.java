package gamepad;

import base.UserProfile;
import com.google.gson.JsonObject;
import main.AccountService;
import main.ResponseHandler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by fatman on 14/12/15.
 */
public class GamepadServlet extends HttpServlet{
    private static Map<String, String> s_availableKeys = new HashMap<>();
    private AccountService accountService;

    public GamepadServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public static Map<String, String> getAvailableKeys() {
        return s_availableKeys;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String id = String.valueOf(request.getSession().getAttribute("userId"));
        UserProfile userProfile = accountService.getSessions(id);

        JsonObject jsonResponse = new JsonObject();
        if (id == null || userProfile == null) {
            jsonResponse.addProperty("Error", "You are not logged in");
            ResponseHandler.respondWithJSON(response, jsonResponse);
            return;
        }

        String strKey = new BigInteger(130, new Random()).toString(32);

        s_availableKeys.put(strKey, userProfile.getLogin());

        jsonResponse.addProperty("key", strKey);

        ResponseHandler.respondWithJSON(response, jsonResponse);
    }
}
