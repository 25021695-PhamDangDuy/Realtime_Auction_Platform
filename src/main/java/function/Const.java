package function;

// Tập các hằng số để các class cần thì gọi để sử dụng, mục đích -> giúp dễ mở rộng Code và logic sau này
public enum Const {
    LENGHTPassWord(8),
    LENGHTName(3);

    private int number;
    private Const(int n){this.number = n;}

    public int getNumber(){return this.number;}
}
