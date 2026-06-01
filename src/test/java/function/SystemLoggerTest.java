//package function;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import java.io.ByteArrayOutputStream;
//import java.io.PrintStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("Test Suite cho SystemLogger")
//public class SystemLoggerTest {
//
//    private SystemLogger logger;
//    private ByteArrayOutputStream outputStream;
//    private PrintStream originalOut;
//
//    @BeforeEach
//    void setUp() {
//        // Khởi tạo logger
//        logger = SystemLogger.getInstance();
//
//        // Capture System.out để kiểm tra output
//        originalOut = System.out;
//        outputStream = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outputStream));
//
//        // Reset lại mức độ log mặc định
//        logger.setMinimumLevel(SystemLogger.LogLevel.INFO);
//    }
//
//    // ==================== Test Singleton ====================
//    @Test
//    @DisplayName("Test Singleton - getInstance trả về cùng một instance")
//    void testSingletonInstance() {
//        SystemLogger logger1 = SystemLogger.getInstance();
//        SystemLogger logger2 = SystemLogger.getInstance();
//
//        assertSame(logger1, logger2, "getInstance phải trả về cùng một instance");
//    }
//
//    // ==================== Test Info Level ====================
//    @Test
//    @DisplayName("Test info() - Log message cấp độ INFO")
//    void testInfoLevel() {
//        logger.info("Test info message");
//
//        String output = outputStream.toString();
//        assertTrue(output.contains("[INFO]"), "Output phải chứa [INFO]");
//        assertTrue(output.contains("Test info message"), "Output phải chứa message");
//    }
//
//    // ==================== Test Warning Level ====================
//    @Test
//    @DisplayName("Test warning() - Log message cấp độ WARNING")
//    void testWarningLevel() {
//        logger.warning("Test warning message");
//
//        String output = outputStream.toString();
//        assertTrue(output.contains("[WARNING]"), "Output phải chứa [WARNING]");
//        assertTrue(output.contains("Test warning message"), "Output phải chứa message");
//    }
//
//    // ==================== Test Error/Bug Level ====================
//    @Test
//    @DisplayName("Test error() - Log message cấp độ BUG")
//    void testErrorLevel() {
//        logger.error("Test error message");
//
//        String output = outputStream.toString();
//        assertTrue(output.contains("[BUG]"), "Output phải chứa [BUG]");
//        assertTrue(output.contains("Test error message"), "Output phải chứa message");
//    }
//
//    // ==================== Test Crash Level ====================
//    @Test
//    @DisplayName("Test crash() - Log message cấp độ CRASH kèm Throwable")
//    void testCrashLevel() {
//        Exception testException = new Exception("Test Exception");
//        logger.crash("Test crash message", testException);
//
//        String output = outputStream.toString();
//        assertTrue(output.contains("[CRASH]"), "Output phải chứa [CRASH]");
//        assertTrue(output.contains("Test crash message"), "Output phải chứa message");
//        assertTrue(output.contains("Test Exception"), "Output phải chứa exception message");
//    }
//
//    @Test
//    @DisplayName("Test crash() - crash với null throwable")
//    void testCrashWithNullThrowable() {
//        assertDoesNotThrow(() -> {
//            logger.crash("Crash message without exception", null);
//        }, "crash() phải xử lý được null throwable");
//
//        String output = outputStream.toString();
//        assertTrue(output.contains("[CRASH]"), "Output phải chứa [CRASH]");
//    }
//
//    // ==================== Test Minimum Level Filtering ====================
//    @Test
//    @DisplayName("Test setMinimumLevel() - INFO level chỉ log INFO và cấp cao hơn")
//    void testMinimumLevelInfo() {
//        logger.setMinimumLevel(SystemLogger.LogLevel.INFO);
//        outputStream.reset();
//
//        logger.info("Info message");
//        String output = outputStream.toString();
//
//        assertTrue(output.contains("[INFO]"), "INFO phải được log");
//    }
//
//    @Test
//    @DisplayName("Test setMinimumLevel() - WARNING level lọc bỏ INFO")
//    void testMinimumLevelWarning() {
//        logger.setMinimumLevel(SystemLogger.LogLevel.WARNING);
//        outputStream.reset();
//
//        logger.info("Info message");
//        String output = outputStream.toString();
//
//        assertTrue(output.isEmpty(), "INFO message phải bị lọc khi minimum level là WARNING");
//    }
//
//    @Test
//    @DisplayName("Test setMinimumLevel() - WARNING level log WARNING và cấp cao hơn")
//    void testMinimumLevelWarningLogsWarning() {
//        logger.setMinimumLevel(SystemLogger.LogLevel.WARNING);
//        outputStream.reset();
//
//        logger.warning("Warning message");
//        String output = outputStream.toString();
//
//        assertTrue(output.contains("[WARNING]"), "WARNING phải được log");
//    }
//
//    @Test
//    @DisplayName("Test setMinimumLevel() - CRASH level chỉ log CRASH")
//    void testMinimumLevelCrash() {
//        logger.setMinimumLevel(SystemLogger.LogLevel.CRASH);
//        outputStream.reset();
//
//        logger.error("Error message");
//        String output1 = outputStream.toString();
//
//        assertTrue(output1.isEmpty(), "ERROR message phải bị lọc khi minimum level là CRASH");
//
//        outputStream.reset();
//        logger.crash("Crash message", null);
//        String output2 = outputStream.toString();
//
//        assertTrue(output2.contains("[CRASH]"), "CRASH phải được log");
//    }
//
//    @Test
//    @DisplayName("Test log format - chứa thread name")
//    void testLogFormatThreadName() {
//        logger.info("Thread test");
//
//        String output = outputStream.toString();
//        String currentThreadName = Thread.currentThread().getName();
//
//        assertTrue(output.contains("[" + currentThreadName + "]"),
//                "Output phải chứa thread name hiện tại");
//    }
//}