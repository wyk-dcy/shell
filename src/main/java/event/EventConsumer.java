package event;

public class EventConsumer implements ValueChangeListener {

    @Override
    public void performed(ValueChangeEvent e5645645) {
        System.out.println("value changed, new value = " + ((Stu)e5645645.getValue()).getName());
        System.out.println("你再说什么");
    }
}