package controller.ItemController;

import models.Art;

import models.Art;
import models.Item;
import models.User;

public class ArtItemController {

    public static Item createItem(User owner, String name, long price, String condition, String author, String material) {
        return new Art(owner, name, price, condition, author, material);
    }

    public static void saveItem(Item item) {
        // Logic lưu vào Database sau này viết ở đây
    }
}
