package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import models.*;

import view.network.RuntimeTypeAdapterFactory;

public class GsonUtil {

    // Tạo sẵn 1 cái máy GSON dùng chung cho cả Client và Server
    public static final Gson gson = createCustomGson();

    private static Gson createCustomGson() {

        // Cấu hình máy dán nhãn cho Item
        RuntimeTypeAdapterFactory<Item> itemFactory = RuntimeTypeAdapterFactory.of(Item.class, "type")
                .registerSubtype(Art.class, "ART")
                .registerSubtype(Vehicle.class, "VEHICLE")
                .registerSubtype(Electronics.class, "ELECTRONIC");

        RuntimeTypeAdapterFactory<User> userFactory = RuntimeTypeAdapterFactory.of(User.class, "role")
                .registerSubtype(Bidder.class, "BIDDER")
                .registerSubtype(Seller.class, "SELLER")
                .registerSubtype(Admin.class, "ADMIN");

        // Gắn máy dán nhãn vào GSON
        return new GsonBuilder()
                .registerTypeAdapterFactory(itemFactory)
                .registerTypeAdapterFactory(userFactory)
                .create();
    }
}
