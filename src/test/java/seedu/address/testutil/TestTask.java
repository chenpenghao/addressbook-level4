package seedu.address.testutil;

import seedu.address.model.task.EndTime;
import seedu.address.model.task.StartTime;
import seedu.address.model.task.Title;
import seedu.address.model.task.Venue;
import seedu.address.model.task.ReadOnlyTask;
import seedu.address.model.tag.UniqueTagList;

/**
 * A mutable task object. For testing only.
 */
public class TestTask implements ReadOnlyTask {

    private Title title;
    private EndTime endtime;
    private StartTime starttime;
    private Venue venue;
    private UniqueTagList tags;

    public TestTask() {
        tags = new UniqueTagList();
    }

    /**
     * Creates a copy of {@code taskToCopy}.
     */
    public TestTask(TestTask taskToCopy) {
        this.title = taskToCopy.getTitle();
        this.venue = taskToCopy.getVenue();
        this.starttime = taskToCopy.getStartTime();
        this.endtime = taskToCopy.getEndTime();
        this.tags = taskToCopy.getTags();
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public void setEndTime(EndTime endtime) {
        this.endtime = endtime;
    }

    public void setStartTime(StartTime starttime) {
        this.starttime = starttime;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void setTags(UniqueTagList tags) {
        this.tags = tags;
    }

    @Override
    public Title getTitle() {
        return title;
    }

    @Override
    public Venue getVenue() {
        return venue;
    }

    @Override
    public StartTime getStartTime() {
        return starttime;
    }

    @Override
    public EndTime getEndTime() {
        return endtime;
    }

    @Override
    public UniqueTagList getTags() {
        return tags;
    }

    @Override
    public String toString() {
        return getAsText();
    }

    public String getAddCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append("add " + this.getTitle().title + " ");
        sb.append("v/" + this.getEndTime().value + " ");
        sb.append("s/" + this.getVenue().value + " ");
        sb.append("e/" + this.getStartTime().value + " ");
        this.getTags().asObservableList().stream().forEach(s -> sb.append("t/" + s.tagName + " "));
        return sb.toString();
    }
}
