# Realtime_Auction_Platform - Hệ thống đấu giá trực tuyến theo thời gian thực

## 1.Mô tả bài toán và phạm vi hệ thống
* **Bài toán:** Xây dựng một hệ thống đấu giá trực tuyến dựa trên kiến trúc Server-Client. Hệ thống cho phép nhiều Client (người dùng) kết nối đồng thời đến Server để tham gia vào các phòng đấu giá sản phẩm, thực hiện trả giá (bid) theo thời gian thực, cập nhật giá cao nhất hiện tại và tự động đóng phiên khi hết giờ.
* **Phạm vi hệ thống:**
    * **Phía Server:** * Quản lý danh sách các sản phẩm đang đấu giá và thời gian đếm ngược.
        * Xử lý kết nối đồng thời từ nhiều Client (Sử dụng Multi-threading hoặc Asynchronous I/O).
        * Tiếp nhận thông tin trả giá, kiểm tra tính hợp lệ (giá mới phải cao hơn giá hiện tại + bước giá tối thiểu).
        * Phát tán (Broadcast) giá mới nhất hoặc thông báo kết thúc phiên đến tất cả Client đang tham gia phòng.
    * **Phía Client:**
        * Kết nối đến Server qua IP và Port.
        * Hiển thị danh sách sản phẩm, thông tin phiên đấu giá hiện tại (tên sản phẩm, giá khởi điểm, giá cao nhất, thời gian còn lại).
        * Gửi lệnh trả giá lên Server. Nhận thông báo thời gian thực khi có người khác trả giá cao hơn hoặc khi phiên đấu giá kết thúc.

## 2. Công nghệ sử dụng và yêu cầu cài đặt
* **Ngôn ngữ lập trình:** Java (JDK 25) 
* **Giao thức mạng:** TCP/IP (Socket Programming) để đảm bảo tính toàn vẹn dữ liệu và đúng thứ tự của các lượt trả giá.
* **Định dạng dữ liệu:** JSON hoặc chuỗi ký tự được chuẩn hóa để truyền tải thông tin giữa Server và Client.
* **Yêu cầu môi trường:**
    * Hệ điều hành hỗ trợ: Windows, macOS, Linux (Ubuntu/Debian/Fedora...).
    * Đã cài đặt trình thông dịch/biên dịch tương ứng (ví dụ: `python`, `java`, hoặc `node`) và đã cấu hình biến môi trường (Environment Variables) để có thể gọi trực tiếp từ Terminal.

## 3. Cấu trúc thư mục dự án
```text
Realtime_Auction_Platform/
├── database/                        # Thư mục lưu trữ cơ sở dữ liệu vật lý
│   ├── createTables.sql             # Kịch bản SQL khởi tạo các bảng dữ liệu
│   └── RAP.sqlite                   # File cơ sở dữ liệu SQLite của ứng dụng
├── src/main/
│   ├── java/                        # Mã nguồn gói Java chính
│   │   ├── Application/             # Khởi tạo nền tảng cấu hình hệ thống
│   │   ├── controller/              # Tầng điều khiển (Business Logic Layer)
│   │   │   ├── brain/               # Quản lý cốt lõi (Account, Auction, Payment, Wallet)
│   │   │   ├── ItemService/         # Nghiệp vụ xử lý các dịch vụ vật phẩm
│   │   │   ├── AuctionObserver.java # Mẫu thiết kế Observer đồng bộ trạng thái
│   │   │   └── *Executor.java       # Thực thi giao dịch (Deposit, Settlement, Transfer, Withdraw)
│   │   ├── database/                # Tầng truy xuất dữ liệu (Data Access Layer - DAO)
│   │   │   ├── items/               # Thao tác dữ liệu liên quan đến vật phẩm
│   │   │   ├── *DAO.java            # Các lớp Interface định nghĩa cổng thực thi dữ liệu
│   │   │   ├── *DAOImpl.java        # Hiện thực hóa các câu lệnh truy vấn SQL cụ thể
│   │   │   └── DatabaseCreator.java # Tự động tạo lập cấu trúc database từ code
│   │   ├── exception/               # Định nghĩa các Custom Exception của hệ thống
│   │   ├── function/                # Lớp tiện ích (Validators, Checkers, Enums trạng thái)
│   │   │   ├── BidStatus / ItemStatus / TransactionStatus # Các Enum định nghĩa trạng thái
│   │   │   └── *Validator.java      # Kiểm tra tính hợp lệ dữ liệu (Password, UserName)
│   │   ├── models/                  # Tầng thực thể (Entity Models)
│   │   │   ├── User / Bidder / Seller / Admin   # Phân cấp phân quyền thực thể người dùng
│   │   │   ├── Item / Art / Electronics / Vehicle # Phân cấp phân loại sản phẩm đấu giá
│   │   │   ├── AuctionSession       # Thông tin phiên đấu giá công khai
│   │   │   └── Transaction* # Các mô hình dữ liệu lịch sử giao dịch số dư
│   │   ├── server/                  # Module Backend Server (Socket xử lý kết nối)
│   │   │   ├── command/             # Triển khai Command Pattern bóc tách gói tin mạng
│   │   │   │   ├── ItemFactory      # Factory Pattern khởi tạo nhanh phân loại vật phẩm
│   │   │   │   ├── Command.java     # Interface nền tảng cho mọi yêu cầu gửi lên
│   │   │   │   └── *Command.java    # Các tập lệnh xử lý cụ thể (Login, Register, Bid, JoinRoom...)
│   │   │   ├── AuctionServer.java   # Khởi chạy Socket Server lắng nghe kết nối TCP
│   │   │   ├── ClientHandler.java   # Tiếp nhận và duy trì luồng giao tiếp với từng Client riêng biệt
│   │   │   ├── ClientManager.java   # Quản lý danh sách các Client đang trực tuyến (Online)
│   │   │   └── GsonUtil.java        # Công cụ mã hóa/giải mã dữ liệu JSON thông qua thư viện Gson
│   │   └── view/                    # Module Frontend Client (Giao diện JavaFX)
│   │       ├── network/             # Quản lý kết nối Socket phía Client (ServerConnection)
│   │       ├── AuctionLogin.java    # Giao diện Đăng nhập hệ thống
│   │       ├── AuctionRegister.java # Giao diện Đăng ký tài khoản mới
│   │       ├── UserDashboardScreen.java # Bảng điều khiển trung tâm người dùng
│   │       ├── AuctionHomeScreen.java   # Màn hình trang chủ danh sách sản phẩm đấu giá
│   │       └── AuctionRoom.java     # Phòng đấu giá chi tiết thời gian thực
│   └── resources/                   # Lưu trữ tài nguyên tĩnh (.fxml, .css, hình ảnh)
├── pom.xml                          # Quản lý các thư viện phụ thuộc (Dependencies) của Maven
├── RAP2.sqlite                      # File cơ sở dữ liệu SQLite dự phòng / kết quả test độc lập
└── README.md                        # Hướng dẫn chi tiết hệ thống này
```
## 4. Hướng dẫn chạy thư mục đa nền tảng.
Hệ thống sử dụng các câu lệnh tiêu chuẩn của Unix.
* **Trên Windows:** Khuyến khích sử dụng **Git Bash** để đồng bộ terminal.
* **Trên macOS/Linux:** Sử dụng ứng dụng **Terminal** có sẵn.
* ⚠️ **LƯU Ý:** Bạn phải khởi động **Server trước**, sau đó mới khởi động một hoặc nhiều **Client**.
### Cách chạy thư mục: 
* **Bước 1: Khởi động Server:** Mở Git Bash (Windows) hoặc Terminal (macOS/Linux), di chuyển vào thư mục `server` và chạy lệnh:
```bash
cd server
java Main
````

* **Bước 2: Khởi động Client:** tiếp tục chạy câu lệnh:
```
cd client
java Main
```
## 5. Các chức năng đã hoàn thành
- [x] Khởi tạo Server xử lý đa luồng, chấp nhận nhiều Client kết nối cùng lúc mà không bị nghẽn hệ thống.

- [x] Client đăng nhập vào hệ thống với một Username duy nhất để định danh khi trả giá.

- [x] Hiển thị danh sách và thông tin sản phẩm đấu giá theo thời gian thực (Real-time).

- [x] Chức năng Trả giá (Bid): Server kiểm tra điều kiện hợp lệ và cập nhật người giữ giá cao nhất.

- [x] Chức năng Đếm ngược thời gian (Countdown) và tự động đóng phiên đấu giá khi hết giờ.

- [x] Broadcast thông báo: Tự động gửi thông tin cập nhật giá mới hoặc thông báo người thắng cuộc đến tất cả các Client đang kết nối.

- [x] Tự động gia hạn thêm 30 giây nếu có người trả giá vào những giây cuối cùng (Anti-sniping).
## 6.Link video chạy thử và báo cáo pdf
- **Video chạy thử:** 

https://github.com/user-attachments/assets/a37117b8-072a-4d12-a65a-931b7e1d8658


- **Báo cáo: [Báo cáo thực hành xây dựng hệ thống đấu giá.docx.pdf](https://github.com/user-attachments/files/28561504/Bao.cao.th.c.hanh.xay.d.ng.h.th.ng.d.u.gia.docx.pdf)






