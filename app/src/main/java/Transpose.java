import java.util.ArrayList;

public class Transpose {

    private static String[] chromatic = { "C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#", "Ab", "A",
            "A#", "Bb", "B" };
    private static String[] notes = { "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B" };
    private static String[] sharps = {"F#", "C#", "G#", "D#", "A#", "E#", "B#"};
    private static String[] flats = {"Bb", "Eb#", "Ab", "Db", "Gb#", "Cb", "Fb"};
    private int shifter;

    /*
     * Constructs a transposer with a shift value according to the initial and final instruments
     * The shifter is used to shift each note the correct number of steps up or down
     * Only works for C, Bb, Eb, and F instruments
     */
    public Transpose(String key1, String key2) {

        switch (key1) {
            case "C":

                switch (key2) {
                    case "Bb":
                        shifter = 2;
                        break;
                    case "Eb":
                        shifter = -3;
                        break;
                    case "F":
                        shifter = -5;
                        break;
                }

                break;
            case "Bb":

                switch (key2) {
                    case "Eb":
                        shifter = 7;
                        break;
                    case "C":
                        shifter = -2;
                        break;
                    case "F":
                        shifter = 5;
                        break;
                }

                break;
            case "Eb":

                switch (key2) {
                    case "C":
                        shifter = -9;
                        break;
                    case "Bb":
                        shifter = -7;
                        break;
                    case "F":
                        shifter = -2;
                        break;
                }
            case "F":

                switch (key2) {
                    case "C":
                        shifter = -7;
                        break;
                    case "Bb":
                        shifter = -5;
                        break;
                    case "Eb":
                        shifter = 2;
                        break;
                }
        }
    }

    /*
     * Returns the index of a note in the static array, notes
     * If a flat or sharp note is not recognized, it is converted to one that is in the notes array
     */
    private static int noteIndex(String note) {

        for (int i = 0; i < notes.length; i++) {
            if (notes[i].toLowerCase().equals(note.toLowerCase()))
                return i;
        }

        int n = 0;
        while (n < 17) {
            if (chromatic[n].equals(note))
                break;
            n++;
        }

        if (note.substring(1).equals("b")) {
            return noteIndex(chromatic[n - 1]);
        }
        if (note.substring(1).equals("#")) {
            return noteIndex(chromatic[n + 1]);
        }

        return -1;
    }

    /*
     * Transposes a note by looping through the notes array according to the shifter
     * Returns the transposed note
     */
    public String transposeNote(String note) {

        int index = Transpose.noteIndex(note);
        return notes[(index + shifter + 12)%12];
    }

    /*
     * Loops through an array of notes and transposes them
     */
    public void transposeArray(ArrayList<String> notes) {

        for (int i = 0; i < notes.size(); i++) {
            notes.set(i, transposeNote(notes.get(i)));
        }
    }

    /*
     * Transposes the key using the transposeNote method
     * Takes in and outputs in int since in musicXML, keys are represented by an int
     * A positive int describes the number of sharps, a negative for flats, and 0 for no flats or sharps
     */
    public int transposeKey(int key) {
        String keyName = new String();

        if (key > 0) {
            switch (key) {
                case 1:
                    keyName = "G";
                    break;
                case 2:
                    keyName = "D";
                    break;
                case 3:
                    keyName = "A";
                    break;
                case 4:
                    keyName = "E";
                    break;
                case 5:
                    keyName = "B";
                    break;
                case 6:
                    keyName = "F#";
                    break;
                case 7:
                    keyName = "C#";
            }
        } else if (key < 0) {
            switch (key) {
                case -1:
                    keyName = "F";
                    break;
                case -2:
                    keyName = "Bb";
                    break;
                case -3:
                    keyName = "Eb";
                    break;
                case -4:
                    keyName = "Ab";
                    break;
                case -5:
                    keyName = "Db";
                    break;
                case -6:
                    keyName = "Gb";
                    break;
                case -7:
                    keyName = "Cb";
            }
        } else {
            keyName = "C";
        }

        String newKeyName = new String(transposeNote(keyName));

        switch (newKeyName) {
            case "C":
                return 0;
            case "F":
                return -1;
            case "G":
                return 1;
            case "Bb":
                return -2;
            case "D":
                return 2;
            case "Eb":
                return -3;
            case "A":
                return 3;
            case "Ab":
                return -4;
            case "E":
                return 4;
            case "Db":
                return -5;
            case "B":
                return 5;
            case "Gb":
                return -6;
            case "F#":
                return 6;
            case "Cb":
                return -7;
            case "C#":
                return 7;

        }

        return 0;
    }

    /**
     * Updates the notes according to the key (unnecessary flats and sharps removed)
     * @param notes
     * @param key
     */
    public void replaceAccordingToKey(ArrayList<String> notes, int key) {
        ArrayList<String> keyNotes = new ArrayList<String>();

        if(key>0) {
            for(int i=0; i<key; i++) {
                keyNotes.add(sharps[i]);
            }
        }
        else if(key<0) {
            for(int i=0; i>key; i--) {
                keyNotes.add(flats[0-i]);
            }
        }
        else {
            return;
        }

        for(int i=0; i<keyNotes.size(); i++) {
            for(int k=0; i<notes.size(); k++) {
                if(notes.get(k).equals(keyNotes.get(i))) {
                    notes.set(k, notes.get(k).substring(0,1));
                }
            }
        }
    }
}
