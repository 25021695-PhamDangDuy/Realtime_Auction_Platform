package service.brain;

import database.BidTicketDAO;
import database.SessionDAO;
import database.items.getItemDao;
import function.SessionStatus;
import models.AuctionSession;
import models.Bidder;
import models.Seller;
import models.SettlementTransaction;
import models.Vehicle;
import models.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Auction lifecycle simulation through controllers")
class AuctionLifecycleSimulationTest {
    private AuctionManager auctionManager;
    private PaymentManager paymentManager;
    private WalletManager walletManager;
    private SessionDAO sessionDAO;
    private getItemDao itemDAO;

    private Seller seller;
    private Bidder bidder;
    private Vehicle item;

    @BeforeEach
    void setUp() throws Exception {
        auctionManager = AuctionManager.getInstance();
        paymentManager = spy(PaymentManager.getInstance());
        walletManager = mock(WalletManager.class);
        sessionDAO = mock(SessionDAO.class);
        itemDAO = mock(getItemDao.class);

        inject(auctionManager, "sessionDAO", sessionDAO);
        inject(auctionManager, "itemDAO", itemDAO);
        inject(auctionManager, "walletManager", walletManager);
        inject(auctionManager, "paymentManager", paymentManager);
        inject(paymentManager, "walletManager", walletManager);

        seller = new Seller("seller_lifecycle", "secret");
        bidder = new Bidder("bidder_lifecycle", "secret");
        item = new Vehicle(seller, "Lifecycle Vehicle", 1_000L, "New");

        attachWallet(seller, 0L);
        attachWallet(bidder, 10_000L);
    }

    @Test
    @DisplayName("should open session, accept bid, then expose settlement blocker on finish")
    void shouldSimulateAuctionUntilSettlementBlocker() throws Exception {
        auctionManager.createSession(
                item,
                seller,
                1_000L,
                100L,
                LocalDateTime.now().plusMinutes(10)
        );

        ArgumentCaptor<AuctionSession> sessionCaptor = ArgumentCaptor.forClass(AuctionSession.class);
        verify(sessionDAO).save(sessionCaptor.capture());
        AuctionSession session = sessionCaptor.getValue();

        assertEquals(SessionStatus.RUNNING, session.getStatus());
        assertEquals(1_000L, session.getCurrentPrice());
        inject(session, "bidTicketDAO", mock(BidTicketDAO.class));

        auctionManager.placeBid(session, bidder, 1_200L);

        assertEquals(1_200L, session.getCurrentPrice());
        assertNotNull(session.getTopBid());
        assertEquals(bidder.getID(), session.getTopBidder().getID());
        verify(walletManager).lockMoney(bidder.getWalletID(), bidder.getID(), 1_200L);
        verify(sessionDAO, atLeastOnce()).update(session);

        forceEndTimeInPast(session);
        SettlementTransaction settlementTransaction = new SettlementTransaction(session);
        doReturn(settlementTransaction)
                .when(paymentManager)
                .createTransaction(eq(SettlementTransaction.class), any());

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> auctionManager.finishSession(session)
        );

        assertTrue(
                thrown.getMessage().contains("Transaction execution failed")
                        || thrown.getMessage().contains("Không tìm thấy strategy"),
                "Current controller flow should expose PaymentManager settlement execution blocker"
        );
    }

    private static void attachWallet(Bidder user, long balance) throws Exception {
        Wallet wallet = new Wallet(UUID.randomUUID(), user.getID(), balance, 0L);
        Field walletField = Bidder.class.getDeclaredField("wallet");
        walletField.setAccessible(true);
        walletField.set(user, wallet);
    }

    private static void forceEndTimeInPast(AuctionSession session) throws Exception {
        Field endTime = AuctionSession.class.getDeclaredField("endTime");
        endTime.setAccessible(true);
        endTime.set(session, LocalDateTime.now().minusSeconds(1));
    }

    private static void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
