package be.ac.ulb.infof307.g03.models;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestGeometry.class, TestBinding.class, TestPoint.class, 
	TestProject.class, TestFloor.class, TestMeshable.class, 
	TestRoom.class, TestEntity.class})
public class TestModels {

}
