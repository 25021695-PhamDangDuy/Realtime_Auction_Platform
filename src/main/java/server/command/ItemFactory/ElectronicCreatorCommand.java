package server.command.ItemFactory;


import controller.ItemController.ElectronicItemController;
import models.Item;
import models.User;
import server.ClientSession;
// import myWeb.controller.ElectronicController;

public class ElectronicCreatorCommand implements ItemCreator {
    @Override
    public void execute(ClientSession clientSession, String[] args) {
        // args = [CREATE_ITEM, ELECTRONIC, name, long price, Interger monthOfWarranty]
        try {
            String name = args[2];
            long price = Long.parseLong(args[3]);
            String condition = "Đang chờ ban";
            Integer month = Integer.valueOf(args[4]);

            // Gọi Controller chuyên biệt cho Đồ Điện Tử
            Item newElectronicItem = ElectronicItemController.createItem(clientSession.getCurrentUser(), name, price, condition, month);
            ElectronicItemController.saveItem(newElectronicItem);
            //gửi thông báo về
            clientSession.sendMessage("SUCCESS|Đã tạo sảm phẩm thành công!");
        } catch (Exception e) {
            clientSession.sendMessage("ERROR|Dữ liệu sản phẩm không hợp lệ!");
        }

    }}