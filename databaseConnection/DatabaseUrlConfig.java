package pl.buarzi.databaseConnection;

import java.io.*;

public class DatabaseUrlConfig {
    private String databaseAdress;
    private static final String fileName = "config.ini";

    public DatabaseUrlConfig() {
        createAdress();
    }

    public String getDatabaseAdress() {
        return databaseAdress;
    }

    private void createAdress() {
        String oneLine = "";
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedWriter = new BufferedReader(fileReader);
            while ((oneLine = bufferedWriter.readLine()) != null) {
                if (oneLine.startsWith("database_address")) {
                    oneLine = oneLine.trim().replace(" ", "");
                    oneLine = oneLine.substring(oneLine.indexOf("=") + 1, oneLine.length() - 1);
                    databaseAdress = oneLine;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot read config file");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
