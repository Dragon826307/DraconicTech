package dragon826307.dt.config;

public record ConfigValue(Object value) {
    //TODO
    public boolean asBoolean() {
        if (value instanceof Boolean) return (Boolean) value;
        throw new ClassCastException("Value is not a boolean");
    }

    public String asString() {
        if (value instanceof String) return (String) value;
        throw new ClassCastException("Value is not a String");
    }

    public int asInt() {
        if (value instanceof Integer) return (Integer) value;
        throw new ClassCastException("Value is not a int");
    }

    public float asFloat() {
        if (value instanceof Float) return (Float) value;
        throw new ClassCastException("Value is not a float");
    }

    public double asDouble() {
        if (value instanceof Double) return (Double) value;
        throw new ClassCastException("Value is not a double");
    }

    public char asChar() {
        if (value instanceof Character) return (Character) value;
        throw new ClassCastException("Value is not a char");
    }
}
