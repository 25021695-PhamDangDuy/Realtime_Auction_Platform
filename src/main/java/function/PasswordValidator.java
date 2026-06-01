package function;

public class PasswordValidator implements Validator<String>{
    private Const rule = Const.LENGHTPassWord;
    private DiversityRule diversityRule = new DiversityRule();
    private LengthRule lengthRule = new LengthRule(rule.getNumber());

    @Override
    public boolean valid(String pw) {
        if(!diversityRule.validate(pw)){
            return false;
        }
        if(!lengthRule.validate(pw)){
            return false;
        }

        return true;
    }

    public boolean checkEquals(String pw, String pw2){
        if(pw.equals(pw2)){
            return true;
        }else {
            return false;
        }
    }
}
