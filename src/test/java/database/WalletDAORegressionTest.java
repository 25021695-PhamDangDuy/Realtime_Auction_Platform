package database;

import models.Wallet;
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

@DisplayName("WalletDAO regression tests")
class WalletDAORegressionTest {
    private WalletDAO walletDAO;

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
        walletDAO = new WalletDAO();
        walletDAO.databaseCreator = databaseCreator;
    }

    @Test
    @DisplayName("getByOwnerID() should map wallet row")
    void getByOwnerIdShouldMapWalletRow() throws SQLException {
        UUID walletId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        when(databaseCreator.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("ID")).thenReturn(walletId.toString());
        when(resultSet.getString("owner_ID")).thenReturn(ownerId.toString());
        when(resultSet.getLong("Balance")).thenReturn(1_000L);
        when(resultSet.getLong("BalanceLocked")).thenReturn(200L);

        Wallet wallet = walletDAO.getByOwnerID(ownerId);

        assertNotNull(wallet);
        assertEquals(walletId, wallet.getID());
        assertEquals(ownerId, wallet.getOwnerID());
        assertEquals(1_000L, wallet.getBalance());
        assertEquals(200L, wallet.getBalanceLocked());
        verify(preparedStatement).setString(1, ownerId.toString());
    }

    @Test
    @DisplayName("isHasOwnerID() should check wallets.owner_ID, not users.ID")
    void isHasOwnerIdShouldCheckWalletOwnerId() throws SQLException {
        UUID ownerId = UUID.randomUUID();
        when(databaseCreator.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBoolean(1)).thenReturn(true);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        assertTrue(walletDAO.isHasOwnerID(ownerId));

        verify(connection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("wallets"), "Should check wallets table");
        assertTrue(sqlCaptor.getValue().contains("owner_ID"), "Should check owner_ID column");
    }
}
