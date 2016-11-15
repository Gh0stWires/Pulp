package tk.samgrogan.pulp.Models;

import com.github.junrar.rarfile.FileHeader;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gh0st on 3/6/16.
 */
public class Comics implements Serializable{

    transient List<FileHeader> pageHeaders;
    List<File> filenames = new ArrayList<File>();

    public Comics(){

    }

    public void setFilenames(File filename) {
        this.filenames.add(filename);
    }

    public File getFilenames(int position) {
        return filenames.get(position);
    }

    public void setPageHeaders(List<FileHeader> pageHeaders) {
        this.pageHeaders = pageHeaders;
    }

    public List<FileHeader> getPageHeaders() {
        return pageHeaders;
    }
}

