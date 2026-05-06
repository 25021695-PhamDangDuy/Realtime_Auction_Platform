package myWeb.function;

public class LengthRule implements StrongRule{
    private int lenght;

    public LengthRule(int n){lenght = n;}

    @Override
    public boolean validate(String item) {
        return item.length() >= lenght;
    }


}
