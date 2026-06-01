package server.command.ItemFactory;


import models.User;
import server.ClientSession;

import java.util.UUID;

public interface ItemCreator {
    // Nhận vào ID người bán và cái mảng chữ khách gửi lên,
    // trả về chuỗi SUCCESS/ERROR sau khi Controller xử lý xong.
    void execute(ClientSession clientSession, String[] args);
}