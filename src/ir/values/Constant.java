package ir.values;

import ir.types.Type;

public abstract class Constant extends Value{
    public Constant(String name, Type type) {
        super(name, type);
    }
}
