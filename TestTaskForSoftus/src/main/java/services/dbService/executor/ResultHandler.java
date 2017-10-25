package services.dbService.executor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Eshu on 04.02.2017.
 */
public interface ResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException;
}