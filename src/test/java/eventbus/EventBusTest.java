package eventbus;

import com.google.common.eventbus.Subscribe;
import junit.framework.TestCase;

import java.util.concurrent.Executors;

/**
 * @author wuyongkang
 * @date 2022年01月07日 14:59
 */
public class EventBusTest extends TestCase {
    private EventBus bus;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        bus = new EventBusImpl(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        bus.register(this);
    }


    public void testPostSync() {
        bus.post("test1", "New hello eventbus 10");

    }

    public void testPostAsync() {
        bus.post("test1", "New hello eventbus 10", false);

        System.out.println("调用事件");
    }

    public void testPullAndPost() {
        bus.post("test2", new PullNewMessageEvent(10));
    }

    @Subject("test1")
    @ParSubscribe
    public void handleString(String message) throws InterruptedException {
        System.out.println(message + "事件开始");
        Thread.sleep(1000);
        System.out.println(message + "事件结束");
    }

    @Subject("test2")
    @ParSubscribe
    public void getNewString(PullNewMessageEvent pullReq) {
        bus.post("test1", "New hello eventbus " + pullReq.getCount(),false);
        System.out.println(pullReq.getCount());
        System.out.println(pullReq.getCount());
        System.out.println("000000000000");
    }

    @Subscribe
    private void add(String str) {
        System.out.println("add" + str);
    }

    @Subscribe
    private void delete(String str) {
        System.out.println("delete" + str);
    }

    static class PullNewMessageEvent {
        private int count;

        PullNewMessageEvent(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }


}