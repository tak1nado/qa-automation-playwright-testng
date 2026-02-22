package com.project.qa.helpers.managers.users;

import com.project.qa.Cockpit;
import com.project.qa.backoffice.AdminCockpit;
import com.project.qa.backoffice.user.BackofficeUserRole;
import com.project.qa.helpers.exceptions.InstanceAlreadyPickedException;
import com.project.qa.helpers.exceptions.NotFoundException;
import com.project.qa.helpers.models.users.User;
import com.project.qa.helpers.models.users.UserRole;
import com.project.qa.storefront.StorefrontCockpit;
import com.project.qa.storefront.user.StorefrontUserRole;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class UsersManager {
    private final ArrayList<User> users = new ArrayList<>();
    private final InheritableThreadLocal<ArrayList<User>> tlUsers = new InheritableThreadLocal<>();
    private final ArrayList<User> pickedUsers = new ArrayList<>();

    public User createInstance(String role, String username, String password, Cockpit userCockpit) {
        User user = new User(username, password, userCockpit);
        this.users.add(user);
        user.setUserRole(parseUserRoles(userCockpit, role));
        return user;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    protected UserRole parseUserRoles(Cockpit userCockpit, String userRoleName) {
        if (userCockpit instanceof StorefrontCockpit) {
            return Stream.of(StorefrontUserRole.values())
                    .filter(storefrontCockpitUserRole -> storefrontCockpitUserRole.getRoleName().equalsIgnoreCase(userRoleName))
                    .findAny().orElseThrow(() -> new NotFoundException(String.format("User with role %s for cockpit %s is not found in framework", userRoleName, userCockpit)));
        } else if (userCockpit instanceof AdminCockpit) {
            return Stream.of(BackofficeUserRole.values())
                    .filter(backofficeCockpitUserRole -> backofficeCockpitUserRole.getRoleName().equalsIgnoreCase(userRoleName))
                    .findAny().orElseThrow(() -> new NotFoundException(String.format("User with role %s for cockpit %s is not found in framework", userRoleName, userCockpit)));
        } else {
            throw new NotFoundException(String.format("Cockpit %s is not found in framework", userCockpit));
        }
    }

    public User getUserByCockpit(Cockpit cockpit) {
        return this.users.stream().filter(user -> user.getUserCockpit().equals(cockpit)).findAny()
                .orElseThrow(() -> new NotFoundException("No user found with cockpit: " + cockpit));
    }

    public ArrayList<User> getAllUsers() {
        return users;
    }

    public List<User> getTestUsers() {
        return getAllUsers().stream()
                .filter(User::isTest)
                .collect(Collectors.toList());
    }

    public User getUserByRole(UserRole userRole) {
        return this.getAllUsers().stream()
                .filter(user -> user.getUserRole().equals(userRole)).findAny()
                .orElseThrow(() -> new NotFoundException("No user with role: " + userRole));
    }

    //We need test user flag if we do not want to use default/hardcoded users not to corrupt any default data
    public User getTestUserByRole(UserRole userRole) {
        return this.getTestUsers().stream()
                .filter(user -> user.getUserRole().equals(userRole)).findAny()
                .orElseThrow(() -> new NotFoundException("No user with role: " + userRole));
    }

    //Default users are hardcoded users that are created out of test execution before test starts and imported from properties
    public User getDefaultUserByRole(UserRole userRole) {
        return this.getAllUsers().stream()
                .filter(user -> !user.isTest())
                .filter(user -> user.getUserRole().equals(userRole)).findAny()
                .orElseThrow(() -> new NotFoundException("No user with role: " + userRole));
    }

    //Pick and unpick functionality needed to leverage users during test execution in parallel mode to avoid data collision
    public synchronized List<User> getNotPickedUsers() {
        return getTestUsers().stream()
                .filter(user -> !pickedUsers.contains(user))
                .collect(Collectors.toList());
    }

    public synchronized User pick(User user) throws InstanceAlreadyPickedException {
        if (!isPicked(user)) {
            this.pickedUsers.add(user);
            if (this.tlUsers.get() == null) {
                this.tlUsers.set(new ArrayList<>());
                this.tlUsers.get().add(user);
            } else if (!tlUsers.get().contains(user)) {
                this.tlUsers.get().add(user);
            }
        }
        return user;
    }

    public boolean isPicked(User user) {
        return this.pickedUsers.contains(user);
    }

    public synchronized void unpickThreadUser(User user) {
        if (this.tlUsers.get() != null) {
            this.pickedUsers.remove(user);
            this.tlUsers.get().remove(user);
        }
    }

    public synchronized void unpickThreadUsers() {
        if (this.tlUsers.get() != null) {
            this.pickedUsers.removeAll(this.tlUsers.get());
            this.tlUsers.get().clear();
        }
    }
}
