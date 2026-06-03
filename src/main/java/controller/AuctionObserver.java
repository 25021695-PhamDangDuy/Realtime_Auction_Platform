package controller;

import java.util.UUID;

public interface AuctionObserver {
    void update(String message);

    UUID getID();
}
