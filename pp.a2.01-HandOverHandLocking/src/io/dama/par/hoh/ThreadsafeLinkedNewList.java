package io.dama.par.hoh;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadsafeLinkedNewList<T> implements NewList<T> {
	private class ListElement<T> {
		private T element;
		private ListElement<T> prev;
		private final ListElement<T> next;
		private ReentrantLock lockfirst = new ReentrantLock();

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
	public T get(final int i) {
		int j = 0;
		ListElement<T> ptr = this.first;

		while (j++ < i) {

			ptr.next.lockfirst.lock();
			ptr.lockfirst.lock();

			try {

				ptr = ptr.next;
			} finally {
				ptr.lockfirst.unlock();
				ptr.prev.lockfirst.unlock();
			}
		}

		return ptr.element;
	}

	@Override
	public synchronized void add(final T e) {
		final ListElement<T> insert = new ListElement<>(e, null, this.first);
		if (this.first != null) {
			this.first.prev = insert;
		}
		this.first = insert;
		System.out.println(insert.element);
	}

	@Override
	public void mod(final int i, final T e) {
		int j = 0;
		ListElement<T> ptr = this.first;
	
	
		while (j++ < i) {
			
			ptr.lockfirst.lock();
			ptr.next.lockfirst.lock();
			
			try {
				
			ptr = ptr.next;

			}finally {
				ptr.lockfirst.unlock();
				ptr.prev.lockfirst.unlock();
			}
		}
		ptr.element = e;
		
	}

}
