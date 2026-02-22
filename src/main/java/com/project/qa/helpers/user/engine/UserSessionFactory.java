package com.project.qa.helpers.user.engine;

import com.project.qa.helpers.models.users.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserSessionFactory {

    private final ThreadLocal<Session> tlSession = new ThreadLocal<>();
    private final Map<Session, String> userToKeyMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<Session, Thread> userToThread = Collections.synchronizedMap(new HashMap<>());

    public synchronized UserSession getUserSession(User user) {
        String newKey = this.createKey(user);
        if(this.tlSession.get() == null) {
            this.createNewUserSession(user);
        } else {
            String key = this.userToKeyMap.get(this.tlSession.get());
            if(key == null) {
                this.createNewUserSession(user);
            } else if(!newKey.equals(key)) {
                this.createNewUserSession(user);
            }
        }
        return (UserSession) this.tlSession.get();
    }

    private synchronized void createNewUserSession(User user) {
        String newKey = this.createKey(user);
        UserSession userSession = this.newUserSession(user);
        this.userToKeyMap.put(userSession, newKey);
        this.userToThread.put(userSession, Thread.currentThread());
        this.tlSession.set(userSession);
    }

    private String createKey(User user) {
        return user.getUsername();
    }

    private UserSession newUserSession(User user) {
        return new UserSession(user);
    }
}
