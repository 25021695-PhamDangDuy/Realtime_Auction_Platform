package server.command;


import server.ClientSession;
import server.Role;
import server.command.ItemFactory.ArtCreatorCommand;
import server.command.ItemFactory.ElectronicCreatorCommand;
import server.command.ItemFactory.ItemCreator;
import server.command.ItemFactory.VehicleCreatorCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CreateItemCommand implements Command {
    public Set<Role> getAllowedRoles() {
        return Set.of(Role.SELLER);
    }

    // Đây là "Cuốn sổ danh bạ" của các Xưởng sản xuất
    private static final Map<String, ItemCreator> creatorRegistry = new HashMap<>();

    // Đăng ký các xưởng vào danh bạ khi Server vừa bật lên
    static {
        creatorRegistry.put("ELECTRONIC", new ElectronicCreatorCommand());
        creatorRegistry.put("ART", new ArtCreatorCommand());
        creatorRegistry.put("VEHICLE", new VehicleCreatorCommand());
    }

    @Override
    public void execute(ClientSession session, String[] args) {
        // Cú pháp chuẩn khách gửi: CREATE_ITEM | LOẠI_HÀNG | Tên | Giá | ...
        // Nên mảng args sẽ là:
        // args[0] = "CREATE_ITEM"
        // args[1] = "ART" hoặc "VEHICLE" hoặc "ELECTRONIC"

        if (args.length < 2) {
            // Nhớ mở comment hàm sendMessage nếu file ClientSession của bạn có hàm này nhé
            session.sendMessage("ERROR|Cú pháp sai. Thiếu loại sản phẩm!");
            return;
        }

        // Lấy từ khóa loại hàng và viết hoa hết lên cho chắc cú (tránh lỗi gõ phím art, Art)
        String itemType = args[1].toUpperCase();

        // Tra danh bạ xem có xưởng nào nhận làm loại hàng này không
        ItemCreator creator = creatorRegistry.get(itemType);

        if (creator == null) {
            session.sendMessage("ERROR|Hệ thống không hỗ trợ loại sản phẩm: " + itemType);
            return;
        }

        // TÌM THẤY RỒI! Ủy quyền toàn bộ mảng chữ cho Xưởng đó tự xử lý tiếp
        creator.execute(session, args);
    }
}
