package database.items;

import database.DatabaseCreator;
import function.ItemStatus;
import models.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Item DAO regression tests")
class ItemDAORegressionTest {
    private TestVehicleDAO vehicleDAO;

    @Mock
    private DatabaseCreator databaseCreator;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicleDAO = new TestVehicleDAO();
        vehicleDAO.databaseCreator = databaseCreator;
    }

    @Test
    @DisplayName("VehicleDAO should preserve ID and status from database row")
    void vehicleDaoShouldPreserveIdAndStatus() throws SQLException {
        UUID itemId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(resultSet.getString("ID")).thenReturn(itemId.toString());
        when(resultSet.getString("Name")).thenReturn("Car");
        when(resultSet.getString("Condition")).thenReturn("Used");
        when(resultSet.getLong("Price")).thenReturn(100_000L);
        when(resultSet.getString("Status")).thenReturn("\"SOLD\"");
        when(resultSet.getString("owner_ID")).thenReturn(ownerId.toString());

        Vehicle vehicle = vehicleDAO.map(resultSet);

        assertEquals(itemId, vehicle.getID());
        assertEquals(ItemStatus.SOLD, vehicle.getItemStatus());
    }

    @Test
    @DisplayName("getbySessionID should query sessions table and then load full item row")
    void getBySessionIdShouldUseSessionsTable() throws SQLException {
        UUID sessionId = UUID.randomUUID();
        when(databaseCreator.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        vehicleDAO.getbySessionID(sessionId);

        verify(connection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("sessions"), "Query should use table sessions");
        assertFalse(sqlCaptor.getValue().contains("FROM session "), "Query should not use missing table session");
    }

    private static class TestVehicleDAO extends VehicleDAO {
        Vehicle map(ResultSet rs) throws SQLException {
            return mapResultSetToItem(rs);
        }
    }
}
