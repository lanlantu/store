package com.cy.store.util.annotion;

public class TestController {


    @MyAutowired
    private TestService testService;

    public TestService getTestService() {
        return testService;
    }


}
