package server.command.ItemFactory;


import controller.ItemService.ArtItemController;
import models.Item;
import server.ClientSession;

public class ArtCreatorCommand implements ItemCreator {

    @Override
    public void execute(ClientSession clientSession, String[] args) {
        // args = [CREATE_ITEM, ART, name, price, author, material]
        try {
            String name = args[2];
            long price = Long.parseLong(args[3]);
            String condition = "Đang chờ bán"; // Tự động điền

            // Thuộc tính riêng của Art
            String author = args[4];
            String material = args[5];

            // Gọi Controller chuyên biệt cho Art
            Item newArtItem = ArtItemController.createItem(clientSession.getCurrentUser(), name, price, condition, author, material);
            ArtItemController.saveItem(newArtItem);

            // Cần gửi tin nhắn báo thành công về cho Client
            clientSession.sendMessage("SUCCESS_CREATE|Đã tạo tác phẩm nghệ thuật thành công!");

        } catch (Exception e) {
             clientSession.sendMessage("ERROR|Dữ liệu tác phẩm nghệ thuật không hợp lệ!");
        }
    }
}