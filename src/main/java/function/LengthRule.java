package function;

public class LengthRule implements StrongRule{
    private int lenght;

    public LengthRule(int n){lenght = n;}
    public LengthRule(Const rule){lenght = rule.getNumber();}

    @Override
    public boolean validate(String item) {
        return item.length() >= lenght;
    }


}
