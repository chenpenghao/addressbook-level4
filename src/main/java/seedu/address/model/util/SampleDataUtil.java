package seedu.address.model.util;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ToDoList;
import seedu.address.model.ReadOnlyToDoList;
import seedu.address.model.task.EndTime;
import seedu.address.model.task.StartTime;
import seedu.address.model.task.Title;
import seedu.address.model.task.Task;
import seedu.address.model.task.Venue;
import seedu.address.model.task.UniqueTaskList.DuplicateTaskException;
import seedu.address.model.tag.UniqueTagList;

public class SampleDataUtil {
    public static Task[] getSampleTasks() {
        try {
            return new Task[] {
                new Task(new Title("CS2103 Tutorial"), new Venue("COM1-B110"), new StartTime("March 8, 10.00@gmail.com"),
                    new EndTime("March 8, 11.00"),
                    new UniqueTagList("tutorials")),
                new Task(new Title("CS2103 Lecture"), new Venue("I3-AUD"), new StartTime("March 10, 16.00"),
                    new EndTime("March 10, 18.00"),
                    new UniqueTagList("lectures", "lessons")),
                new Task(new Title("Print ST3131 Notes"), new Venue("Biz Librar"), new StartTime("March 8, 11.00"),
                    new EndTime("March 8, 12.00"),
                    new UniqueTagList("chores")),
                new Task(new Title("FIN3101 Project Meeting"), new Venue("BIZ2 Level 5"), new StartTime("March 8, 14.00"),
                    new EndTime("March 8, 16.00"),
                    new UniqueTagList("projects")),
                new Task(new Title("QF3101 Midterm"), new Venue("LT20"), new StartTime("March 9, 12.00"),
                    new EndTime("March 9, 13.30"),
                    new UniqueTagList("exams")),
                new Task(new Title("GET1018 Midterm"), new Venue("Utown-AUD3"), new StartTime("March 13, 18.00"),
                    new EndTime("March 13, 20.00"),
                    new UniqueTagList("exams"))
            };
        } catch (IllegalValueException e) {
            throw new AssertionError("sample data cannot be invalid", e);
        }
    }

    public static ReadOnlyToDoList getSampleToDoList() {
        try {
            ToDoList sampleAB = new ToDoList();
            for (Task sampleTask : getSampleTasks()) {
                sampleAB.addTask(sampleTask);
            }
            return sampleAB;
        } catch (DuplicateTaskException e) {
            throw new AssertionError("sample data cannot contain duplicate tasks", e);
        }
    }
}
