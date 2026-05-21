package myWeb.function;

import java.util.HashMap;

public class DiversityRule implements StrongRule{
    private  HashMap<String,Boolean> rule;

    public DiversityRule(){
        rule = new HashMap<>();
        rule.put("InHoa", false);
        rule.put("InThuong",false);
        rule.put("So",false);
    }

    @Override
    public boolean validate(String item) {
        char[] characters = item.toCharArray();

        for(char c : characters){
            if(Character.isLowerCase(c)){
                rule.replace("InThuong",true);
            } else if (Character.isUpperCase(c)) {
                rule.replace("InHoa",true);
            } else if (Character.isDigit(c)) {
                rule.replace("So",true);
            }
        }

        if(rule.containsValue(false)){
            return false;
        }else {return true;}

    }
}
