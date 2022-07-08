package event;

public class EventConsumer implements ValueChangeListener {

    @Override
    public void performed(ValueChangeEvent valueChangeEvent) {
        System.out.println("value changed, new value 新建的= " +"wyk");
        System.out.println("value changed, new value = bbb" + ((Stu)valueChangeEvent.getValue()).getName());
    }
}