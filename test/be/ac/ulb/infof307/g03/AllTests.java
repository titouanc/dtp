package be.ac.ulb.infof307.g03;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import be.ac.ulb.infof307.g03.models.TestModels;
import be.ac.ulb.infof307.g03.controllers.TestControllers;

@RunWith(Suite.class)
@SuiteClasses({ TestMain.class, TestModels.class, TestControllers.class })
public class AllTests {

}
