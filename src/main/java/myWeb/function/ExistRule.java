package myWeb.function;
import java.util.Set;


public class ExistRule implements StrongRule{
    private Set<String> existList;

    public ExistRule(Set<String> tList){
        existList = tList;
    }
    @Override
    public boolean validate(String item) {
        if(existList.contains(item)){
            return false;
        }else {return true;}
    }
}
