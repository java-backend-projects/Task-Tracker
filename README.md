# Task Tracker CLI (https://roadmap.sh/projects/task-tracker)

This is a simple CLI (Command-Line Interface) application for managing tasks. You can do all CRUD operations on tasks, and also mark them as "In progress"/"Done", and by status when listing.

## Features

- **Add a task:** Create a new task with the specified description
- **Update a task:** Change existing task's description
- **Delete a task:** Delete existing task
- **Change task's status:** Mark existing task as "In progress" or "Done"
- **List tasks:** List all existing tasks or filter them by status

## Installation

1. **Clone the repo:**
  ```bash
  git clone https://github.com/java-backend-projects/Task-Tracker
  ```
2. **Unzip archive with .jar file and .sh script**
  ```bash
  unzip task-tracker-cli.zip
  cd task-tracker-cli
  ```
3. **Run the application:**
  ```bash
  ./task-tracker-cli.sh <command> [ARGUMENTS]
  ```

## Usage
```bash
# Adding a new task
./task-tracker-cli.sh add "Buy groceries"
# Output: Task added successfully (ID: 1)

# Updating and deleting tasks
./task-tracker-cli.sh update 1 "Buy groceries and cook dinner"
./task-tracker-cli.sh delete 1

# Marking a task as in progress or done
./task-tracker-cli.sh mark-in-progress 1
./task-tracker-cli.sh mark-done 1

# Listing all tasks
./task-tracker-cli.sh list

# Listing tasks by status
./task-tracker-cli.sh list done
./task-tracker-cli.sh list todo
./task-tracker-cli.sh list in-progress
```
