public class Bidder implement DauGia,ThaIcon,TimKiemPhienDauGia{
  private double soDuTaiKhoan;
  private String name;
  public Bidder(String name,double soDuTaiKhoan){
    this.name=name;
    this.soDuTaiKhoan=soDuTaiKhoan;
  }
  public void setName(String newName){
    name=newName;
  }
  public void napTien(double tien){
    soDuTaiKhoan+=tien;
  }
}


public class Seller entends Bidder implement taoDsHang,ThietLapthoigian,Ketthucphien{
  //tạo danh sách tổng vì không biết người ta tạo bao nhiêu.
  protected List<List<String>> allLists = new ArrayList<>();
  public Seller(String n,double tien){
    super(n,tien);
  }
}

public class Admin extends Seller implement QuyenHanCao{
   public Admin(String n,double tien){
     super(n,tien);
   } 
   public naangcaptaikhoan(Bidder b){
     Seller s=(Seller s);
   }
   puclic nangcaplenadmin(Seller s){
     Admin a=(Admin) s;
   }  
}

/* xếp extend dần xuống để lớp trên không truy cập được interface của lớp dưới, đồng thời hỗ trợ xây dựng hệ thống nâng cấp tài khoản*/
