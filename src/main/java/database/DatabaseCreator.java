package database;

import function.SystemLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseCreator{
    private final String url = "jdbc:sqlite:./database/RAP.sqlite";
    private final String urlPath = "./database/RAP.sqlite";
    private final String scriptPath = "./database/createTables.sql";
    private static DatabaseCreator instance;
    private SystemLogger log = SystemLogger.getInstance();
    //Constructor
    private DatabaseCreator(){}
    public static DatabaseCreator getInstance(){
        if(instance == null){
            synchronized (DatabaseCreator.class){
                instance = new DatabaseCreator();
                return instance;
            }
        }
        return instance;
    }
    public String getUrl(){return url;}
    public String getUrlPath(){return urlPath;}
    public String getScriptPath(){return scriptPath;}

    public void loadScriptSQL(String script){
        // 1. Đọc toàn bộ file SQL từ ký tự ĐẦU TIÊN đến ký tự CUỐI CÙNG thành 1 chuỗi String
        StringBuilder scriptBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(script))) {
            String line;
            while ((line = br.readLine()) != null) {
                scriptBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            log.crash("Lỗi không mở được file Scripts SQL",e);
        }

        String fullScript = scriptBuilder.toString();
        String[] sqlStatements = fullScript.split(";");
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            int successCount = 0;
            for (String sql : sqlStatements) {
                String cleanSql = sql.trim(); // Loại bỏ nốt các ký tự xuống dòng \n còn sót lại

                // Điều kiện lọc: Nếu dòng đó không rỗng và không phải là dòng comment (--) thì mới chạy
                if (!cleanSql.isEmpty() && !cleanSql.startsWith("--")) {

                    stmt.execute(cleanSql);
                    successCount++;
                }
            }
            System.out.println("Đã duyệt xong! Chạy thành công " + successCount + " câu lệnh tạo bảng.");

        } catch (Exception e) {
            System.err.println("Lỗi khi chạy lệnh SQL xuống Database: " + e.getMessage());
        }

    }

    public void createDatabase() throws SQLException {
        DriverManager.getConnection(url);
    }

    public Connection getConnection(String dbPath, String script) throws  SQLException {
        Path path_database = Paths.get(dbPath);
        Path path_script = Paths.get(script);
        if(!Files.exists(path_database)){
            System.out.println("File database chưa có, tiến hành tạo tự động!");
            this.loadScriptSQL(scriptPath);
        }
        if(!Files.exists(path_script)){
            throw new SQLException("File script tao database chua co");
        }
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);

    }

    public Connection getConnection() throws  SQLException {
        Path path_database = Paths.get(urlPath);
        Path path_script = Paths.get(scriptPath);
        if(!Files.exists(path_database)){
            System.out.println("File database chưa có, tiến hành tạo tự động!");
            this.loadScriptSQL(scriptPath);
        }
        if(!Files.exists(path_script)){
            throw new SQLException("File script tao database chua co");
        }
        return DriverManager.getConnection(url);

    }


}
