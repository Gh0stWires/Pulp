package tk.samgrogan.pulp.Data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by ghost on 11/25/2016.
 */
@ContentProvider(authority = ComicProvider.AUTHORITY, database = ComicDB.class)
public class ComicProvider {

    public static final String AUTHORITY = "tk.samgrogan.pulp.Data.ComicProvider";

    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path{
        String COMICS = "comics";
    }

    private static Uri buildUri(String... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path:paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = ComicDB.COMICS)
    public static class Comics{
        @ContentUri(
                path = Path.COMICS,
                type = "vnd.android.cursor.dir/quote"
        )
        public static final Uri CONTENT_URI = buildUri(Path.COMICS);

        @InexactContentUri(
                name = "COMIC_ID",
                path = Path.COMICS + "/*",
                type = "vnd.android.cursor.item/comic",
                whereColumn = ComicColumns.TITLE,
                pathSegment = 1
        )
        public static Uri withSymbol(String title){
            return buildUri(Path.COMICS, title);
        }
    }
}
