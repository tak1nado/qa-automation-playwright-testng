package com.project.qa.helpers.user.engine;

import com.project.qa.api.controller.actions.AuthActions;
import com.project.qa.helpers.managers.users.UsersManager;
import com.project.qa.helpers.models.users.User;
import com.project.qa.helpers.models.users.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserSessions {

    @Autowired private UsersManager usersManager;
    @Autowired private AuthActions authActions;
    private final ArrayList<Session> allSessionsList = new ArrayList<>();
    private final InheritableThreadLocal<ArrayList<Session>> tlSession = new InheritableThreadLocal<>();

    private final UserSessionFactory userFactory = new UserSessionFactory();

    public boolean isSessionsListEmpty() {
        return allSessionsList.isEmpty();
    }

    public synchronized void setActiveUserSession(UserRole userRole) {
        User user = usersManager.getUsers().stream()
                .filter(user1 -> user1.getUserRole().equals(userRole))
                .findAny()
                .orElseThrow(() -> new NullPointerException("No such user role in properties: " + userRole.toString()));

        if (tlSession.get() == null) {
            tlSession.set(new ArrayList<>());
            createUserSession(user).setActive(true);
        } else if (getActiveUserSession() == null || !getActiveUserSession().getUserRole().equals(userRole)) {
            tlSession.get().forEach(user1 -> user1.setActive(false));
            tlSession.get().stream()
                    .filter(user1 -> user1.getUserRole().equals(userRole))
                    .findFirst()
                    .orElseGet(() -> createUserSession(user)).setActive(true);
        }
    }

    public synchronized UserSession getActiveUserSession() {
        return (UserSession) tlSession.get().stream().filter(session -> session instanceof UserSession).filter(Session::isActive).findAny().orElse(null);
    }

    public UserSession getAnyUserSessionForUser(User user) {
        return (UserSession) allSessionsList.stream()
                .filter(session -> session instanceof UserSession)
                .filter(userSession -> ((UserSession) userSession).getUser().equals(user))
                .findAny().orElse(null);
    }

    public UserSession getUserSession(User user) {
        if (tlSession.get() == null) {
            tlSession.set(new ArrayList<>());
            return createUserSession(user);
        } else {
            return (UserSession) tlSession.get().stream()
                    .filter(session -> session instanceof UserSession)
                    .filter(session -> ((UserSession) session).getUser().equals(user))
                    .findFirst()
                    .orElseGet(() -> createUserSession(user));
        }
    }

    public UserSession createUserSession(User user) {
        tlSession.get().add(userFactory.getUserSession(user));
        allSessionsList.add(userFactory.getUserSession(user));
        return userFactory.getUserSession(user);
    }
}
