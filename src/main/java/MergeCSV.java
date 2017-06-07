import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by RegulusXiang on 2017/6/5.
 */

public class MergeCSV {

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    private String header;// the header for the CSV file

    public Hashtable<String, ArrayList<String>> getFileOne() {
        return fileOne;
    }

    public void setFileOne(Hashtable<String, ArrayList<String>> fileOne) {
        this.fileOne = fileOne;
    }

    private Hashtable<String, ArrayList<String>> fileOne;

    public Hashtable<String, ArrayList<String>> getFileTwo() {
        return fileTwo;
    }

    public void setFileTwo(Hashtable<String, ArrayList<String>> fileTwo) {
        this.fileTwo = fileTwo;
    }

    private Hashtable<String, ArrayList<String>> fileTwo;

    public Hashtable<String, ArrayList<String>> getMerged() {
        return merged;
    }

    public void setMerged(Hashtable<String, ArrayList<String>> merged) {
        this.merged = merged;
    }

    private Hashtable<String, ArrayList<String>> merged; //the result after merging

    public Hashtable<String, ArrayList<String>> getConflicts() {
        return conflicts;
    }

    public void setConflicts(Hashtable<String, ArrayList<String>> conflicts) {
        this.conflicts = conflicts;
    }

    private Hashtable<String, ArrayList<String>> conflicts; //the conflicts of merging

    //constructor of the class
    public MergeCSV()
    {
        fileOne = new Hashtable<String, ArrayList<String>>();
        fileTwo = new Hashtable<String, ArrayList<String>>();
        merged = new Hashtable<String, ArrayList<String>>();
        conflicts = new Hashtable<String, ArrayList<String>>();
    }

    public void readCSV(String url, Hashtable<String, ArrayList<String>> file)
    {
        int line = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(url))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                //System.out.println(sCurrentLine);
                if(line == 0)
                {
                    header = sCurrentLine;
                    line++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //parse the first CSV file
        Reader in = null;
        try {
            in = new FileReader(url);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Iterable<CSVRecord> records = null;
        try {
            records = CSVFormat.EXCEL.withHeader().parse(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (CSVRecord record : records) {
            String key = record.get(0);
            //System.out.println("Key: " + key);

//            if(line == 0)
//            {
//                header = record.toString();
//                line++;
//            }

            ArrayList<String> value = new ArrayList<String>();
            for(int i = 1;i < record.size();i++)
            {
                value.add(record.get(i));
            }

            file.put(key, value);
        }

    }

    public void printFile(Hashtable<String, ArrayList<String>> file)
    {
        for(String key: file.keySet())
        {
            System.out.print("Key: " + key);
            System.out.print(", Value: ");
            for(String value: file.get(key))
            {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public void mergeFile()
    {
        //traverse file one
        for(String key: fileOne.keySet())
        {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%" + key);

            ArrayList<String> valueOne = fileOne.get(key);

            if(!fileTwo.containsKey(key)) //file two doesn't contain this key
            {
                System.out.println("CSV 2 doesn't contain key: " + key);

                //add this column to file merged
                merged.put(key, valueOne);
            }
            else //file two contains this key
            {
                ArrayList<String> valueTwo = fileTwo.get(key);

                System.out.println("Value for the second array list: " + valueTwo);

                ArrayList<String> valueThree = new ArrayList<>();
                ArrayList<String> conflict = new ArrayList<>();

                boolean hasConflict = false;

                //traverse each value of list one
                for(int i = 0;i < valueOne.size();i++)
                {
                    String one = valueOne.get(i);
                    String two = valueTwo.get(i);

                    System.out.println("One: " + one);
                    System.out.println("Two: " + two);

                    if(one.equals(two))
                    {
                        System.out.println("One equals two!");

                        valueThree.add(one);
                        conflict.add(one);//remember to add to conflict even when there is no conflict for this column
                    }
                    else
                    {
                        if(one.equals("N/A") && !two.equals("N/A"))
                        {
                            System.out.println("Choose the value from the second file: " + two);
                            valueThree.add(two);
                            conflict.add(two);
                        }
                        else if(two.equals("N/A") && !one.equals("N/A"))
                        {
                            System.out.println("Choose the value from the first file: " + one);
                            valueThree.add(one);
                            conflict.add(one);
                        }
                        else if(!two.equals("N/A") && !one.equals("N/A"))//there is a conflict
                        {
                            System.out.println("Has a conflict!");

                            hasConflict = true;
                            valueThree.add(one);
                            conflict.add("(" + one + "/" + two + ")");
                        }
                    }

                }

                System.out.println("--------------------Key: " + key + ", Value three: " + valueThree);

                merged.put(key, valueThree);

                if(hasConflict)
                {
                    conflicts.put(key, conflict);
                }
            }
        }

        //traverse file two to find missing keys
        for(String key: fileTwo.keySet())
        {
            if(!fileOne.containsKey(key))
            {
                merged.put(key, fileTwo.get(key));
            }
        }
    }

    //output to files
    public void outputCSV(Hashtable<String, ArrayList<String>> table, String url)
    {
        List<String> lines = new ArrayList<>();

        //add header to the output file
        lines.add(header);

        for(String key: table.keySet())
        {
            String line = key;

            for(String value: table.get(key))
            {
                line = line.concat("," + value);
            }

            lines.add(line);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(url), "utf-8"))) {

            for(String line: lines)
            {
                writer.write(line+"\n");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {

        //steps:
        //1. Read file 1 store it in one hash table;
        //2. Read file 2 store it in another hash table;
        //3. Merge two files to file 3 and store conflicts in file 4


        MergeCSV test = new MergeCSV();

        JFileChooser fd = new JFileChooser();
        fd.showDialog(null, "Select the first CSV");
        fd.setApproveButtonText("Select");
        File f = fd.getSelectedFile();
        String csvPath1 = f.getAbsolutePath();

        JFileChooser fdc = new JFileChooser();
        fdc.showDialog(null, "Select the second CSV");
        fdc.setApproveButtonText("Select");
        File f2 = fdc.getSelectedFile();
        String csvPath2 = f2.getAbsolutePath();

        //read two CSV files to be merged
        test.readCSV(csvPath1, test.getFileOne());
        test.readCSV(csvPath2, test.getFileTwo());

        test.printFile(test.getFileOne());
        test.printFile(test.getFileTwo());

        test.mergeFile();



        test.outputCSV(test.getMerged(), "./merged.csv");
        test.outputCSV(test.getConflicts(), "./conflict.csv");

    }

}
