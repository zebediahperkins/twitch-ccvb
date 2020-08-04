package main.bots;

import main.util.BrowserUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Checkpoint {
    private String url;
    private By[] elements;

    public Checkpoint(String url, By ...elements) {
        this.url = url;
        this.elements = elements;
    }

    public By getCheckpoint(WebDriver driver, int timeOut, int interval) {
        for (int currentTime = 0; currentTime <= timeOut; currentTime += interval) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) { e.printStackTrace(); }
            for (By element : elements) {
                if (BrowserUtils.tryFindElement(element, driver, url) == BrowserUtils.StatusCode.SUCCESS) {
                    return element;
                }
            }
        }
        return null;
    }
    public By getCheckpoint(WebDriver driver) { return getCheckpoint(driver, 0, 0); }

    public By[] getElements() {
        return elements;
    }

    public void setElements(By[] components) {
        this.elements = components;
    }
}
