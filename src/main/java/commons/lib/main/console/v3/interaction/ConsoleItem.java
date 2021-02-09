package commons.lib.main.console.v3.interaction;

import commons.lib.tooling.documentation.MdDoc;

@MdDoc(description = "this interface is used for all possible choice in a menu.")
public interface ConsoleItem {

    @MdDoc(description = "The text as it will be displayed to the user.")
    String label();

    @MdDoc(description = "This method is called when an item has been selected.")
    ConsoleItem[] run();

    default int ordering()  {
        return 0;
    }

}
