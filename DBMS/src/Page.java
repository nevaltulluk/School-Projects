import java.io.IOException;
import java.io.RandomAccessFile;

public class Page {
    final int PAGE_SIZE = 1; //KB
    int page_Id;
    int next_page_pointer;
    int remaining_space;
    int header_var_size = 4;


    public Page(RandomAccessFile recordFileRAD, int pageID, int numOfFields)throws IOException{
        page_Id = pageID;
        next_page_pointer = pageID + PAGE_SIZE;
        remaining_space = (PAGE_SIZE*1024 / (1+(numOfFields * 16))-1);
        String header = "" + fixedLengthString(page_Id, header_var_size)  + "" + fixedLengthString(next_page_pointer, header_var_size)
                +"" + fixedLengthString(remaining_space, header_var_size);
        recordFileRAD.writeBytes(header);
    }
    public static String fixedLengthString(int string, int length) {
        return String.format("%1$-"+length+ "s", string);
    }

}
