package seedu.address.logic.commands;

public class ListTagCommand extends Command{
	 public static final String COMMAND_WORD = "listTag";

	    public static final String MESSAGE_SUCCESS = "Listed all tags";


	    @Override
	    public CommandResult execute() {
	        model.updateFilteredListToShowAll();
	        return new CommandResult(MESSAGE_SUCCESS);
	    }

}
