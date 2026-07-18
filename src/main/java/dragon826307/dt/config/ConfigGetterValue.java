package dragon826307.dt.config;

import dragon826307.dt.DraconicTech;

public record ConfigGetterValue(Object value) {
    //TODO
    public boolean asBoolean() {
        if (value instanceof Boolean) return (Boolean) value;
        else return fallback(value, Boolean.class);
    }

    public String asString() {
        if (value instanceof String) return (String) value;
        else return fallback(value, String.class);
    }

    public long asLong() {
        if (value instanceof Long) return (Long) value;
        else return fallback(value, Long.class);
    }

    public int asInt() {
        if (value instanceof Integer) return (Integer) value;
        else return fallback(value, Integer.class);
    }

    public float asFloat() {
        if (value instanceof Float) return (Float) value;
        else return fallback(value, Float.class);
    }

    public double asDouble() {
        if (value instanceof Double) return (Double) value;
        else return fallback(value, Double.class);
    }

    public char asChar() {
        if (value instanceof Character) return (Character) value;
        else return fallback(value, Character.class);
    }
    @SuppressWarnings("unchecked")
    private static <T> T fallback(Object value, Class<T> clazz) {
        DraconicTech.LOGGER.warn("Fail to parse the value '{}' as {}", value, clazz.getName());
        if (clazz == int.class) return (T) Integer.valueOf(Integer.MIN_VALUE);
        if (clazz == long.class) return (T) Long.valueOf(Long.MIN_VALUE);
        if (clazz == double.class) return (T) Double.valueOf(Double.NaN);
        if (clazz == float.class) return (T) Float.valueOf(Float.NaN);
        if (clazz == boolean.class) return (T) Boolean.FALSE;
        if (clazz == byte.class) return (T) Byte.valueOf((byte) 0);
        if (clazz == short.class) return (T) Short.valueOf((short) 0);
        if (clazz == char.class) return (T) Character.valueOf('\u0000');
        throw new IllegalArgumentException();
    }
}
