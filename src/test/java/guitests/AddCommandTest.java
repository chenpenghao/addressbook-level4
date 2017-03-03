package guitests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import guitests.guihandles.TaskCardHandle;
import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.testutil.TestTask;
import seedu.address.testutil.TestUtil;

public class AddCommandTest extends ToDoListGuiTest {

    @Test
    public void add() {
        //add one Task
        TestTask[] currentList = td.getTypicalTasks();
        TestTask TaskToAdd = td.hoon;
        assertAddSuccess(TaskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, TaskToAdd);

        //add another Task
        TaskToAdd = td.ida;
        assertAddSuccess(TaskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, TaskToAdd);

        //add duplicate Task
        commandBox.runCommand(td.hoon.getAddCommand());
        assertResultMessage(AddCommand.MESSAGE_DUPLICATE_Task);
        assertTrue(TaskListPanel.isListMatching(currentList));

        //add to empty list
        commandBox.runCommand("clear");
        assertAddSuccess(td.alice);

        //invalid command
        commandBox.runCommand("adds Johnny");
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }

    private void assertAddSuccess(TestTask TaskToAdd, TestTask... currentList) {
        commandBox.runCommand(TaskToAdd.getAddCommand());

        //confirm the new card contains the right data
        TaskCardHandle addedCard = TaskListPanel.navigateToTask(TaskToAdd.getName().fullName);
        assertMatching(TaskToAdd, addedCard);

        //confirm the list now contains all previous Tasks plus the new Task
        TestTask[] expectedList = TestUtil.addTasksToList(currentList, TaskToAdd);
        assertTrue(TaskListPanel.isListMatching(expectedList));
    }

}
