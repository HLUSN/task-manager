package com.example.task_manager_sqlite;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskManagerSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        this.driver = new ChromeDriver(options); // Fix: Use this.driver instead of hiding field
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAddTask() {
        // Open app page
        driver.get("http://localhost:" + port + "/tasks");

        // Wait for page to load completely
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        // Find the input field and add button
        WebElement taskInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter task name']")));

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//form[@class='add-form']//button[@type='submit']")));

        // Enter task name and click add
        taskInput.sendKeys("New Selenium Task");
        addButton.click();

        // Wait for the task to appear in the table
        // The task will be in an input field within a table row
        boolean taskFound = wait.until(webDriver -> { // Fix: Use different parameter name to avoid hiding field
            List<WebElement> inputFields = webDriver.findElements(
                    By.xpath("//table//input[@type='text']"));
            for (WebElement input : inputFields) {
                String value = input.getAttribute("value");
                if ("New Selenium Task".equals(value)) {
                    return true;
                }
            }
            return false;
        });

        assertTrue(taskFound, "New task should be displayed in the table");
    }
}