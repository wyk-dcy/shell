package event;

public class EventConsumer implements ValueChangeListener {

    @Override
    public void performed(ValueChangeEvent valueChangeEvent) {
        System.out.println("value changed, new value 新建的= ");
        System.out.println("value changed is wyk, new value = " + ((Stu)valueChangeEvent.getValue()).getName());
    }
}