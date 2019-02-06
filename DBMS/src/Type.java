public class Type {

    final int MAX_FIELD_NUM = 8;
    final int FIELD_LENGHT = 16;
    final int FIELD_NUM;
    String[] type;

    public Type(int fieldNum) {
        type = new String[MAX_FIELD_NUM+2];
        type[0] = "0";
        FIELD_NUM = fieldNum;
    }
    public void setTypeName(String fieldName){
        String fixedName = fixedLengthString(fieldName, FIELD_LENGHT);
        type[1] = fixedName;
    }
    public void setFieldName(String fieldName , int FieldNumber){
        String fixedName = fixedLengthString(fieldName, FIELD_LENGHT);
        type[FieldNumber+2] = fixedName;
    }
    public static String fixedLengthString(String string, int length) {
        return String.format("%1$-"+length+ "s", string);
    }
    public String typeToString(){
        String finalStr = "";
        for (int i = 0; i < 10; i++) {
            finalStr = finalStr +type[i];
        }
        return finalStr;
    }
}
