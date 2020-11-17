package ru.mvd.driving.license.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Testservice {

    @Transactional
    public void test(){
        System.out.println("DA!!!");
    }
}
