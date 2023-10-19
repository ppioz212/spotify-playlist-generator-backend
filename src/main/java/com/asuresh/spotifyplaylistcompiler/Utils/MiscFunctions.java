package com.asuresh.spotifyplaylistcompiler.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MiscFunctions {
    public static List<String> mergeToUniqueList(List<String> listOne, List<String> listTwo) {
        for (String item : listTwo) {
            if (!listOne.contains(item)) {
                listOne.add(item);
            }
        }
        return new ArrayList<>(listOne);
    }

    public static List<String> addUniqueStringToList(List<String> list, String item) {
        if (list == null) {
            return new ArrayList<>();
        }
        if (!list.contains(item)) {
            list.add(item);
        }
        return list;
    }

    public static String getInput(String message) {
        System.out.println(message);
        Scanner keyboard = new Scanner(System.in);
        return keyboard.nextLine();
    }

    public static Boolean fileExists(String filename) {
        File f = new File(filename);
        return f.exists();
    }

    public static String readFrom(String filename) throws IOException {
        return new Scanner(new File(filename)).useDelimiter("\\Z").next();
    }

    public static void writeToFile(String input, String filename) throws IOException {
        FileWriter myWriter = new FileWriter(filename);
        myWriter.write(input);
        myWriter.close();
    }

    public static String checkIfNextURLAvailable(JSONObject object) {
        if (object.isNull("next")) {
            return null;
        }
        return object.getString("next");
    }
}
