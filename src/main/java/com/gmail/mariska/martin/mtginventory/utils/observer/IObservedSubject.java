package com.gmail.mariska.martin.mtginventory.utils.observer;


/**
 * Trida predstavujici subjekt v Observerable pattern
 * 
 * @author MAR
 * 
 * @param <T>
 *            udava typ Eventu ktery posila Subjekt observerum
 */
public interface IObservedSubject<T> {
    void addObserver(IObserver<T> o);

    void removeObserver(IObserver<T> o);

    void notifyObservers(T event);
}
