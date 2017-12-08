package za.ac.nwu.model;

public enum Status {
    INSERTED, DELETED, DONE;

    @Override
    public String toString() {
        //Make the first letter capital and the rest lower case.
        String enumName = super.toString();
        char firstLetter = enumName.charAt(0);
        StringBuilder sb = new StringBuilder(enumName.toLowerCase());
        sb.replace(0, 1, String.valueOf(firstLetter));
        return sb.toString();
    }
}