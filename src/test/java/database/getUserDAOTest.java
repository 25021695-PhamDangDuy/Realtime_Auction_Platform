package database;

import models.User;
import models.Bidder;
import models.Seller;
import models.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Test getUserDAO.getbyUsername()")
public class getUserDAOTest {

    private getUserDAO userDAO;

    @Mock
    private DatabaseCreator mockDatabaseCreator;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private UUID testUserId;
    private final String testUsername = "testuser";
    private final String testPassword = "password123";
    private final String testRole = "BIDDER";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userDAO = new getUserDAO();
        userDAO.databaseCreator = mockDatabaseCreator;
        testUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should return User when username exists in database")
    public void testGetbyUsername_UserFound() throws SQLException {
        // Arrange
        when(mockDatabaseCreator.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("ID")).thenReturn(testUserId.toString());
        when(mockResultSet.getString("Username")).thenReturn(testUsername);
        when(mockResultSet.getString("Password")).thenReturn(testPassword);
        when(mockResultSet.getString("role")).thenReturn(testRole);

        // Act
        User result = userDAO.getbyUsername(testUsername);

        // Assert
        assertNotNull(result, "User should not be null");
        assertEquals(testUserId, result.getID(), "User ID should match");
        assertEquals(testUsername, result.getName(), "Username should match");
        assertEquals(testPassword, result.getPassword(), "Password should match");

        // Verify
        verify(mockDatabaseCreator).getConnection();
        verify(mockPreparedStatement).setString(1, testUsername);
        verify(mockPreparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Should return Bidder when role is BIDDER")
    public void testGetbyUsername_ReturnsBidder() throws SQLException {
        // Arrange
        when(mockDatabaseCreator.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("ID")).thenReturn(testUserId.toString());
        when(mockResultSet.getString("Username")).thenReturn(testUsername);
        when(mockResultSet.getString("Password")).thenReturn(testPassword);
        when(mockResultSet.getString("role")).thenReturn("BIDDER");

        // Act
        User result = userDAO.getbyUsername(testUsername);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Bidder.class, result, "Should return Bidder instance");
    }

    @Test
    @DisplayName("Should return Seller when role is SELLER")
    public void testGetbyUsername_ReturnsSeller() throws SQLException {
        // Arrange
        when(mockDatabaseCreator.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("ID")).thenReturn(testUserId.toString());
        when(mockResultSet.getString("Username")).thenReturn(testUsername);
        when(mockResultSet.getString("Password")).thenReturn(testPassword);
        when(mockResultSet.getString("role")).thenReturn("SELLER");

        // Act
        User result = userDAO.getbyUsername(testUsername);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Seller.class, result, "Should return Seller instance");
    }

    @Test
    @DisplayName("Should return Admin when role is ADMIN")
    public void testGetbyUsername_ReturnsAdmin() throws SQLException {
        // Arrange
        when(mockDatabaseCreator.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("ID")).thenReturn(testUserId.toString());
        when(mockResultSet.getString("Username")).thenReturn(testUsername);
        when(mockResultSet.getString("Password")).thenReturn(testPassword);
        when(mockResultSet.getString("role")).thenReturn("ADMIN");

        // Act
        User result = userDAO.getbyUsername(testUsername);

        // Assert
        assertNotNull(result);
        assertInstanceOf(Admin.class, result, "Should return Admin instance");
    }

    @Test
    @DisplayName("Should throw SQLException when database connection fails")
    public void testGetbyUsername_DatabaseConnectionFails() throws SQLException {
        // Arrange
        when(mockDatabaseCreator.getConnection()).thenThrow(new SQLException("Connection failed"));

        // Act & Assert
        assertThrows(SQLException.class, () -> userDAO.getbyUsername(testUsername),
                "Should throw SQLException when connection fails");
    }

    @Test
    @DisplayName("Should throw SQLException when query fails")
    public void testGetbyUsername_QueryExecutionFails() throws SQLException {
        // Arrange
        when(mockDatabaseCreator.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        // Act & Assert
        assertThrows(SQLException.class, () -> userDAO.getbyUsername(testUsername),
                "Should throw SQLException when query execution fails");
    }

    @Test
    @DisplayName("Should handle null username input")
    public void testGetbyUsername_NullUsername() throws SQLException {
        // Arrange
        when(mockDatabaseCreator.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // Act
        User result = userDAO.getbyUsername(null);

        // Assert
        assertNull(result, "Should return null when user not found");
    }

}