import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static final Scanner scanner = new Scanner(System.in);

    final static String SYSTEM_CAT = "system_cat.txt";
    final static int MAX_FIELD_NUM = 8;

    public static void main(String[] args) {
        File system_catalogue = new File(SYSTEM_CAT);
        try {
            //creates system catalogue if it doesnt exist already.
            if (!system_catalogue.exists()) {
                system_catalogue.createNewFile();
            }
            RandomAccessFile systemCatWriter = new RandomAccessFile(system_catalogue, "rw");

            //the main loop that you can choose operations.
            while (true) {

                System.out.println("SELECT OPERATION:");
                System.out.println("1 -> DDL Operations");
                System.out.println("2 -> DML Operations");
                System.out.println("3 -> QUIT");
                int choice = scanner.nextInt();
                if (choice == 1) {
                    System.out.println("1 - Create a Type.");
                    System.out.println("2 - Delete a Type.");
                    System.out.println("3 - List all Types.");
                    choice = scanner.nextInt();
                    if (choice == 1) {
                        create_a_type(systemCatWriter);
                    } else if (choice == 2) {
                        delete_a_type(systemCatWriter);
                    } else if (choice == 3) {
                        ArrayList<String> listAllTypes = list_all_types(systemCatWriter);
                        for (int i = 0; i < listAllTypes.size(); i++) {

                            System.out.println(listAllTypes.get(i));

                        }
                    }
                } else if (choice == 2) {
                    System.out.println("1 -> Create a Record");
                    System.out.println("2 -> Delete a Record");
                    System.out.println("3 -> Search a Record");
                    System.out.println("4 -> List All Records");
                    choice = scanner.nextInt();
                    if (choice == 1) {
                        create_a_record(systemCatWriter);
                    } else if (choice == 2) {
                        delete_a_record(systemCatWriter);
                    } else if (choice == 3) {
                        System.out.println(search_a_record(systemCatWriter).substring(1));

                    } else if (choice == 4) {
                        ArrayList<String > listAllRecords = list_all_records(systemCatWriter);
                        for (int i = 0; i < listAllRecords.size(); i++) {
                                System.out.println(listAllRecords.get(i));
                        }
                    }
                } else if (choice == 3) {
                    System.out.println("exiting..");
                    break;
                }
                System.out.println("insert anything for new operation");
                scanner.next();
            }
            systemCatWriter.close();
            scanner.close();


        } catch (IOException e) {
            System.out.println(SYSTEM_CAT + " cannot be created");
            e.printStackTrace();
        }


    }

    /**
     * updates the remaining space information
     * @param recordRAF the reader and writer for the file of the concerning page
     * @param pos the position of the page
     * @param increase increase or decrease the remaining space header field by 1
     * @throws IOException
     */
    public static void updatepageinfo(RandomAccessFile recordRAF, int pos ,int increase)throws IOException{
        recordRAF.seek(pos+8);
        byte[] temp = new byte[4];
        recordRAF.read(temp, 0, 4);
        int currentempty = byteToInt(temp);
        int newempty = currentempty+increase;
        recordRAF.seek(pos+8);
        recordRAF.writeBytes(Page.fixedLengthString(newempty,4));
    }

    /**
     * creates a type, writes it into the system catalogue.
     * @param systemCatWriter the reader and writer for the file of the system catalogue
     * @throws IOException
     */
    public static void create_a_type(RandomAccessFile systemCatWriter) throws IOException {

        System.out.println("Enter the type name:");
        String typeName = scanner.next();

        System.out.println("Enter number of field names:");
        int count = scanner.nextInt();

        Type type = new Type(count);
        type.setTypeName(typeName);

        for (int i = 0; i < count; i++) {
            System.out.println("Enter the field name:");
            type.setFieldName(scanner.next(), i);
        }
        for (int i = count; i < MAX_FIELD_NUM; i++) {
            type.setFieldName("", i);
        }
        boolean written = false;
        for (int i = 0; i < systemCatWriter.length(); i = i + 145) {
            systemCatWriter.seek(i);
            if (systemCatWriter.read() == 49 && !written) {
                systemCatWriter.seek(i);
                systemCatWriter.writeBytes(type.typeToString());
                written = true;

            }
        }
        if (!written) {
            systemCatWriter.seek(systemCatWriter.length());
            systemCatWriter.writeBytes(type.typeToString());
        }

        System.out.println("created");
        File typeFile = new File(typeName + ".txt");

        if (!typeFile.exists()) {
            boolean created = typeFile.createNewFile();
            if (created) {
                System.out.println("file created successfully");
            }
        }

    }

    /**
     * creates a record for a given type
     * @param systemCatRAF the reader and writer for the file of system catalogue
     * @throws IOException
     */
    public static void create_a_record(RandomAccessFile systemCatRAF) throws IOException {
        System.out.println("enter the record type you want to create");
        String name = scanner.next();
        String fileName = name + ".txt";
        RandomAccessFile recordRAF = new RandomAccessFile(fileName, "rw");
        int fieldNumber = findFieldNumber(systemCatRAF,name);
        Record record = new Record(fieldNumber);
        for (int i = 0; i < record.fieldNum; i++) {
            System.out.println("enter the field value");
            record.setFieldValue(scanner.nextInt(), i);
        }
        boolean written = false;
        if (recordRAF.length() == 0) {
            Page page = new Page(recordRAF, 0, fieldNumber);
            recordRAF.seek(recordRAF.length());
            recordRAF.writeBytes(record.recordToString());
        } else {
            for (int i = 0; i < recordRAF.length(); i = i + 1024) {
                recordRAF.seek(i + 8);
                byte[] remainingSpace = new byte[4];
                recordRAF.read(remainingSpace, 0, 4);
                if (byteToInt(remainingSpace) != 0) {
                    for (int j = i + 16; j < i + 1024; j = j + fieldNumber * 16 + 1) {
                        recordRAF.seek(i);
                        if (recordRAF.read() == 49 && !written) {
                            recordRAF.seek(i);
                            recordRAF.writeBytes(record.recordToString());
                            written = true;
                            updatepageinfo(recordRAF,i,-1);

                        }
                    }
                    if (!written) {
                        recordRAF.seek(recordRAF.length());
                        recordRAF.writeBytes(record.recordToString());
                        written = true;
                        updatepageinfo(recordRAF,i,-1);
                    }
                }
            }
            if (!written){
                int point = (int) (recordRAF.length()/1024);
                recordRAF.seek(point*1024+4);
                byte[] pageID = new byte[4];
                recordRAF.read(pageID,0,4);
                recordRAF.seek(recordRAF.length());

                byte[] fill = new byte[(int)(((recordRAF.length()/1024)+1)*1024-recordRAF.length())];
                recordRAF.write(fill, 0, fill.length);

                Page page = new Page(recordRAF, byteToInt(pageID)+1, fieldNumber);
                recordRAF.seek(recordRAF.length());
                recordRAF.writeBytes(record.recordToString());
                written = true;
            }

        }
        recordRAF.close();
    }

    /**
     * makes the isempty field of the specified type 1
     * @param systemCatRAF
     * @throws IOException
     */
    public static void delete_a_type(RandomAccessFile systemCatRAF) throws IOException {
        System.out.println("enter the type name of the type you want to delete");
        String name = scanner.next();
        char[] nameArray = name.toCharArray();
        for (int i = 1; i < systemCatRAF.length(); i = i + 145) {
            systemCatRAF.seek(i);
            int counter = 0;
            for (int j = 0; j < name.length(); j++) {
                if (nameArray[j] == systemCatRAF.read()) {
                    counter++;
                }
            }
            if (counter == name.length()) {
                systemCatRAF.seek(i - 1);
                systemCatRAF.writeBytes("1");
                File deletedType = new File(name + ".txt");
                deletedType.delete();
            }
        }
    }

    /**
     * lists all types whose isempty field is 0
     * @param systemCatRAF
     * @return
     * @throws IOException
     */

    public static ArrayList<String> list_all_types(RandomAccessFile systemCatRAF) throws IOException {
        ArrayList<String> returnArray = new ArrayList<>();
        ArrayList<byte[]> list = new ArrayList<>();
        for (int i = 1; i < systemCatRAF.length(); i = i + 145) {
            systemCatRAF.seek(i - 1);
            if (systemCatRAF.read() != 49) {
                byte[] toString = new byte[144];
                systemCatRAF.read(toString, 0, 144);
                list.add(toString);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            String temp = "";
            for (int j = 0; j < list.get(i).length; j++) {
                temp = temp + (char) list.get(i)[j];
            }
            returnArray.add(temp);
            //System.out.println(temp);
        }
        return returnArray;
    }

    /**
     * makes a records isEmpty field 1
     * @param systemCatRAF
     * @throws IOException
     */
    public static void delete_a_record(RandomAccessFile systemCatRAF) throws IOException {
        System.out.println("enter the type name of the record you want to delete");
        String name = scanner.next();
        RandomAccessFile recordRAF = new RandomAccessFile(name.toUpperCase()+".txt" , "rw");
        System.out.println("enter the primary key of the record");
        String primary = scanner.next();
        char[] primaryKey = primary.toCharArray();
        int typeLength = findFieldNumber(systemCatRAF, name)*16 +1;
        recordRAF.seek(0);
        for (int i = 0; i < recordRAF.length(); i = i + 1024) {
            for (int j = i; j < i+1024; j = j+typeLength) {
                recordRAF.seek(i+j+13);
                int counter = 0;
                for (int k = 0; k < primaryKey.length; k++) {
                    if (primaryKey[k] == recordRAF.read()) {
                        counter++;
                    }
                }
                if (counter == primaryKey.length) {
                    recordRAF.seek(i + j + 13 - 1);
                    recordRAF.writeBytes("1");
                    recordRAF.seek(i+8);
                    byte[] temp = new byte[4];
                    recordRAF.read(temp, 0, 4);
                    int currentempty = byteToInt(temp);
                    int newempty = currentempty+1;
                    recordRAF.seek(i+8);
                    recordRAF.writeBytes(Page.fixedLengthString(newempty,4));
                }
            }
        }
        recordRAF.close();
    }

    /**
     * @param systemCatRAF
     * @return
     * @throws IOException
     */
    public static String search_a_record(RandomAccessFile systemCatRAF) throws IOException {
        System.out.println("enter the type name of the record you want to search");
        String name = scanner.next();
        RandomAccessFile recordRAF = new RandomAccessFile(name.toUpperCase()+".txt" , "rw");
        System.out.println("enter the primary key of the record");
        String primary = scanner.next();
        char[] primaryKey = primary.toCharArray();
        int typeLength = findFieldNumber(systemCatRAF, name)*16 +1;
        recordRAF.seek(0);
        byte[] searchResult = new byte[typeLength-1];
        for (int i = 0; i < recordRAF.length(); i = i + 1024) {
            for (int j = i; j < i+1024; j = j+typeLength) {
                recordRAF.seek(i+j+13);
                int counter = 0;
                for (int k = 0; k < primaryKey.length; k++) {
                    if (primaryKey[k] == recordRAF.read()) {
                        counter++;
                    }
                }
                recordRAF.seek(i+j+13-1);
                if (counter == primaryKey.length && recordRAF.read() != 49) {
                    recordRAF.read(searchResult,0,typeLength-1);
                    recordRAF.close();
                    return byteToString(searchResult);
                }
            }
        }
        recordRAF.close();
        return "no result";
    }

    public static ArrayList<String> list_all_records(RandomAccessFile systemCatRAF) throws IOException{
        System.out.println("insert the type name");
        String name = scanner.next();
        RandomAccessFile recordRAF = new RandomAccessFile(name.toUpperCase() + ".txt", "rw");

        ArrayList<String> returnArray = new ArrayList<>();
        ArrayList<byte[]> list = new ArrayList<>();
        int typeLength = findFieldNumber(systemCatRAF,name)*16+1;

        for (int i = 13; i < recordRAF.length(); i = i+1024) {
            for (int j = i; j < i + 1024 - 13 - typeLength; j = j + typeLength) {
                recordRAF.seek(j - 1);
                if (recordRAF.read() != 49) {
                    byte[] toString = new byte[typeLength-1];
                    recordRAF.read(toString, 0, typeLength-1);
                    list.add(toString);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            String temp = "";
            for (int j = 0; j < list.get(i).length; j++) {
                temp = temp + (char) list.get(i)[j];
            }
            returnArray.add(temp);
            //System.out.println(temp);
        }
        recordRAF.close();
        return returnArray;
    }

    public static String byteToString(byte[] bytes) {
        String returnString = "0";
        for (int i = 0; i < bytes.length; i++) {
            returnString = returnString + (char) bytes[i];
        }
        return returnString;
    }

    public static int byteToInt(byte[] bytes) {
        Scanner scanner = new Scanner(byteToString(bytes));
        int ret = scanner.nextInt();
        scanner.close();
        return ret;

    }
    public static int findFieldNumber(RandomAccessFile systemCatRAF , String typeName)throws IOException{
        ArrayList<String> listOfAllTypes = list_all_types(systemCatRAF);
        int fieldNumber = 0;
        for (int i = 0; i < listOfAllTypes.size(); i++) {
            String temp = listOfAllTypes.get(i);
            Scanner tempScanner = new Scanner(temp);
            String next = tempScanner.next();
            if (next.equalsIgnoreCase(typeName)) {
                while (tempScanner.hasNext()) {
                    tempScanner.next();
                    fieldNumber++;
                }
            }
        }
        return fieldNumber;
    }

}
