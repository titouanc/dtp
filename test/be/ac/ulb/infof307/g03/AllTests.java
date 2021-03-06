package be.ac.ulb.infof307.g03;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import be.ac.ulb.infof307.g03.models.TestModels;
import be.ac.ulb.infof307.g03.utils.exporter.TestA3DSExporter;
import be.ac.ulb.infof307.g03.utils.parser.Test3DSParser;
import be.ac.ulb.infof307.g03.utils.parser.TestParsers;
import be.ac.ulb.infof307.g03.controllers.TestControllers;

@RunWith(Suite.class)
@SuiteClasses({ TestMain.class, TestModels.class, TestControllers.class, TestParsers.class, TestA3DSExporter.class})
public class AllTests {

}
