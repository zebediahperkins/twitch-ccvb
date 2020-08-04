package main.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import main.bots.Account;
import main.bots.Bot;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BrowserUtils {
    public final static JFrame frame = new JFrame("Progress");
    public final static JLabel status = new JLabel("");
    public final static String currentAccString = "Current Account: ";
    public final static String statusString = "; Status: ";
    public static int currentAcc = 1;

    public static void launchCVB(File accountsJson, File proxiesTxt, int botsPerIp, int totalBots, String streamerName) {
        List<String> proxies = new ArrayList<>();
        List<Account> accounts = new ArrayList<>();
        try {
            proxies = FileUtils.parseProxiesTXT(proxiesTxt);
            accounts = FileUtils.parseAccountsJSONList(accountsJson);
        } catch (FileNotFoundException e) { e.printStackTrace(); }

        currentAcc = 1;
        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setText(currentAccString + currentAcc + statusString + "Starting CVB...");
        frame.add(status);
        frame.setSize(350, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        for (int currentProxy = 0; currentAcc < totalBots + 1; ++currentAcc, ++currentProxy) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); /*** Comment this line out to disable headless browser ***/
            options.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
            if (currentProxy / botsPerIp < proxies.size()) {
                String proxy = proxies.get(currentProxy / botsPerIp);
                System.out.println("Connecting to proxy: " + proxy);
                options.addArguments("--proxy-server=socks5://" + proxy);
            } else {
                System.out.println("Out of proxies, running on the host's ip");
            }
            Bot bot = new Bot(accounts.get(currentAcc - 1).getUsername(), accounts.get(currentAcc - 1).getPassword(), new ChromeDriver(options));
            if (bot.login()) {
                System.out.println("Account " + accounts.get(currentAcc - 1).getUsername() + " successfully logged in");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) { e.printStackTrace(); }
                bot.navigateAndChat(streamerName);
                System.out.println("Account " + accounts.get(currentAcc - 1).getUsername() + " started chatting");
            } else {
                --currentAcc;
            }
        }
        status.setText("Done! All accounts up and running...");
    }

    public static void launchAccCreator(File accountsJson, File proxiesTxt, int botsPerIp, int totalBots) {
        List<String> proxies = new ArrayList<>();
        try {
            proxies = FileUtils.parseProxiesTXT(proxiesTxt);
        } catch (FileNotFoundException e) { e.printStackTrace(); }
        currentAcc = 1;
        status.setHorizontalAlignment(SwingConstants.CENTER);
        status.setText(currentAccString + currentAcc + statusString + "Starting account creator...");
        frame.add(status);
        frame.setSize(350, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        for (int currentProxy = 0; currentAcc < totalBots + 1; ++currentAcc, ++currentProxy) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless"); /*** Comment this line out to disable headless browser ***/
            options.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
            if (currentProxy / botsPerIp < proxies.size()) {
                String proxy = proxies.get(currentProxy / botsPerIp);
                System.out.println("Connecting to proxy: " + proxy);
                options.addArguments("--proxy-server=socks5://" + proxy);
            } else {
                System.out.println("Out of proxies, running on the host's ip");
            }
            Account account = new Bot(new ChromeDriver(options)).createAccount();
            if (account != null) {
                try {
                    JsonObject accountsObject = FileUtils.parseAccountsJSONObj(accountsJson);
                    JsonArray accountsArray = accountsObject.getAsJsonArray("Accounts");
                    accountsArray.add(JsonParser.parseString("{username:" + account.getUsername() + ",password:" + account.getPassword() + "}"));
                    FileUtils.writeToAccountsJSON(accountsJson, accountsObject);
                    System.out.println("Created new account with username: " + account.getUsername() + " and password: " + account.getPassword());
                } catch (IOException e) { e.printStackTrace(); }
            } else {
                --currentAcc;
            }
        }
        status.setText("Done! All accounts logged in...");
    }

    public enum StatusCode {
        SUCCESS,
        FAILURE,
        WRONG_URL
    }

    public static StatusCode tryFindElement(By by, WebDriver driver, String expectedURL) {
        if (!driver.getCurrentUrl().equals(expectedURL)) return StatusCode.WRONG_URL;
        try {
            driver.findElement(by);
        } catch (NoSuchElementException ignored) { return StatusCode.FAILURE; }
        return StatusCode.SUCCESS;
    }
}
