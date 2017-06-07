# MergeCSV
Java application for merging two CSV files with the same header and outputs merged file and conflicts record.
Click [here][https://www.useloom.com/share/7d6b17ffaa074789a6de974f5fdadce4] for the demo of the program.

## Input & Output formats

The two input CSV files should have the same header (the first lines of each file should be the same). **All the blanks in the input files should be filled in "N/A"**.

In the output ```conflict.csv``` files, two entries of the conflicting column are saved in one column in the format of 

>(conflict_value1/conflict_value2)

### Example

Input file1.csv:

| Name  | Gender | Age  |
| ----- | ------ | ---- |
| Mike  | Male   | 14   |
| Alice | Male   | 25   |
| John  | Female | N/A  |

Input file2.csv:

| Name  | Gender | Age  |
| ----- | ------ | ---- |
| Mike  | Female | 14   |
| Alice | N/A    | 16   |
| JoJo  | Male   | 0    |

merged.csv:

| Name  | Gender | Age  |
| ----- | ------ | ---- |
| Mike  | Male   | 14   |
| John  | Female | N/A  |
| Alice | Male   | 25   |
| JoJo  | Male   | 0    |

conflict.csv:

| Name  | Gender        | Age     |
| ----- | ------------- | ------- |
| Mike  | (Male/Female) | 14      |
| Alice | Male          | (25/16) |



## Compile

Actually there's no need to compile the file, cause there's already a target JAR file ```Releaf.jar``` under the ```out``` folder. But FYI about compiling the program:

1. Make sure JRE 1.7+ is installed on your testing computer
2. Make sure Maven for dependency management is installed

In your terminal, go to the directory of ```./src/main/java/MergeCSV.java```, then type:

> javac MergeCSV.java

The target JAR file will be created under the ```out``` folder.

## Run

In your terminal, go to the directory where ```Releaf.jar``` lies, then type:

> java -cp Releaf.jar MergeCSV

Choose CSV files following the instructions of the popped up dialogs, you will find the generated ```merged.csv``` and ```conflict.csv``` under the same directory of the JAR file.

