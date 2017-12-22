package io.dama.par.hoh;

import java.util.concurrent.locks.Lock;

public class ThreadsafeLinkedNewList<T> implements NewList<T> {
    private class ListElement<T> {
        private T                    element;
        private ListElement<T>       prev;
        private final ListElement<T> next;
        private Lock lock;
        
        
        
        private ListElement(final T element, final ListElement<T> prev, final ListElement<T> next) {
            this.element = element;
            this.prev = prev;
            this.next = next;
        }
    }

    private ListElement<T> first;

    public ThreadsafeLinkedNewList() {
        this.first = null;
    }

    @Override
    public  T get(final int i) {
        int j = 0;
        ListElement<T> ptr = this.first;
        ListElement<T> tmp = null;
        //aktueller Node wird gelockt
        ptr.lock.lock();
        while (j++ < i) {
        	
        	try {
        		
        	ptr = ptr.next;
            tmp = ptr.prev;
            
            //der nächste Node wird gelockt
            ptr.lock.lock();
        
        	}
        	finally {
        		
        	    //der vorherige Node wird geunlockt
                tmp.lock.unlock();
        		
        	}        
        }        
        ptr.lock.unlock();
        return ptr.element;
    }

    @Override
    public  void add(final T e) {
        final ListElement<T> insert = new ListElement<>(e, null, this.first);
        insert.lock.lock();
        try {
        	if (this.first != null) {
   	            this.first.prev = insert;
   	        }
   	        this.first = insert;
        
        }finally {
        	insert.lock.unlock();
        }
	     
    }

    @Override
    public  void mod(final int i, final T e) {
        int j = 0;
        ListElement<T> ptr = this.first;
        ListElement<T> tmp = ptr;
        //Aktueller Node wird gelockt
        ptr.lock.lock();
        while (j++ < i) {
        	
        	try {
            
        	ptr = ptr.next;
        	tmp = ptr.prev;
        	//den nächsten Node locken
        	ptr.lock.lock();
            
        	}
        	finally {
        		tmp.lock.unlock();
        	}
        	
        }
        
        try {
        tmp = ptr.next;
        tmp.lock.lock();
        ptr.element = e;
        }
        finally {
        	tmp.lock.unlock();
        	ptr.lock.unlock();
        }
        }

}
