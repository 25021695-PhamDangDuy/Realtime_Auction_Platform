package database;

import models.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SellerDAOImpl regression tests")
class SellerDAOImplRegressionTest {
    private SellerDAOImpl sellerDAO;

    @Mock
    private DatabaseCreator databaseCreator;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;

    private UUID sellerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sellerDAO = new SellerDAOImpl();
        sellerDAO.databaseCreator = databaseCreator;
        sellerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("get() should bind seller ID and read Username column")
    void getShouldBindSellerIdAndReadUsername() throws SQLException {
        when(databaseCreator.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("ID")).thenReturn(sellerId.toString());
        when(resultSet.getString("Username")).thenReturn("seller_one");
        when(resultSet.getString("Password")).thenReturn("secret");

        Seller seller = sellerDAO.get(sellerId);

        assertNotNull(seller);
        assertEquals(sellerId, seller.getID());
        assertEquals("seller_one", seller.getName());
        verify(preparedStatement).setString(1, sellerId.toString());
        verify(resultSet, never()).getString("Name");
    }

    @Test
    @DisplayName("getAll() should read Username column, not missing Name column")
    void getAllShouldReadUsernameColumn() throws SQLException {
        when(databaseCreator.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("ID")).thenReturn(sellerId.toString());
        when(resultSet.getString("Username")).thenReturn("seller_all");
        when(resultSet.getString("Password")).thenReturn("secret");
        when(resultSet.getString("Name")).thenThrow(new SQLException("no such column: Name"));

        List<Seller> sellers = sellerDAO.getAll();

        assertEquals(1, sellers.size());
        assertEquals("seller_all", sellers.get(0).getName());
        verify(resultSet, never()).getString("Name");
    }
}
