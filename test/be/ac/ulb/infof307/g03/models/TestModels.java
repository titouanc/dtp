package be.ac.ulb.infof307.g03.models;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    TestGeometricDAO.class, TestOrdered.class, TestBinding.class,TestEntity.class,
    TestMasterChanges.class ,TestPrimitive.class
})
public class TestModels {

}
