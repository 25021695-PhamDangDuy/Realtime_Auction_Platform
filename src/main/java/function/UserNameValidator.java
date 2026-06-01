package function;

public class UserNameValidator implements Validator<String>{
    private Const rule = Const.LENGHTName;
    private LengthRule lengthRule = new LengthRule(rule.getNumber());

    @Override
    public boolean valid(String s) {


        if(lengthRule.validate(s)){
            return true;
        }else {
            return false;
        }
    }
}
