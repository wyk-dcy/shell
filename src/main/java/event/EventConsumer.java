package event;

public class EventConsumer implements ValueChangeListener {

    @Override
    public void performed(ValueChangeEvent valueChangeEvent) {
        System.out.println("value changed, new value 新建的= " + ((Stu)valueChangeEvent.getValue()).getName());
        System.out.println("value changed, new value = " + ((Stu)valueChangeEvent.getValue()).getName());
    }
}