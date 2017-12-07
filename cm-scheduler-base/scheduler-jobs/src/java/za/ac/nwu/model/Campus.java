package za.ac.nwu.model;

public enum Campus {
    POTCHEFSTROOM("1"), VAALDRIEHOEK("2"), MAFIKENG("9");

    private String number;

    Campus(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public String getCode() {
        String code = null;
        if ("1".equals(number)) {
            code = "P";
        }
        else if ("2".equals(number)) {
            code = "V";
        }
        else if ("9".equals(number)) {
            code = "M";
        }
        return code;
    }
}
