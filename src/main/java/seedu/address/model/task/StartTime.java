package seedu.address.model.task;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Represents a Task's start time in the to-do list.
 * Guarantees: immutable; is valid as declared in {@link #isValidStartTime(String)}
 */
public class StartTime {

<<<<<<< HEAD
    public static final String MESSAGE_STARTTIME_CONSTRAINTS = "Task starttime numbers should only contain numbers";
    public static final String STARTTIME_VALIDATION_REGEX = "[^\\s].*";
=======
    public static final String MESSAGE_STARTTIME_CONSTRAINTS = "Person emails should be 2 alphanumeric/period strings separated by '@'";
    public static final String STARTTIME_VALIDATION_REGEX = "[\\w\\.]+@[\\w\\.]+";
>>>>>>> 23a468444a6352c09647dd2b7461d1249ff5a848

    public final String value;

    /**
     * Validates given start time.
     *
     * @throws IllegalValueException if given start time string is invalid.
     */
    public StartTime(String starttime) throws IllegalValueException {
        assert starttime != null;
        String trimmedStartTime = starttime.trim();
        if (!isValidStartTime(trimmedStartTime)) {
            throw new IllegalValueException(MESSAGE_STARTTIME_CONSTRAINTS);
        }
        this.value = trimmedStartTime;
    }

    /**
     * Returns true if a given string is a valid task start time.
     */
    public static boolean isValidStartTime(String test) {
        return test.matches(STARTTIME_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof StartTime // instanceof handles nulls
                && this.value.equals(((StartTime) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
