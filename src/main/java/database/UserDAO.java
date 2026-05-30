package database;

import models.User;

public interface UserDAO<T extends User> extends DataAccessObject<T> {
}
