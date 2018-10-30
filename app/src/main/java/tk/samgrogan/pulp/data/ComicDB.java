package tk.samgrogan.pulp.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by ghost on 11/25/2016.
 */
@Database(version = ComicDB.VERSION)
class ComicDB {
    private ComicDB(){}

    public static final int VERSION = 1;

    @Table(ComicColumns.class) public static final String COMICS = "comics";
    //@Table(ComicColumns.class) public static final String COMICS = "comics";
}
