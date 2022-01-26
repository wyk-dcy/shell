package event;

public class EventProducer {
    ListenerRegister register = new ListenerRegister();
    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object newValue) {
        if (value != newValue) {
            value = newValue;
            ValueChangeEvent event = new ValueChangeEvent(this, value);
            fireAEvent(event);
        }
    }

    public void addListener(ValueChangeListener a) {
        register.addListener(a);
    }

    public void removeListener(ValueChangeListener a) {
        register.removeListener(a);
    }

    public void fireAEvent(ValueChangeEvent event) {
        register.fireAEvent(event);
    }

}