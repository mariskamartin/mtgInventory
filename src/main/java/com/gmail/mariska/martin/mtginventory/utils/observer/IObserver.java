package com.gmail.mariska.martin.mtginventory.utils.observer;

/**
 * Predstavuje observera v Observerable pattern
 * 
 * @author MAR
 * 
 * @param <T>
 *            udava typ Eventu ktery posila Subjekt observerum
 */
public interface IObserver<T> {

    void update(T event);

}
