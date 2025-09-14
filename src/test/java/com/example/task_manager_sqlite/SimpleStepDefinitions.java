package com.example.task_manager_sqlite;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.github.bonigarcia.wdm.WebDriverManager;
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

import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration // Add this annotation
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleStepDefinitions {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("I am on the tasks page")
    public void i_am_on_the_tasks_page() {
        driver.get("http://localhost:" + port + "/tasks");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    @When("I enter {string} into the task field")
    public void i_enter_into_the_task_field(String taskName) {
        WebElement taskInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter task name']")));
        taskInput.clear();
        taskInput.sendKeys(taskName);
    }

    @When("I click the {string} button")
    public void i_click_the_button(String buttonText) {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), '" + buttonText + "')]")));
        button.click();

        // Wait for operations to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Then("I should see {string} in the task list")
    public void i_should_see_in_the_task_list(String taskName) {
        boolean taskFound = wait.until(driver -> {
            List<WebElement> taskElements = driver.findElements(
                    By.xpath("//table//tr//input[@type='text']"));
            for (WebElement element : taskElements) {
                String value = element.getAttribute("value");
                if (taskName.equals(value)) {
                    return true;
                }
            }
            return false;
        });
        assertTrue(taskFound, "Task should be visible in the list: " + taskName);
    }

    @Then("I should not see {string} in the task list")
    public void i_should_not_see_in_the_task_list(String taskName) {
        boolean taskNotFound = wait.until(driver -> {
            List<WebElement> taskElements = driver.findElements(
                    By.xpath("//table//tr//input[@type='text']"));
            for (WebElement element : taskElements) {
                String value = element.getAttribute("value");
                if (taskName.equals(value)) {
                    return false;
                }
            }
            return true;
        });
        assertTrue(taskNotFound, "Task should not be in the list: " + taskName);
    }

    @And("there is a task named {string}")
    public void there_is_a_task_named(String taskName) {
        i_enter_into_the_task_field(taskName);
        i_click_the_button("Add Task");

        // Verify the task was added
        i_should_see_in_the_task_list(taskName);
    }

    @When("I delete the task {string}")
    public void i_delete_the_task(String taskName) {
        WebElement deleteButton = wait.until(driver -> {
            List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));
            for (WebElement row : rows) {
                List<WebElement> inputs = row.findElements(By.xpath(".//input[@type='text']"));
                for (WebElement input : inputs) {
                    String value = input.getAttribute("value");
                    if (taskName.equals(value)) {
                        return row.findElement(By.xpath(".//button[contains(text(), 'Delete')]"));
                    }
                }
            }
            return null;
        });
        assertNotNull(deleteButton, "Delete button should be found for task: " + taskName);
        deleteButton.click();

        // Wait for deletion to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @When("I update the task {string} to {string}")
    public void i_update_the_task_to(String oldName, String newName) {
        WebElement taskInput = wait.until(driver -> {
            List<WebElement> inputs = driver.findElements(By.xpath("//table//tr//input[@type='text']"));
            for (WebElement input : inputs) {
                String value = input.getAttribute("value");
                if (oldName.equals(value)) {
                    return input;
                }
            }
            return null;
        });
        assertNotNull(taskInput, "Task input should be found: " + oldName);

        // Update the task name
        taskInput.clear();
        taskInput.sendKeys(newName);

        // Find and click the update button
        WebElement updateForm = taskInput.findElement(By.xpath("./ancestor::tr//form[contains(@action, 'update')]"));
        WebElement updateButton = updateForm.findElement(By.tagName("button"));
        updateButton.click();

        // Wait for update to complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}