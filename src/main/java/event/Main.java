package event;

public class Main {

    public static void main(String[] args) {
        EventProducer producer = new EventProducer();
        producer.addListener(new EventConsumer());
        Stu stu = new Stu("吴永康");
        producer.setValue(stu);
        stu.setName("788");
        producer.setValue(stu);
    }
}