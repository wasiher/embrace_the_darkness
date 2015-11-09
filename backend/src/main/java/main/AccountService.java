package main;


import base.UserProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by v.chibrikov on 13.09.2014.
 */
public class AccountService {

    @NotNull
    private final Map<String, UserProfile> users = new HashMap<>();
    @NotNull
    private final Map<String, UserProfile> sessions = new HashMap<>();

    public boolean addUser(String userName, UserProfile userProfile) {
        if (users.containsKey(userName))
            return false;
        users.put(userName, userProfile);
        return true;
    }

    public void addSessions(@NotNull String sessionId, @NotNull UserProfile userProfile) {
        sessions.put(sessionId, userProfile);
    }

    public int getUsersQuantity() {
        return users.size();
    }

    public int getSessionsQuantity() {
        return sessions.size();
    }

    @Nullable
    public UserProfile getUser(@Nullable String userName) {
        return users.get(userName);
    }

    @Nullable
    public UserProfile getSessions(@Nullable String sessionId) {
        return sessions.get(sessionId);
    }


    public boolean deleteSessions(@Nullable String sessionId) {
        if (sessions.get(sessionId) != null) {
            sessions.remove(sessionId);
            return true;
        }
        return false;
    }
}
