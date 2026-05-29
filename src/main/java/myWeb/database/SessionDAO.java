package myWeb.database;

import myWeb.models.AuctionSession;
import myWeb.models.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SessionDAO implements DataAccessObject<AuctionSession>{
    public Set<User> getObsever(AuctionSession session){

    }

    @Override
    public void update(AuctionSession session) {

    }

    @Override
    public void save(AuctionSession session) {

    }

    @Override
    public AuctionSession get(UUID ID) {
        return null;
    }

    @Override
    public List<AuctionSession> getAll() {
        return List.of();
    }
}
