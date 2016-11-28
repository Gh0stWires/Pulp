package tk.samgrogan.pulp.Data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by ghost on 11/25/2016.
 */

public class ComicColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";

    @DataType(TEXT) @NotNull
    public static final String TITLE = "title";

    @DataType(INTEGER) @NotNull
    public static final String PAGE = "page";
}
