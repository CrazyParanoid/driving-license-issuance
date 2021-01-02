package ru.mvd.driving.license.application;

public interface CommandProcessor<T extends Command, R> {

    R process(T command);

}
