# src/test/resources/features/task_management.feature
Feature: Task Management
  As a user
  I want to manage tasks
  So that I can keep track of my to-do items

  Scenario: Add a new task
    Given I am on the tasks page
    When I enter "Complete Cucumber tests" into the task field
    And I click the "Add Task" button
    Then I should see "Complete Cucumber tests" in the task list

  Scenario: Delete a task
    Given I am on the tasks page
    And there is a task named "Task to be deleted"
    When I delete the task "Task to be deleted"
    Then I should not see "Task to be deleted" in the task list

  Scenario: Update a task
    Given I am on the tasks page
    And there is a task named "Old task name"
    When I update the task "Old task name" to "New task name"
    Then I should see "New task name" in the task list
    And I should not see "Old task name" in the task list