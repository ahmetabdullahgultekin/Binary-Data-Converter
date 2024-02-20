/*
This program converts the given hexadecimal input to decimal output unsigned / signed integer or float as desired.

Ahmet Abdullah GULTEKIN 150121025
Melik OZDEMIR 150120004
Omer DELIGOZ 150120035
Mehmet Akif GULMUS 150119026
*/
//Necessary imports

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.pow;

public class P1_150121025_150120004_150120035_150119026 {

    //Global variables
    static ArrayList<String> results = new ArrayList<String>();
    static ArrayList<String[]> groups = new ArrayList<>();

    static int size;
    static char byteOrdering;
    static String dataType;

    //reverse the given array
    private static void reverseNumbers(String[] numbers) {
        for (int i = 0; i < numbers.length / 2; i++) {
            String temp = numbers[i];
            numbers[i] = numbers[numbers.length - i - 1];
            numbers[numbers.length - i - 1] = temp;
        }
    }

    //print the given string array
    private static void printNumbers(String[] numbers) {
        for (String number : numbers) {
            System.out.print(number + " ");
        }
        System.out.println();
    }


    //print the given arraylist
    private static void printGroups() { // <(xx xx xx xx),(xx xx xx xx),(xx xx xx xx)>
        for (String[] group : groups) {
            for (String number : group) {
                System.out.print(number + " ");
            }
            System.out.println();
        }
    }

    // Print floating point number
    private static void printResultsToFile() throws IOException {
        PrintWriter writer = new PrintWriter("output.txt");

        // for floats ->
        if (dataType.equals("float")) {
            // According to result create the output
            for (int i = 0; i < results.size(); i += (12 / size)) {
                for (int j = i; j < i + (12 / size) && j < results.size(); j++) {
                    String resultStr = results.get(j);

                    if (resultStr.equals("-inf") || resultStr.equals("inf") || resultStr.equals("NaN"))
                        writer.print(resultStr);
                    else {
                        double doubleResult = Double.parseDouble(resultStr);
                        if (resultStr.equals("0.0"))
                            writer.print(0);
                        else if (resultStr.equals("-0.0"))
                            writer.print("-" + 0);
                        else if (doubleResult <= -10 || doubleResult >= 10 || (doubleResult >= -1 && doubleResult <= 1))
                            writer.printf("%.5e", doubleResult);
                        else
                            writer.printf("%.5f", doubleResult);
                    }
                    if (j < i + (12 / size) - 1)
                        writer.print(" ");
                }
                if (i < (results.size() - (12 / size)) - 1)
                    writer.println();
            }
        } else {
            for (int i = 0; i < results.size(); i += (12 / size)) {
                for (int j = i; j < i + (12 / size) && j < results.size(); j++) {
                    writer.print(results.get(j));
                    if (j < i + (12 / size) - 1)
                        writer.print(" ");
                }
                if (i < (results.size() - (12 / size)) - 1)
                    writer.println();
            }
        }
        writer.close();
    }

    // Get the input and insert the values to global array size by size
    private static void readFile(File file) throws FileNotFoundException {
        // Local variables
        Scanner input = new Scanner(file);
        String line;
        String[] numbers;

        //read until end of file
        while (input.hasNext()) {
            // Get line
            line = input.nextLine();
            // Get the numbers byte by byte
            numbers = line.split(" ");

            //split numbers into groups
            for (int i = 0; i < numbers.length; i += size) {
                String[] group = new String[size];
                for (int j = 0; j < size && i + j < numbers.length; j++) {
                    group[j] = numbers[i + j];
                }
                groups.add(group);
            }
        }

        //for little indian
        if (byteOrdering == 'l') {
            //reverse all numbers
            for (String[] arr : groups) {
                reverseNumbers(arr);
            }
        }
    }

    // Process will go on by data type
    private static void calculateNumbers() {
        switch (dataType) {
            case "unsigned" -> calculateUnsigned();
            case "int" -> calculateInt();
            case "float" -> calculateFloat();
        }
    }

    // Determine binary representation of given parameter
    private static String convertToBinary(String[] str) {
        //concat numbers
        String result = "";
        for (String s : str) {
            result += s;
        }

        // Convert hex to binary through string
        String binaryString = Long.toBinaryString(Long.parseLong(result, 16));
        binaryString = String.format("%" + (size * 8) + "s", binaryString).replace(' ', '0');

        return binaryString;
    }

    // Compute the float case
    private static void calculateFloat() {

        // Local variables
        String binaryString, exponentStr, mantissaStr, result = "";
        int bias, exponent, signBit, exponentSize = 0;
        double mantissa;

        // Assign exponent size according to number of bytes
        switch (size) {
            case 1 -> exponentSize = 4;
            case 2 -> exponentSize = 6;
            case 3 -> exponentSize = 8;
            case 4 -> exponentSize = 10;
        }

        for (String[] group : groups) {

            // Convert group of numbers to binary
            // Assign parts of the number like mantissa, exponent etc. to variables
            binaryString = convertToBinary(group);
            signBit = Character.getNumericValue(binaryString.charAt(0));
            exponentStr = binaryString.substring(1, exponentSize + 1);
            mantissaStr = binaryString.substring(exponentSize + 1);


            // Denormalized situation if exponent bits are full of zeros
            if (!exponentStr.contains("1")) {
                bias = ((int) pow(2, exponentSize - 1)) - 1;
                exponent = 1 - bias;
                mantissa = 0 + calculateMantissa(mantissaStr);
                result += pow(-1, signBit) * mantissa * pow(2, exponent);
                results.add(result);
                result = "";

            } else if (!exponentStr.contains("0")) {
                if (!mantissaStr.contains("1") && signBit == 0) {
                    results.add("inf");

                } else if (!mantissaStr.contains("1") && signBit == 1) {
                    results.add("-inf");

                } else if (mantissaStr.contains("1")) {
                    results.add("NaN");
                }
            } else { // Normalized situation
                bias = ((int) pow(2, exponentSize - 1)) - 1;
                exponent = binaryToDecimal(exponentStr);
                mantissa = 1 + calculateMantissa(mantissaStr);
                result += pow(-1, signBit) * mantissa * pow(2, exponent - bias);
                results.add(result);
                result = "";
            }
        }
    }

    private static double calculateMantissa(String str) {
        double mantissa = 0;
        String binaryString;
        int mantissaInt;

        if (size == 3 || size == 4) {
            if (str.charAt(13) == '1' && str.substring(14).contains("1")) {         //greater than half way (round up)
                mantissaInt = binaryToDecimal((str.substring(0, 13)));
                mantissaInt++;
                if (!(str.substring(0, 13).contains("0"))) { //all 1's
                    mantissa = 1.0;
                    return mantissa;
                } else {
                    binaryString = Long.toBinaryString(mantissaInt);
                    binaryString = String.format("%13s", binaryString).replace(' ', '0');
                }


            } else if (str.charAt(13) == '1' && !str.substring(14).contains("1")) { //half way  (round to even)
                if (str.charAt(12) == '1') { //round up
                    mantissaInt = binaryToDecimal((str.substring(0, 13)));
                    mantissaInt++;
                    if (!(str.substring(0, 13).contains("0"))) { //all 1's
                        mantissa = 1.0;
                        return mantissa;
                    } else {
                        binaryString = Long.toBinaryString(mantissaInt);
                        binaryString = String.format("%13s", binaryString).replace(' ', '0');
                    }
                } else {   //round down
                    binaryString = str.substring(0, 13);
                }
            } else {                                                                        //less than half way   (round down)
                binaryString = str.substring(0, 13);

            }
        } else { // size == 3 || size == 4
            binaryString = str;
        }

        //Calculate mantissa
        for (int i = 0; i < binaryString.length(); i++) {
            int digit = Character.getNumericValue(binaryString.charAt(i));
            mantissa += digit * pow(2, -(i + 1));
        }
        return mantissa;
    }


    private static int binaryToDecimal(String str) {
        int decimalNumber = 0;
        for (int i = 0; i < str.length(); i++) {
            int digit = Character.getNumericValue(str.charAt(i));
            decimalNumber += digit * pow(2, (str.length() - 1 - i));
        }
        //return number as a string
        return decimalNumber;
    }

    private static void calculateUnsigned() {
        String binaryString;
        long result = 0;
        int digit;
        for (String[] arr : groups) {
            binaryString = convertToBinary(arr);
            for (int i = 0; i < binaryString.length(); i++) {
                digit = Character.getNumericValue(binaryString.charAt(binaryString.length() - 1 - i));
                result += digit * pow(2, i);
            }
            results.add(result + "");
            result = 0;
        }
    }

    private static void calculateInt() {
        String binaryString;
        long result = 0;
        int digit;
        for (String[] arr : groups) {
            binaryString = convertToBinary(arr);
            for (int i = 0; i < binaryString.length() - 1; i++) {
                digit = Character.getNumericValue(binaryString.charAt(binaryString.length() - 1 - i));
                result += digit * pow(2, i);
            }

            //starts with 1
            if (binaryString.charAt(0) == '1') {
                digit = Character.getNumericValue(binaryString.charAt(0));
                result -= digit * pow(2, binaryString.length() - 1);
            }
            //starts with zero
            else {
                digit = Character.getNumericValue(binaryString.charAt(0));
                result += digit * pow(2, binaryString.length() - 1);
            }
            results.add(result + "");
            result = 0;
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        File inputFile = null;

        try {
            String inputFileName = input.next();
            inputFile = new File(inputFileName);
            byteOrdering = input.next().charAt(0);
            dataType = input.next();
            size = input.nextInt();
            if ((byteOrdering == 'l' || byteOrdering == 'b')
                    && (dataType.equals("float") || dataType.equals("int") || dataType.equals("unsigned"))
                    && (size == 1 || size == 2 || size == 3 || size == 4)) {

                readFile(inputFile);
                calculateNumbers();
                printResultsToFile();
            } else {
                System.out.println("Invalid input!");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}