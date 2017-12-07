package edu.nwu.sakai.studentlink.server;

public enum ModuleLinkStatus {
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