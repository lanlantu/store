package com.cy.store.util.annotion;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public class TestMain {
    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("springmvc-servlet.xml");
        TestController testController = new TestController();
        Class<? extends TestController> clazz = testController.getClass();


        Stream.of(clazz.getDeclaredFields()).forEach(field -> {
            String name = field.getName();
            MyAutowired annotation = field.getAnnotation(MyAutowired.class);
            if (annotation != null) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                try {
                    Object o = type.newInstance();
                    field.set(testController, o);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println(testController.getTestService());


    }
}
