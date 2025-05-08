package com.dannemann.stringcompressor.util;

@FunctionalInterface
public interface TriConsumer<T, U, V> {

	void accept(T t, U u, V v);

}
