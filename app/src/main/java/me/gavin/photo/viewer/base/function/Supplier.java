package me.gavin.photo.viewer.base.function;

/**
 * () -> T
 *
 * @author gavin.xiong 2017/8/10
 */
@FunctionalInterface
public interface Supplier<T> {

    T get();
}
