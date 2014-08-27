package com.gmail.mariska.martin.mtginventory.utils.observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Zakladni implementace observered Subjektu
 * 
 * @author MAR
 * 
 * @param <T>
 *            udava typ Eventu ktery posila Subjekt observerum
 */
public abstract class AbstractObservedSubject<T> implements IObservedSubject<T> {
    private List<IObserver<T>> observers = new ArrayList<IObserver<T>>();

    @Override
    public void addObserver(IObserver<T> o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(IObserver<T> o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(T event) {
        Iterator<IObserver<T>> i = observers.iterator();
        while (i.hasNext()) {
            IObserver<T> o = i.next();
            o.update(event);
        }
    }

}
