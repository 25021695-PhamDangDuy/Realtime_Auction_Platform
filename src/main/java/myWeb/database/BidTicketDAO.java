package myWeb.database;

import myWeb.models.BidTicket;

import java.util.List;
import java.util.UUID;

public class BidTicketDAO implements DataAccessObject<BidTicket> {

    @Override
    public void update(BidTicket bidTicket) {

    }

    @Override
    public void save(BidTicket bidTicket) {

    }

    @Override
    public BidTicket get(UUID ID) {
        return null;
    }

    @Override
    public List<BidTicket> getAll() {
        return List.of();
    }
}
