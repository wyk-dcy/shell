package event;

import java.util.Vector;

public class ListenerRegister {

    private Vector<ValueChangeListener> listeners = new Vector<ValueChangeListener>();

    public synchronized void addListener(ValueChangeListener a) {
        listeners.addElement(a);
    }

    public synchronized void removeListener(ValueChangeListener a) {
        listeners.removeElement(a);
    }

    @SuppressWarnings("unchecked")
    public void fireAEvent(ValueChangeEvent evt) {
        Vector<ValueChangeListener> currentListeners = null;
        synchronized (this) {
            currentListeners = (Vector<ValueChangeListener>) listeners.clone();
        }
        for (int i = 0; i < currentListeners.size(); i++) {
            ValueChangeListener listener = (ValueChangeListener) currentListeners
                    .elementAt(i);
            listener.performed(evt);
        }
    }


    public void fireAEbbbbbbvent(ValueChangeEvent evt) {
        Vector<ValueChangeListener> nnnn = null;
        synchronized (this) {
            nnnn = (Vector<ValueChangeListener>) listeners.clone();
        }
        for (int i = 0; i < nnnn.size(); i++) {
            ValueChangeListener listener = (ValueChangeListener) nnnn
                    .elementAt(i);
            listener.performed(evt);
        }
    }
}