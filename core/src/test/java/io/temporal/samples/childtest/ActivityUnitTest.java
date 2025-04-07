package io.temporal.samples.childtest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityUnitTest {
  private static final Logger logger = LoggerFactory.getLogger(ActivityUnitTest.class);

  @Test
  public void testFirstNumberActivity() {
    FirstNumberActivityImpl activity = new FirstNumberActivityImpl();
    int result = activity.getFirstNumber();
    logger.info("FirstNumberActivity result: {}", result);
    assertTrue(result >= 0 && result < 100, "First number should be between 0 and 99");
  }

  @Test
  public void testSecondNumberActivity() {
    SecondNumberActivityImpl activity = new SecondNumberActivityImpl();
    int result = activity.getSecondNumber();
    logger.info("SecondNumberActivity result: {}", result);
    assertTrue(result >= 0 && result < 100, "Second number should be between 0 and 99");
  }

  @Test
  public void testThirdNumberActivity() {
    ThirdNumberActivityImpl activity = new ThirdNumberActivityImpl();
    int result = activity.getThirdNumber();
    logger.info("ThirdNumberActivity result: {}", result);
    assertTrue(result >= 0 && result < 100, "Third number should be between 0 and 99");
  }
}
