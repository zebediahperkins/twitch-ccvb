package main.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import main.bots.Account;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileUtils {
    private FileUtils() {}

    public static int getLineCount(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);
        int lineCount;
        for (lineCount = 0; fileScanner.hasNextLine(); ++lineCount) fileScanner.nextLine();
        return lineCount;
    }

    public static JsonObject parseAccountsJSONObj(File file) throws FileNotFoundException {
        String jsonString = "";
        Scanner fileScanner = new Scanner(file);

        while (fileScanner.hasNextLine()) jsonString += fileScanner.nextLine();

        return JsonParser.parseString(jsonString).getAsJsonObject();
    }

    public static List<Account> parseAccountsJSONList(File file) throws FileNotFoundException {
        List<Account> accounts = new ArrayList<>();
        String jsonString = "";
        Scanner fileScanner = new Scanner(file);
        while (fileScanner.hasNextLine()) jsonString += fileScanner.nextLine();
        JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonObject().get("Accounts").getAsJsonArray();
        jsonArray.forEach((jsonElement -> {
            accounts.add(new Account(jsonElement.getAsJsonObject().get("username").toString().replace("\"", ""), jsonElement.getAsJsonObject().get("password").toString().replace("\"", "")));
        }));
        return accounts;
    }

    public static void writeToAccountsJSON(File file, JsonObject accounts) throws IOException {
        FileWriter profileWriter = new FileWriter(file);
        profileWriter.write(accounts.toString());
        profileWriter.close();
    }

    public static List<String> parseProxiesTXT(File file) throws FileNotFoundException {
        List<String> proxies = new ArrayList<>();
        Scanner fileScanner = new Scanner(file);

        while (fileScanner.hasNextLine()) proxies.add(fileScanner.nextLine());

        return proxies;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    public static String getRandomEnglishWord() {
        try (Stream<String> lines = Files.lines(Paths.get("src/main/resources/dictionary.txt"))) {
            Optional<String> text = lines.skip(new Random().nextInt(370105)).findFirst();
            if (text.isPresent()) return text.get();
        } catch (IOException e) {e.printStackTrace();}
        return "";
    }
}
