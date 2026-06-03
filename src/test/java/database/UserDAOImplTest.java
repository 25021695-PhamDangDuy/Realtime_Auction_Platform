package database;

import models.Admin;
import models.Bidder;
import models.Seller;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;


import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test UserDAOImpl.createUserByRole()")
public class UserDAOImplTest {

    private TestUserDAO testUserDAO;
    private UUID testId;
    private final String testUsername = "testuser";
    private final String testPassword = "password123";

    // Inner class để test abstract method
    private static class TestUserDAO extends UserDAOImpl<User> {
        @Override
        public User get(UUID ID) throws SQLException {
            return null;
        }

        @Override
        public java.util.List<User> getAll() {
            return java.util.List.of();
        }
    }

    @BeforeEach
    public void setUp() {
        testUserDAO = new TestUserDAO();
        testId = UUID.randomUUID();
    }

    // ============ TEST ROLE BIDDER ============

    @Test
    @DisplayName("Should create Bidder when role is 'BIDDER'")
    public void testCreateUserByRole_Bidder() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "BIDDER");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Bidder.class, result, "Should return Bidder instance");
        assertEquals(testId, result.getID(), "User ID should match");
        assertEquals(testUsername, result.getName(), "Username should match");
        assertEquals(testPassword, result.getPassword(), "Password should match");
    }

    @Test
    @DisplayName("Should create Bidder when role is lowercase 'bidder'")
    public void testCreateUserByRole_BidderLowercase() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "bidder");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Bidder.class, result, "Should return Bidder instance");
    }

    @Test
    @DisplayName("Should create Bidder when role has extra whitespace")
    public void testCreateUserByRole_BidderWithWhitespace() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "  BIDDER  ");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Bidder.class, result, "Should return Bidder instance");
    }

    // ============ TEST ROLE SELLER ============

    @Test
    @DisplayName("Should create Seller when role is 'SELLER'")
    public void testCreateUserByRole_Seller() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "SELLER");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Seller.class, result, "Should return Seller instance");
        assertEquals(testId, result.getID(), "User ID should match");
        assertEquals(testUsername, result.getName(), "Username should match");
        assertEquals(testPassword, result.getPassword(), "Password should match");
    }

    @Test
    @DisplayName("Should create Seller when role is lowercase 'seller'")
    public void testCreateUserByRole_SellerLowercase() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "seller");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Seller.class, result, "Should return Seller instance");
    }

    @Test
    @DisplayName("Should create Seller when role has extra whitespace")
    public void testCreateUserByRole_SellerWithWhitespace() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "  SELLER  ");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Seller.class, result, "Should return Seller instance");
    }

    // ============ TEST ROLE ADMIN ============

    @Test
    @DisplayName("Should create Admin when role is 'ADMIN'")
    public void testCreateUserByRole_Admin() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "ADMIN");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Admin.class, result, "Should return Admin instance");
        assertEquals(testId, result.getID(), "User ID should match");
        assertEquals(testUsername, result.getName(), "Username should match");
        assertEquals(testPassword, result.getPassword(), "Password should match");
    }

    @Test
    @DisplayName("Should create Admin when role is lowercase 'admin'")
    public void testCreateUserByRole_AdminLowercase() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "admin");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Admin.class, result, "Should return Admin instance");
    }

    @Test
    @DisplayName("Should create Admin when role has extra whitespace")
    public void testCreateUserByRole_AdminWithWhitespace() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "  ADMIN  ");

        // Assert
        assertNotNull(result, "User should not be null");
        assertInstanceOf(Admin.class, result, "Should return Admin instance");
    }

    // ============ TEST INVALID ROLES ============

    @Test
    @DisplayName("Should return null when role is invalid")
    public void testCreateUserByRole_InvalidRole() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "INVALID_ROLE");

        // Assert
        assertNull(result, "Should return null for invalid role");
    }

    @Test
    @DisplayName("Should return null when role is empty string")
    public void testCreateUserByRole_EmptyRole() throws SQLException {
        // Act
        User result = testUserDAO.createUserByRole(testId, testUsername, testPassword, "");

        // Assert
        assertNull(result, "Should return null for empty role");
    }

    @Test
    @DisplayName("Should return null when role is null")
    public void testCreateUserByRole_NullRole() {
        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                        testUserDAO.createUserByRole(testId, testUsername, testPassword, null),
                "Should throw NullPointerException when role is null");
    }

}