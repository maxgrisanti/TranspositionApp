package com.example.transposescan;

import com.example.transposescan.EditNotes;

public class SheetTransposer {

    // intKey and finalKey are the keys transposing to and from, these go into the transpose class
    // fileName is the name of the original file
    // newFileName is the name of the file that will be output

    public SheetTransposer(String intKey, String finalKey, String fileName, String newFileName) {

        Transpose transposer = new Transpose(intKey, finalKey);

        EditNotes updatedXML = new EditNotes(fileName, newFileName, transposer);
    }
}
