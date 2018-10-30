package tk.samgrogan.pulp.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

/**
 * Created by ghost on 3/15/2017.
 */

class ShortBoxColumn {
    @DataType(INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(TEXT)
    @Unique(onConflict = ConflictResolutionType.IGNORE)
    public static final String BOXTITLE = "box_title";

    /*@DataType(TEXT)
    @Unique(onConflict = ConflictResolutionType.IGNORE)
    public static final String CACH_DIR = "cache_dir";
*/
    @DataType(INTEGER)
    public static final String PAGE = "page";
}
