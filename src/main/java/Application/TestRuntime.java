package Application;

import java.util.ArrayList;
import java.util.List;

public class TestRuntime {

    public static void main(String[] args) {
        String msg = "LOGIN||07092007Isi";
        String[] out = msg.strip().split("\\|");
        List<String> print = List.of(out);

        print.forEach(s -> System.out.println(s.trim()));
    }
}
