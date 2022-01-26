package event;

public class EventConsumer implements ValueChangeListener {

    @Override
    public void performed(ValueChangeEvent e) {
        System.out.println("value changed, new value = " + ((Stu)e.getValue()).getName());
    }
}