package server.command.ItemFactory;


import controller.ItemController.VehicleItemController;
import models.Item;
import server.ClientSession;

public class VehicleCreatorCommand implements ItemCreator {

    @Override
    public void execute(ClientSession clientSession, String[] args) {
        // args = [CREATE_ITEM, VEHICLE, name, price]
        try {
            String name = args[2];
            long price = Long.parseLong(args[3]);
            String condition = "Đang chờ bán"; // Tự động điền

            // Gọi Controller chuyên biệt cho Vehicle
            Item newVehicleItem = VehicleItemController.createItem(clientSession.getCurrentUser(), name, price, condition);
            VehicleItemController.saveItem(newVehicleItem);

            clientSession.sendMessage("SUCCESS|Đã tạo phương tiện thành công!");

        } catch (Exception e) {
            clientSession.sendMessage("ERROR|Dữ liệu phương tiện không hợp lệ!");
        }
    }
}