
package io.github.tuxmonteiro.planclab.tests.suites;


import io.github.tuxmonteiro.planclab.tests.cucumber.CucumberTest;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CucumberTest.class
})
public class ManagerTestSuite {

    @AfterClass
    public static void after() {
    }
}
