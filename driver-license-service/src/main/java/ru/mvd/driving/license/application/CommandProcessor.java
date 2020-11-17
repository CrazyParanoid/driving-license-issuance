package ru.mvd.driving.license.application;

public interface CommandProcessor<T extends Command> {

    void process(T command);

}
