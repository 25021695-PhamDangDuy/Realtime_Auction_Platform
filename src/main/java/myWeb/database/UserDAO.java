package myWeb.database;

import myWeb.models.User;

import java.util.UUID;

public interface UserDAO<T extends User> extends DataAccessObject<T> {
}
