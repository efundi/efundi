package edu.nwu.sakai.studentlink.client;

public class Utility {

    /**
     * A simple method that generates the hashCode by taking the sum of the parameter hashCodes. If
     * a parameter value is null a default number(old nr. 7) is used.
     */
    public static int hashCode(Object... objects) {
        int hash = 0;
        for (Object obj : objects) {
            hash += obj == null ? 7 : obj.hashCode();
        }
        return hash;
    }

    /**
     * A method that tests for String equality.
     */
    public static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }

    /**
     * A method that tests for Integer equality.
     */
    public static boolean equals(Integer a, Integer b) {
        return a == null ? b == null : a.equals(b);
    }
}