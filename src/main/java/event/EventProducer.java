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

    public void addListener(ValueChangeListener anrrtyiur) {
        register.addListener(anrrtyiur);
    }


    public void fireAEvent(ValueChangeEvent sssss) {
        register.fireAEvent(sssss);
    }

    public void add (){
        System.out.println("我要策划hi");
    }
}