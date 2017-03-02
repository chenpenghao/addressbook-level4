# To-do List - Developer Guide

By : `Team SE-EDU`  &nbsp;&nbsp;&nbsp;&nbsp; Since: `Jun 2016`  &nbsp;&nbsp;&nbsp;&nbsp; Licence: `MIT`

---

1. [Setting Up](#setting-up)
2. [Design](#design)
3. [Implementation](#implementation)
4. [Testing](#testing)
5. [Dev Ops](#dev-ops)

* [Appendix A: User Stories](#appendix-a--user-stories)
* [Appendix B: Use Cases](#appendix-b--use-cases)
* [Appendix C: Non Functional Requirements](#appendix-c--non-functional-requirements)
* [Appendix D: Glossary](#appendix-d--glossary)
* [Appendix E : Product Survey](#appendix-e--product-survey)


## 1. Setting up

### 1.1. Prerequisites

1. **JDK `1.8.0_60`**  or later<br>

    > Having any Java 8 version is not enough. <br>
    This app will not work with earlier versions of Java 8.

2. **Eclipse** IDE
3. **e(fx)clipse** plugin for Eclipse (Do the steps 2 onwards given in
   [this page](http://www.eclipse.org/efxclipse/install.html#for-the-ambitious))
4. **Buildship Gradle Integration** plugin from the Eclipse Marketplace
5. **Checkstyle Plug-in** plugin from the Eclipse Marketplace


### 1.2. Importing the project into Eclipse

0. Fork this repo, and clone the fork to your computer
1. Open Eclipse (Note: Ensure you have installed the **e(fx)clipse** and **buildship** plugins as given
   in the prerequisites above)
2. Click `File` > `Import`
3. Click `Gradle` > `Gradle Project` > `Next` > `Next`
4. Click `Browse`, then locate the project's directory
5. Click `Finish`

  > * If you are asked whether to 'keep' or 'overwrite' config files, choose to 'keep'.
  > * Depending on your connection speed and server load, it can even take up to 30 minutes for the set up to finish
      (This is because Gradle downloads library files from servers during the project set up process)
  > * If Eclipse auto-changed any settings files during the import process, you can discard those changes.

### 1.3. Configuring Checkstyle
1. Click `Project` -> `Properties` -> `Checkstyle` -> `Local Check Configurations` -> `New...`
2. Choose `External Configuration File` under `Type`
3. Enter an arbitrary configuration name e.g. addressbook
4. Import checkstyle configuration file found at `config/checkstyle/checkstyle.xml`
5. Click OK once, go to the `Main` tab, use the newly imported check configuration.
6. Tick and select `files from packages`, click `Change...`, and select the `resources` package
7. Click OK twice. Rebuild project if prompted

> Note to click on the `files from packages` text after ticking in order to enable the `Change...` button

### 1.4. Troubleshooting project setup

**Problem: Eclipse reports compile errors after new commits are pulled from Git**

* Reason: Eclipse fails to recognize new files that appeared due to the Git pull.
* Solution: Refresh the project in Eclipse:<br>
  Right click on the project (in Eclipse package explorer), choose `Gradle` -> `Refresh Gradle Project`.

**Problem: Eclipse reports some required libraries missing**

* Reason: Required libraries may not have been downloaded during the project import.
* Solution: [Run tests using Gradle](UsingGradle.md) once (to refresh the libraries).


## 2. Design

### 2.1. Architecture

<img src="images/Architecture.png" width="600"><br>
_Figure 2.1.1 : Architecture Diagram_

The **_Architecture Diagram_** given above explains the high-level design of the App.
Given below is a quick overview of each component.

> Tip: The `.pptx` files used to create diagrams in this document can be found in the [diagrams](diagrams/) folder.
> To update a diagram, modify the diagram in the pptx file, select the objects of the diagram, and choose `Save as picture`.

`Main` has only one class called [`MainApp`](../src/main/java/seedu/address/MainApp.java). It is responsible for,

* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup method where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.
Two of those classes play important roles at the architecture level.

* `EventsCenter` : This class (written using [Google's Event Bus library](https://github.com/google/guava/wiki/EventBusExplained))
  is used by components to communicate with other components using events (i.e. a form of _Event Driven_ design)
* `LogsCenter` : Used by many classes to write log messages to the App's log file.

The rest of the App consists of four components.

* [**`UI`**](#ui-component) : The UI of the App.
* [**`Logic`**](#logic-component) : The command executor.
* [**`Model`**](#model-component) : Holds the data of the App in-memory.
* [**`Storage`**](#storage-component) : Reads data from, and writes data to, the hard disk.

Each of the four components

* Defines its _API_ in an `interface` with the same name as the Component.
* Exposes its functionality using a `{Component Name}Manager` class.

For example, the `Logic` component (see the class diagram given below) defines it's API in the `Logic.java`
interface and exposes its functionality using the `LogicManager.java` class.<br>
<img src="images/LogicClassDiagram.png" width="800"><br>
_Figure 2.1.2 : Class Diagram of the Logic Component_

#### Events-Driven nature of the design

The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the
command `delete 1`.

<img src="images\SDforDeletePerson.png" width="800"><br>
_Figure 2.1.3a : Component interactions for `delete 1` command (part 1)_

>Note how the `Model` simply raises a `AddressBookChangedEvent` when the Address Book data are changed,
 instead of asking the `Storage` to save the updates to the hard disk.

The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
<img src="images\SDforDeletePersonEventHandling.png" width="800"><br>
_Figure 2.1.3b : Component interactions for `delete 1` command (part 2)_

> Note how the event is propagated through the `EventsCenter` to the `Storage` and `UI` without `Model` having
  to be coupled to either of them. This is an example of how this Event Driven approach helps us reduce direct
  coupling between components.

The sections below give more details of each component.

### 2.2. UI component

Author: Alice Bee

<img src="images/UiClassDiagram.png" width="800"><br>
_Figure 2.2.1 : Structure of the UI Component_

**API** : [`Ui.java`](../src/main/java/seedu/address/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`,
`StatusBarFooter`, `BrowserPanel` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files
 that are in the `src/main/resources/view` folder.<br>
 For example, the layout of the [`MainWindow`](../src/main/java/seedu/address/ui/MainWindow.java) is specified in
 [`MainWindow.fxml`](../src/main/resources/view/MainWindow.fxml)

The `UI` component,

* Executes user commands using the `Logic` component.
* Binds itself to some data in the `Model` so that the UI can auto-update when data in the `Model` change.
* Responds to events raised from various parts of the App and updates the UI accordingly.

### 2.3. Logic component

Author: Bernard Choo

<img src="images/LogicClassDiagram.png" width="800"><br>
_Figure 2.3.1 : Structure of the Logic Component_

**API** : [`Logic.java`](../src/main/java/seedu/address/logic/Logic.java)

1. `Logic` uses the `Parser` class to parse the user command.
2. This results in a `Command` object which is executed by the `LogicManager`.
3. The command execution can affect the `Model` (e.g. adding a person) and/or raise events.
4. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")`
 API call.<br>
<img src="images/DeletePersonSdForLogic.png" width="800"><br>
_Figure 2.3.1 : Interactions Inside the Logic Component for the `delete 1` Command_

### 2.4. Model component

Author: Cynthia Dharman

<img src="images/ModelClassDiagram.png" width="800"><br>
_Figure 2.4.1 : Structure of the Model Component_

**API** : [`Model.java`](../src/main/java/seedu/address/model/Model.java)

The `Model`,

* stores a `UserPref` object that represents the user's preferences.
* stores the Address Book data.
* exposes a `UnmodifiableObservableList<ReadOnlyPerson>` that can be 'observed' e.g. the UI can be bound to this list
  so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.

### 2.5. Storage component

Author: Darius Foong

<img src="images/StorageClassDiagram.png" width="800"><br>
_Figure 2.5.1 : Structure of the Storage Component_

**API** : [`Storage.java`](../src/main/java/seedu/address/storage/Storage.java)

The `Storage` component,

* can save `UserPref` objects in json format and read it back.
* can save the Address Book data in xml format and read it back.

### 2.6. Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

## 3. Implementation

### 3.1. Logging

We are using `java.util.logging` package for logging. The `LogsCenter` class is used to manage the logging levels
and logging destinations.

* The logging level can be controlled using the `logLevel` setting in the configuration file
  (See [Configuration](#configuration))
* The `Logger` for a class can be obtained using `LogsCenter.getLogger(Class)` which will log messages according to
  the specified logging level
* Currently log messages are output through: `Console` and to a `.log` file.

**Logging Levels**

* `SEVERE` : Critical problem detected which may possibly cause the termination of the application
* `WARNING` : Can continue, but with caution
* `INFO` : Information showing the noteworthy actions by the App
* `FINE` : Details that is not usually noteworthy but may be useful in debugging
  e.g. print the actual list instead of just its size

### 3.2. Configuration

Certain properties of the application can be controlled (e.g App name, logging level) through the configuration file
(default: `config.json`):


## 4. Testing

Tests can be found in the `./src/test/java` folder.

**In Eclipse**:

* To run all tests, right-click on the `src/test/java` folder and choose
  `Run as` > `JUnit Test`
* To run a subset of tests, you can right-click on a test package, test class, or a test and choose
  to run as a JUnit test.

**Using Gradle**:

* See [UsingGradle.md](UsingGradle.md) for how to run tests using Gradle.

We have two types of tests:

1. **GUI Tests** - These are _System Tests_ that test the entire App by simulating user actions on the GUI.
   These are in the `guitests` package.

2. **Non-GUI Tests** - These are tests not involving the GUI. They include,
   1. _Unit tests_ targeting the lowest level methods/classes. <br>
      e.g. `seedu.address.commons.UrlUtilTest`
   2. _Integration tests_ that are checking the integration of multiple code units
     (those code units are assumed to be working).<br>
      e.g. `seedu.address.storage.StorageManagerTest`
   3. Hybrids of unit and integration tests. These test are checking multiple code units as well as
      how the are connected together.<br>
      e.g. `seedu.address.logic.LogicManagerTest`

#### Headless GUI Testing
Thanks to the [TestFX](https://github.com/TestFX/TestFX) library we use,
 our GUI tests can be run in the _headless_ mode.
 In the headless mode, GUI tests do not show up on the screen.
 That means the developer can do other things on the Computer while the tests are running.<br>
 See [UsingGradle.md](UsingGradle.md#running-tests) to learn how to run tests in headless mode.

### 4.1. Troubleshooting tests

 **Problem: Tests fail because NullPointException when AssertionError is expected**

 * Reason: Assertions are not enabled for JUnit tests.
   This can happen if you are not using a recent Eclipse version (i.e. _Neon_ or later)
 * Solution: Enable assertions in JUnit tests as described
   [here](http://stackoverflow.com/questions/2522897/eclipse-junit-ea-vm-option). <br>
   Delete run configurations created when you ran tests earlier.

## 5. Dev Ops

### 5.1. Build Automation

See [UsingGradle.md](UsingGradle.md) to learn how to use Gradle for build automation.

### 5.2. Continuous Integration

We use [Travis CI](https://travis-ci.org/) and [AppVeyor](https://www.appveyor.com/) to perform _Continuous Integration_ on our projects.
See [UsingTravis.md](UsingTravis.md) and [UsingAppVeyor.md](UsingAppVeyor.md) for more details.

### 5.3. Publishing Documentation

See [UsingGithubPages.md](UsingGithubPages.md) to learn how to use GitHub Pages to publish documentation to the
project site.

### 5.4. Making a Release

Here are the steps to create a new release.

 1. Generate a JAR file [using Gradle](UsingGradle.md#creating-the-jar-file).
 2. Tag the repo with the version number. e.g. `v0.1`
 2. [Create a new release using GitHub](https://help.github.com/articles/creating-releases/)
    and upload the JAR file you created.

### 5.5. Converting Documentation to PDF format

We use [Google Chrome](https://www.google.com/chrome/browser/desktop/) for converting documentation to PDF format,
as Chrome's PDF engine preserves hyperlinks used in webpages.

Here are the steps to convert the project documentation files to PDF format.

 1. Make sure you have set up GitHub Pages as described in [UsingGithubPages.md](UsingGithubPages.md#setting-up).
 1. Using Chrome, go to the [GitHub Pages version](UsingGithubPages.md#viewing-the-project-site) of the
    documentation file. <br>
    e.g. For [UserGuide.md](UserGuide.md), the URL will be `https://<your-username-or-organization-name>.github.io/addressbook-level4/docs/UserGuide.html`.
 1. Click on the `Print` option in Chrome's menu.
 1. Set the destination to `Save as PDF`, then click `Save` to save a copy of the file in PDF format. <br>
    For best results, use the settings indicated in the screenshot below. <br>
    <img src="images/chrome_save_as_pdf.png" width="300"><br>
    _Figure 5.4.1 : Saving documentation as PDF files in Chrome_

### 5.6. Managing Dependencies

A project often depends on third-party libraries. For example, Address Book depends on the
[Jackson library](http://wiki.fasterxml.com/JacksonHome) for XML parsing. Managing these _dependencies_
can be automated using Gradle. For example, Gradle can download the dependencies automatically, which
is better than these alternatives.<br>
a. Include those libraries in the repo (this bloats the repo size)<br>
b. Require developers to download those libraries manually (this creates extra work for developers)<br>

## Appendix A : User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have)  - `* *`,  Low (unlikely to have) - `*`


Priority | As a ... | I want to ... | So that...
-------- | :-------- | :--------- | :-----------
`* * *` | new user | see usage instructions | I can refer to instructions when I forget how to use the App
`* * *` | user | add a task by specifying a task description and the deadline | I can record tasks that need to be done by a specific day
`* * *` | user |add an event/task with description, time period and date	| I can record events that need to be done in a specific period 
`* * *` | user |add an event/task with description that has variation of formats	| I am not limited to one and only one format - I can have some flexibility
`* * *` | user |add a recurring task with description, time, frequency	|I can record tasks that need to be done on a specific day
`* * *` | user |add a tag/multiple tag to an existing event/task	| I do not have to always add the tag when I add the task
`* * *` | user |delete unwanted tag	| I can remove the tags when I no longer need them
`* * *` | user |delete task | I can remove the tasks when I have completed them
`* * *` | user |to have confirmation on deleting uncompleted task	| I will not accidentally delete a task
`* * *` | user |edit the description/deadline of a certain task |	I can update the task once the conditions changes/deadline extended
`* * *` | user |edit the tag of a certain task	| I can update the task once I feel another tag is more suitable
`* * *` | user |edit the tag name	| I can update name of a tag for all tasks under it once for all
`* * *` | user |find the tasks whose description contains certain key words	| I can easily locate the task if I cannot remember fully the task name
`* * *` | user |find the tasks by giving multiple criteria	| I can have the choice of input more information to search for the task I have in mind
`* * *` | user |find the tag by typing the keywords	| I can locate the tags if I cannot remember fully the tag name
`* * *` | user |find the most urgent task/the task with the highest urgency level	| I can know in a blink what I am supposed to do now
`* * *` | user |have an exhaustive user guide on all commands	| I will know all availabe commands that I have
`* * *` | user |have user guide on specific area of commands |	I will know what are the available commands for that area
`* * *` | user | obtain a list of all task |	I can know all the tasks I have at hand
`* * *` | user |list tasks whose deadlines are within a certain time period	| I can know all the tasks that have deadlines within that time period
`* * *` | user |list all tasks whose deadline is on one given day	| I can know by certain time point what I should have completed
`* * *` | user |have a list of completed task	| I can check what I have done
`* * *` | user |list all tasks by under a certain tag	| I can know that tasks I have within one category
`* * *` | user |list the ongoing tasks for a given day	| I can know what I am occupied with on that day
`* * *` | user |list all tasks under multiple tags	| I can know the tasks under more than just one tag
`* * *` | user |select one/multiple tasks/tags	| I can carry out further actions on the selected tasks
`* * *` | user |save the data into a folder I have specified	| I know where to find the data I have entered
`* * *` | user |undo the most recent operation	| in case I made a mistake I can reverse
`* * *` | user |exit the software | I can move to other windows when I finish using To-do list
`* *` | user |group/filter tasks according to categories such as work/family/etc |	I will not be distracted by irrelevant tasks at specific times
`* *` | user | edit the subtasks of a task	| I can update the subtasks if there is any changes
`* *` | user |have suggestions when I typed invalidly |	I will know how to correct my mistakes
`* *` | New user | have reminders of format when I started a command word	| I will know the correct format of the input
`* *` | user | assign importance levels to each task with colour coding	| I can easily spot the more important tasks and prioritize them
`* *` | user |set notifications before the due time for certain tasks |	I will be reminded of the task in a timely manner
`* *` | user |split tasks into subtasks with a progress bar | I can keep track of my progress in individual tasks
`* *` | user |copy and paste the task to some other day |	I don't need to type extra
`* * ` | Advanced user | use shorter versions of a command/customize my commands	 | I can type a command faster
`*` | user|have a guided tour of the software	| I will know what are the available features of the software
`*` | user|customize the timing for reminder	| I will not be bombasted with too many reminders
`*` | user| share task/tasks with others |	I can designate my tasks to others

{More to be added}
## Appendix B : Use Cases
     	        	        	        	
(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)
 
#### Use case #1: add task
 
**MSS**
 
1. User requests to add a task
2. To-do list adds the new task and shows the details of the new task on UI
Use case ends
 
**Extensions**
 
1a. The new task created contains some optional parameters including description, start time, end time, tag, urgency level and so on.
 
>1a1. To-do list adds in the input parameters and shows the details on UI <br>
> Use case ends
 
1b. The input task name contains invalid input
 
> 1b1. To-do list shows an error message for invalid name input <br>
> Use case ends
 
1c. The input start time is before the end
time
 
>1c1. To-do list shows an error message for invalid input <br>
> Use case ends

1d. The input start time or end time is in an invalid format
 
>1d1. To-do list shows an error message for invalid input <br>
> Use case ends
 
1e. The input tag name does not exist and user would like to create a new tag
 
> 1e1. To-do list prompts a message to query if the user would like to create the tag
> 1e2. User choose yes
> 1e3. To-do list creates the new tag and shows information for the new task on UI <br>
> Use case ends
 
1f. The input tag name does not exist and it is actually a typo error
 
> 1f1. To-do list prompts a message to query if the user would like to create the tag
> 1f2. User choose no <br>
> Use case resumes at Step 1
 
1g. The input urgency level is not valid e.g. the user input a float while an integer is expected
 
> 1d1. To-do list shows an error message for invalid input <br>
> Use case ends
 
1h. There already exists a task with exact same value of all parameters
 
> 1h1. To-do list shows an error message for conflicts <br>
> Use case ends

<br>

#### Use case #2: add tag
 
**MSS**
 
1. User requests to add a tag
2. To-do list adds the new tag and shows the details of the new task on UI
Use case ends
 
**Extensions**
 
1a. The input tag name already exists
 
> 1a1. To-do list prompts an error message that the tag name already exists <br>
> Use case ends
 
1b. The input new tag name does not have a proper format
 
> 1b1. To-do list prompts an error message for invalid input <br>
> Use case ends
 
1c. User would like to add new task immediately to the task
> 1c1. To-do list processes Update tags of a task (UC #5)
> Use case ends
 
<br>

#### Use case #3: customize commands
 
**MSS**
 
1. User requests to customize some command with their customized shortcut
2. To-do list updates the command input and show message on UI
Use case ends
 
**Extensions**
 
1a. The input command word already exists
> 1a1. To-do list prompts an error message for invalid input <br>
> Use case ends
 
1b. The input command word contains invalid input
> 1b1. To-do list prompts an error message for invalid input <br>
> Use case ends

<br>

#### Use case #4: Delete task or tag
 
**MSS**
 
1. User requests to delete a task or tag
2. To-do list delete the task and prompts message on UI to show success  
Use case ends
 
**Extensions**
 
1a. The task/tag selected does not exist
> 1a1. To-do list shows an error message for target not found <br>
> Use case ends
 
1b. The user requests to delete all tasks under certain criteria, e.g. under the same tag or contains certain keywords
> 1b1. To-do list lists out the tasks that fulfil the criteria (UC #) and prompts a confirmation message
> 1b2. User confirms his actions
> 1b3. To-do list delete the selected tasks and prompts a message on UI to show success <br>
> Use case ends

<br>

#### Use case #5: Update a task
 
**MSS**
 
1. User requests to update a task
2. To-do list select the task and show the current information
3. User select the parameters to be updated and input new value
4. To-do list updates the value, and show updated information on UI
Use case ends
 
**Extensions**
3a. The task is not found
> 1a1. To-do list prompts an error message for target not found <br>
> Use case ends
 
3b. The input new values contain invalid input
> 3b1. To-do list prompts an error message for invalid input <br>
> Use case ends
 
3c. The use input a tag that does now exist
> 3c1. To-do list prompts a message to query if the user needs to create a new list
> 3c2. User confirms yes or no
> 3c3. To-do list add the tag (UC #2) and update the task, show new information on UI <br>
> Use case ends

<br>

#### Use case #6: Update a tag name
 
**MSS**
 
1. User requests to select a tag update its name
2. To-do list update the name of the tag and show the updated information on UI
Use case ends
 
**Extensions**
1a. The selected tag does not exist
> 1a1. To-do list shows error message on target not found. <br>
> Use case ends

1b. The input new tag name is invalid

> 1b1. To-do list shows error message on invalid input <br>
> Use case ends
 
1c. The input new tag name already exist

> 1c1. To-do list prompts message to query the user if he would like to merge two tags
> 1c2. User confirms yes
> 1c3. To-do list merges the two tags (UC #7)
> Use case ends

<br>

#### Use case #7: Merge two tags
 
**MSS**
 
1. User requests to select tag A to be merged to tag B
2. To-do list merges the two tags by changing tags of all tasks under A to B
Use case ends

**Extensions**

1a. Either or both of the selected tags are not found
> 1a1. To-do list shows error message for target not found
> Use case ends

2a. Some of the tasks are having both tags at the same time
> 2a1. To-do list deleted tag A of such tasks and leave only tag B
> Use case ends <br>

<br>

#### Use case #8: List tasks
 
**MSS**
 
1. User requests to list tasks by certain parameters or criteria, for example by urgency levels, by end time, by tag, etc.
2. To-do list shows all tasks that the user requests on UI
Use case ends
 
**Extensions**
 
1a. The given description is not following the format
 
> 1a1. To-do list shows an error message for invalid input <br>
> Use case ends
 
1b. To-do list does not found any tasks
> 1b1. To-do list returns ‘No task has been found.’ <br>
> Use case ends <br>

<br>

#### Use case #9: List tags
 
**MSS**
 
1. User requests to list tags
2. To-do list shows all tags on UI
Use case ends
 
**Extensions**
 
1a. The given description is not following the format
 
> 1a1. To-do list shows an error message for invalid input <br>
> Use case ends
 
1b. To-do list does not found any tags
>1b1. To-do list returns ‘No tags has been found.’ <br>
> Use case ends <br>

<br>

#### Use case #10: List reminders
 
**MSS**
 
1. User requests to list reminders
2. To-do list shows all reminders on UI
Use case ends
 
**Extensions**
 
1a. The given description is not following the format
 
>1a1. To-do list shows an error message for invalid input <br>
> Use case ends
 
1b. To-do list does not found any tags
> 1b1. To-do list returns ‘No reminder has been found.’ <br>
> Use case ends <br>

<br>

#### Use case #11: Select a task or a tag        	
 
**MSS**
 
1. User requests to select an object from a list of tasks by its given number <br>
or to select consecutive objects from a list <br>
or to select multiple objects form a list <br>
or to select all objects form a list <br>
2. To-do list selects and displays the task on UI.
 
Use case ends
 
**Extensions**
 
1a. User input is invalid
 
>1a1. To-do list shows an error message for invalid input. <br>
 
> Use case ends
 
1b. User input number out of range.
 
> 1b1. To-do list shows an error message for invalid number. <br>
> Use case ends <br>

<br>

#### Use case #12: Exit                  	
 
**MSS**
 
1. User requests to exit To-do list
2. To-do list exits.
 
Use case ends

**Extension**
1a. The user input exit in the middle of an unfinished action
> 1a1. To-do List show error message that the user could not exit until he finishes the action <br>
> Use case ends <br>

<br>

#### Use case #13: Storage             	
 
**MSS**
 
1. User requests to change the path of the storage file to an existing file and export all data into that file <br>
or change the path of the storage file to a new file and export all data into that file <br>
or  clear all existing data <br>
 
2. To-do list asks for confirmation.
3. User inputs yes or no.
4. To-do list do the commands accordingly and displays ‘operation done’ on UI
 
Use case ends
 
**Extensions**
 
1a. User input is invalid
 
> 1a1. To-do list shows an error message for invalid input. <br>
> Use case ends <br>

<br>

#### Use case #14: Set reminder      	
 
**MSS**
 
1. User requests to set a reminder including its description, frequency and end time (for a specific task)
2. To-do list set the reminder and displays ‘operation done’ on UI
 
Use case ends
 
**Extensions**
 
1a. User input is invalid
> 1a1. To-do list shows an error message for invalid input. <br>
> Use case ends <br>

<br>

#### Use case #15: Tag                  	
 
**MSS**
 
1. User requests to add a tag to the selected tasks
2. To-do created the tag or added the tag and displays ‘operation done’ on UI
 
Use case ends
 
**Extensions**
 
1a. User input is invalid
 
> 1a1. To-do list shows an error message for invalid input. <br>
 
1b. The tag input does not exist
> 1b1. To-do list create the new tag (UC#2) <br>
> Use case ends <br>

<br>

#### Use case #16: Help

**MSS**

1. User requests help message
2. To-do list prompts help message on the UI 
Use case ends

**Extensions**

1a. User requests help message in a specific field or contains certain keywords
> 1a1. To-do list provide the information according to the criteria <br>
> Use case ends

<br>

#### Use case #17: Find

**MSS**

1. User requests to look for tasks or tags with a certain criteria
2. To-do list provides a list of tasks or tags that fulfil the criteria (UC #8)
3. User select one of the tasks or tags (UC #9)
Use case ends

**Extension**
1a. The input criteria does not match any tasks or tags
> 1a1. To-do list prompts error message for target not found. <br>
> Use case ends

1b. The input criteria does not follow the format
> 1b1. To-do list prompts error message for invalid input. <br>
> Use case ends

<br>

#### Use case #18: Undo an operation	
 
**MSS**
 
1. User requests to undo the last operations. Only a maximum of three consecutive undo requests is allowed.
2. To-do list undoes the last operation and displays ‘operation done’ on UI.
 
Use case ends
 
**Extensions**
 
1a. To-do list finds no previous operation. There could be result from too many undo requests or first time using the software.
 
>1a1. To-do list shows ‘no previous operation found’<br>
> Use case ends
 
1b. Last operation is invalid
 
>1b1. To-do list undoes last valid operation.
 
> Use case ends
 
 
1c. Last operation cannot be undo (e.g. clear all data)
>1c1. To-do list shows an error message for invalid operation <br>
 
> Use case ends


## Appendix C : Non Functional Requirements

1. Should work on any [mainstream OS](#mainstream-os) as long as it has Java `1.8.0_60` or higher installed.
2. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands)
   should be able to accomplish most of the tasks faster using commands than using the mouse.

{More to be added}

## Appendix D : Glossary

##### Mainstream OS

> Windows, Linux, Unix, OS-X

##### Private contact detail

> A contact detail that is not meant to be shared with others

## Appendix E : Product Survey

**Product Name**

Author: ...

Pros:

* ...
* ...

Cons:

* ...
* ...

