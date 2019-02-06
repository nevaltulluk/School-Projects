import java.util.ArrayList;
import java.util.List;

public class Record {

    final int MAX_FIELD_NUM = 8;
    final int FIELD_LENGHT = 16;
    int fieldNum;
    String[] data;

    public Record(int fieldNum) {
        this.fieldNum = fieldNum;
        data = new String[fieldNum+1];
        data[0] = "0";

    }
    public void setFieldValue(int recordValue , int FieldNumber){
        String value = fixedLengthString(recordValue, FIELD_LENGHT);
        data[FieldNumber+1] = value;
    }
    public String recordToString(){
        String finalStr = "";
        for (int i = 0; i < fieldNum+1; i++) {
            finalStr = finalStr +data[i];
        }
        return finalStr;
    }

    public static String fixedLengthString(int string, int length) {
        return String.format("%1$-"+length+ "s", string);
    }
}
