package event;

public class EventProducer {
    ListenerRegister register = new ListenerRegister();
    private Object value;
    private String b;
    private String c;
    private String e;


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

    public void addListener(ValueChangeListener valueChangeListeneraa) {
        register.addListener(valueChangeListeneraa);
    }

    public void removeListener(ValueChangeListener avalueChangeListeneraa) {
        register.removeListener(avalueChangeListeneraa);
    }

    public void fireAEvent(ValueChangeEvent valueChangeListeneraa) {
        register.fireAEvent(valueChangeListeneraa);
    }

    public void aa(ValueChangeEvent event) {
        register.fireAEvent(event);
    }


}