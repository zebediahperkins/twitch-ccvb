package test.bots;

import main.bots.Checkpoint;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CheckpointTest {

    @org.junit.jupiter.api.Test
    void getCheckpoint() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        System.setProperty("webdriver.chrome.driver", "dependencies/selenium/chromedriver.exe");
        String url = "file:///" + new File("src/test/resources/html/test.html").getAbsolutePath().replace("\\", "/");
        Checkpoint checkpoint = new Checkpoint(url, By.id("ThisIdDoesNotExist"), By.id("header"), By.id("AnotherNonexistentId"));
        driver.get(url);
        assertEquals(checkpoint.getCheckpoint(driver), By.id("header"));
        checkpoint.setElements(new By[]{ By.id("hello"), By.id("DoesNotExist") });
        assertEquals(checkpoint.getCheckpoint(driver, 5000, 50), By.id("hello"));
    }
}