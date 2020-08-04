package main.bots;

import main.util.BrowserUtils;
import main.util.FileUtils;
import org.ccvb.main.ImageSolver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

public class Bot {
    private String username;
    private String password;
    private WebDriver driver;

    public Bot(String username, String password, WebDriver driver) {
        this.username = username;
        this.password = password;
        this.driver = driver;
    }
    public Bot(WebDriver driver) {
        this.driver = driver;
    }

    private String getGreeting() {
        String[] greetings = {"Hello", "Hi", "Yo", "Hey guys", "Waddup", "What's up", "Hey chat"};
        return greetings[new Random().nextInt(greetings.length)];
    }

    private String getMessage(/*TODO: Add a mood parameter that influences the message to be returned*/) {
        String[] emotes = {"POG", "POGGERS", "LULW", "F", "f", "PogChamp", "Kreygasm", "HeyGuys", "Kappa", "LUL", "VoHiYo", "NotLikeThis", "<3", "BibleThump", "WutFace", "ResidentSleeper", "SeemsGood", "CTC", "cmonBruh", "BloodTrail", "W", "L"};
        String[] messages = {"Win one for the boys", "what is that sound?", "Can I play with you?", "how many hours do you have?", "gamer moment", "can you add me?", "when do you usually stream?", "how long have you been playing?", "Anyone wanna follow for follow?", "How do I donate to this chad?", "Epic gamer moment", "Hahahaha", "Hackerman", "Yea", "No", "Yes", "Missed you guys", "How's your day going?", "How are you?", "fat", "Exactly", "bully", "pimpin", "bro", "bruh moment", "bruh", "What is this?", "When will you stream again?", "I have to go soon", "I'm confused", "What will you do if I follow?", "What will you do if I donate?", "Sneaky", "Clop clop", "Thanks chat", "Don't do that", "Damn, you're pretty good", "Are you getting off soon?", "Don't leave pls"};
        if (new Random().nextInt(5) > 2) return messages[new Random().nextInt(messages.length)];
        String emote = emotes[new Random().nextInt(emotes.length)];
        String emoteSpam = emote;
        int rand = new Random().nextInt(12);
        for (int i = 0; i < rand; ++i) emoteSpam += " " + emote;
        return emoteSpam;
    }

    private void chat(String text) {
        driver.findElement(By.tagName("textarea")).sendKeys(text + Keys.ENTER);
    }

    public boolean navigateAndChat(String streamerName) {
        if (this.username == null || this.password == null) return false; //TODO: Change return value to a status code. This should return no credentials.
        new Thread(() -> {
            driver.get("https://www.twitch.tv/" + streamerName);
            By chatMessageFieldTag = By.tagName("textarea");
            Checkpoint checkpoint = new Checkpoint("https://www.twitch.tv/" + streamerName, chatMessageFieldTag);
            while (checkpoint.getCheckpoint(driver, 5000, 200) == null) {
                System.out.println("Waiting...");
            }
            chat(getGreeting());
            while (true) {
                By getCheckpoint = checkpoint.getCheckpoint(driver, 8000, 200);
                if (getCheckpoint == null) break;
                try {
                    Thread.sleep(new Random().nextInt(45000) + 10000);
                } catch (InterruptedException e) { e.printStackTrace(); }
                chat(getMessage());
                System.out.println("Sent message");
                //TODO: If not following streamer, decide whether or not to follow
            }
            driver.quit();
        }).start();
        return true;
    }

    public boolean login() {
        if (this.username == null || this.password == null) return false; //TODO: Change return value to a status code. This should return no credentials.
        driver.get("https://www.twitch.tv/login");
        By loginButtonXPath = By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div[3]/form/div/div[3]/button");
        By verifyingXPath = By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div[3]/div/div/div[1]/div[2]/p");
        By workingCaptchaXPath = By.xpath("/html/body/div[1]/div[1]/p");
        By captchaWrapper1 = By.xpath("//*[@id=\"fc-iframe-wrap\"]");
        Checkpoint checkpoint = new Checkpoint("https://www.twitch.tv/login", loginButtonXPath, verifyingXPath, workingCaptchaXPath, captchaWrapper1);
        while (true) {
            By getCheckpoint = checkpoint.getCheckpoint(driver, 8000, 200);
            if (!driver.getCurrentUrl().equals("https://www.twitch.tv/login")) {
                System.out.println("Breaking");
                return true;
            }
            if (getCheckpoint == null) return false;
            if (getCheckpoint.equals(loginButtonXPath)) {
                WebElement loginButton = driver.findElement(loginButtonXPath);
                WebElement usernameField = driver.findElement(By.xpath("//*[@id=\"login-username\"]"));
                WebElement passwordField = driver.findElement(By.xpath("//*[@id=\"password-input\"]"));
                if (loginButton.isEnabled()) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Clicking log in button");
                    loginButton.click();
                } else if (usernameField.getAttribute("value").length() == 0 && passwordField.getAttribute("value").length() == 0) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Filling out login form");
                    usernameField.sendKeys(username);
                    passwordField.sendKeys(password);
                } else {
                    continue; //return true; //TODO: Change return value to a status code. This should return invalid login credentials.
                }
            } else if (getCheckpoint.equals(verifyingXPath) || getCheckpoint.equals(workingCaptchaXPath)) {
                BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Waiting for captcha");
                continue;
            } else if (getCheckpoint.equals(captchaWrapper1)) {
                By continueButtonXPath = By.xpath("//*[@id=\"triggerLiteMode\"]");
                By instructionTextXPath = By.xpath("//*[@id=\"instruction-text\"]");
                By tryAgainButtonXPath = By.xpath("//*[@id=\"try_again\"]");
                By altTryAgainButtonXPath = By.xpath("//*[@id=\"begin_btn\"]");
                Checkpoint wrapperCheckpoint = new Checkpoint("https://www.twitch.tv/login", continueButtonXPath, instructionTextXPath, tryAgainButtonXPath, altTryAgainButtonXPath);
                getCheckpoint = wrapperCheckpoint.getCheckpoint(driver.switchTo().frame(driver.findElement(By.xpath("//*[@id=\"fc-iframe-wrap\"]"))), 8000, 200);
                if (getCheckpoint == null) continue;
                if (getCheckpoint.equals(continueButtonXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Starting captcha");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                    new Actions(driver).sendKeys(Keys.TAB, Keys.ENTER).build().perform();
                } else if (getCheckpoint.equals(instructionTextXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Solving captcha");
                    String imageURL = driver.findElement(By.xpath("//*[@id=\"fcimgform1\"]/span[2]/input")).getAttribute("src");
                    try {
                        int rotation = (int) ImageSolver.findCorrectStandingRotation(40.0, ImageIO.read(new URL(imageURL)));
                        for (int i = 1; i < 9; ++i) {
                            WebElement img = driver.findElement(By.xpath("//*[@id=\"fcimgform" + i + "\"]/span[2]/input"));
                            if (img.getAttribute("class").equals("pic-1 b-lazy rot-" + rotation + " b-loaded")) {
                                img.click();
                                break;
                            }
                        }
                    } catch (IOException e) { e.printStackTrace(); }
                } else if (getCheckpoint.equals(tryAgainButtonXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Re-attempting captcha");
                    driver.findElement(tryAgainButtonXPath).click();
                }
                else if (getCheckpoint.equals(altTryAgainButtonXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Re-attempting captcha");
                    driver.findElement(altTryAgainButtonXPath).click();
                }
                driver.switchTo().parentFrame();
            }
        }
    }

    public Account createAccount() {
        driver.get("https://www.twitch.tv/signup");
        By signUpButtonXPath = By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div[3]/form/div/div[5]/button");
        By verifyingXPath = By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div[3]/div/div/div[1]/div[2]/p");
        By workingCaptchaXPath = By.xpath("/html/body/div[1]/div[1]/p");
        By captchaWrapper1 = By.xpath("//*[@id=\"fc-iframe-wrap\"]");
        By remindMeButtonXPath = By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div/div/div[3]/div[2]/div/button");
        Checkpoint checkpoint = new Checkpoint("https://www.twitch.tv/signup", remindMeButtonXPath, signUpButtonXPath, verifyingXPath, workingCaptchaXPath, captchaWrapper1);
        while (true) {
            By getCheckpoint = checkpoint.getCheckpoint(driver, 8000, 200);
            if (getCheckpoint == null) break;
            if (getCheckpoint.equals(signUpButtonXPath)) {
                WebElement signUpButton = driver.findElement(signUpButtonXPath);
                if (signUpButton.isEnabled()) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Clicking signup button");
                    signUpButton.click();
                } else {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Filling out signup form");
                    WebElement usernameField = driver.findElement(By.xpath("//*[@id=\"signup-username\"]"));
                    WebElement passwordField = driver.findElement(By.xpath("//*[@id=\"password-input\"]"));
                    WebElement confirmPassField = driver.findElement(By.xpath("//*[@id=\"password-input-confirmation\"]"));
                    WebElement monthField = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div[3]/form/div/div[3]/div/div[2]/div[1]/select"));
                    WebElement dayField = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div[3]/form/div/div[3]/div/div[2]/div[2]/div/input"));
                    WebElement yearField = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[1]/div[3]/div/div/div/div[3]/form/div/div[3]/div/div[2]/div[3]/div/input"));
                    WebElement emailField = driver.findElement(By.xpath("//*[@id=\"email-input\"]"));
                    String username = FileUtils.getRandomEnglishWord() + new Random().nextInt(1000);
                    String password = FileUtils.getRandomEnglishWord() + FileUtils.getRandomEnglishWord() + new Random().nextInt(1000);
                    String email = username + "@yahoo.com";
                    int birthMonth = new Random().nextInt(12) + 1;
                    int birthDay = new Random().nextInt(22) + 1;
                    int birthYear = new Random().nextInt(50) + 1950;
                    usernameField.sendKeys(username);
                    passwordField.sendKeys(password);
                    confirmPassField.sendKeys(password);
                    this.username = username;
                    this.password = password;
                    for (int i = 0; i < birthMonth; ++i) { monthField.sendKeys(Keys.ARROW_DOWN); }
                    monthField.sendKeys(Keys.ENTER);
                    dayField.sendKeys(Integer.toString(birthDay));
                    yearField.sendKeys(Integer.toString(birthYear));
                    emailField.sendKeys(email);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }
            } else if (getCheckpoint.equals(remindMeButtonXPath)) {
                BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Writing to accounts.json");
                driver.quit();
                return new Account(username, password);
            } else if (getCheckpoint.equals(verifyingXPath) || getCheckpoint.equals(workingCaptchaXPath)) {
                BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Waiting for captcha");
                continue;
            } else if (getCheckpoint.equals(captchaWrapper1)) {
                By continueButtonXPath = By.xpath("//*[@id=\"triggerLiteMode\"]");
                By instructionTextXPath = By.xpath("//*[@id=\"instruction-text\"]");
                By tryAgainButtonXPath = By.xpath("//*[@id=\"try_again\"]");
                By altTryAgainButtonXPath = By.xpath("//*[@id=\"begin_btn\"]");
                Checkpoint wrapperCheckpoint = new Checkpoint("https://www.twitch.tv/signup", continueButtonXPath, instructionTextXPath, tryAgainButtonXPath, altTryAgainButtonXPath);
                getCheckpoint = wrapperCheckpoint.getCheckpoint(driver.switchTo().frame(driver.findElement(By.xpath("//*[@id=\"fc-iframe-wrap\"]"))), 8000, 200);
                if (getCheckpoint == null) continue;
                if (getCheckpoint.equals(continueButtonXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Starting captcha");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                    new Actions(driver).sendKeys(Keys.TAB, Keys.ENTER).build().perform();
                } else if (getCheckpoint.equals(instructionTextXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Solving captcha");
                    String imageURL = driver.findElement(By.xpath("//*[@id=\"fcimgform1\"]/span[2]/input")).getAttribute("src");
                    try {
                        int rotation = (int) ImageSolver.findCorrectStandingRotation(40.0, ImageIO.read(new URL(imageURL)));
                        for (int i = 1; i < 9; ++i) {
                            WebElement img = driver.findElement(By.xpath("//*[@id=\"fcimgform" + i + "\"]/span[2]/input"));
                            if (img.getAttribute("class").equals("pic-1 b-lazy rot-" + rotation + " b-loaded")) {
                                img.click();
                                break;
                            }
                        }
                    } catch (IOException e) { e.printStackTrace(); }
                } else if (getCheckpoint.equals(tryAgainButtonXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Re-attempting captcha");
                    driver.findElement(tryAgainButtonXPath).click();
                }
                else if (getCheckpoint.equals(altTryAgainButtonXPath)) {
                    BrowserUtils.status.setText(BrowserUtils.currentAccString + BrowserUtils.currentAcc + BrowserUtils.statusString + "Re-attempting captcha");
                    driver.findElement(altTryAgainButtonXPath).click();
                }
                driver.switchTo().parentFrame();
            }
        }
        driver.quit();
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
