package database.items;

import database.DatabaseCreator;
import models.Electronics;
import models.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

@DisplayName("ElectronicDAO regression tests")
class ElectronicDAORegressionTest {
    private ElectronicDAO electronicDAO;

    @Mock
    private DatabaseCreator databaseCreator;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        electronicDAO = new ElectronicDAO();
        electronicDAO.databaseCreator = databaseCreator;
    }

    @Test
    @DisplayName("update() should bind HSD first and ID second")
    void updateShouldBindHsdThenId() throws SQLException {
        Seller owner = new Seller("seller_electronic", "secret");
        Electronics electronics = new Electronics(owner, "Phone", 5_000L, "New", 12);
        when(databaseCreator.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);

        electronicDAO.update(electronics);

        verify(preparedStatement).setInt(1, 12);
        verify(preparedStatement).setString(2, electronics.getID().toString());
    }
}
