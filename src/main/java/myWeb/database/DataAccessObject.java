package myWeb.database;

import java.util.List;
import java.util.UUID;

//DAO pattern : Mẫu thiết kế giúp an toàn dữ liệu và tách biệt logic server giữa các tầng với DB
//Generic Class: Class tổng quát nhận đầu vào là một class T bất kì -> giúp đa dạng hóa methods
public interface DataAccessObject<T> {
    void update(T t);

    void save(T t);

    T get(UUID ID);

    List<T>  getAll();

}
