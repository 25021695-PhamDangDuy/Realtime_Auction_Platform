package function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DiversityRuleTest {
    DiversityRule diversityRule = new DiversityRule();


    // Kiểm thử phân hoạch tương đương
    @Test
    @DisplayName("Kiểm tra số")
    void numberTest() {
        String password = "asBdbwbas";
        String password2 = "10a0d0asA";
        assertFalse(diversityRule.validate(password), "Test: MK phai co it nhat 1 so");
        assertTrue(diversityRule.validate(password2), "Test: MK nay co so roi");
    }

    @Test
    @DisplayName("Kiểm tra chữ in thường")
    void lowerTest() {
        String pw = "0912093DAODI";
        String pw2 = "0910290DAISs";

        assertFalse(diversityRule.validate(pw), "Test: MK phai co it nhat 1 chu thuong");
        assertTrue(diversityRule.validate(pw2), "Test:MK nay da co chu thuong roi");
    }

    @Test
    @DisplayName("Kiểm tra chữ in thường")
    void upperTest() {
        String pw = "0912093dasdw";
        String pw2 = "0910290sadasD";

        assertFalse(diversityRule.validate(pw), "Test: MK phai co it nhat 1 chu hoa");
        assertTrue(diversityRule.validate(pw2), "Test:MK nay da co chu hoa roi");

    }
}
