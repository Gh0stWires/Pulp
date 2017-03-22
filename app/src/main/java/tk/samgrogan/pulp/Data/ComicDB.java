package tk.samgrogan.pulp.Data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by ghost on 11/25/2016.
 */
@Database(version = ComicDB.VERSION)
public class ComicDB {
    private ComicDB(){}

    public static final int VERSION = 1;

    @Table(ComicColumns.class) public static final String COMICS = "comics";
    //@Table(ComicColumns.class) public static final String COMICS = "comics";
}
