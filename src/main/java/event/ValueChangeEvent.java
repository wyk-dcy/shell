package event;

import java.util.EventObject;

public class ValueChangeEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 767352958358520268L;
    private Object value;

    public ValueChangeEvent(Object source) {
        this(source,0);
    }

    public ValueChangeEvent(Object source, Object newValue) {
        super(source);
        value = newValue;
    }

    public Object getValue() {
        return value;
    }
}