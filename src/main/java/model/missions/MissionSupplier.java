package model.missions;

import java.util.function.Supplier;

public class MissionSupplier {
  public static final int NUMBER_OF_MISSIONS = 12;

  private static final Supplier<Mission>[] MISSION_SUPPLIERS = new Supplier[]{
      M1::new, M2::new, M3::new, M4::new, M5::new, M6::new,
      M7::new, M8::new, M9::new, M10::new, M11::new, M12::new
  };

  public static Mission createMission(int missionNumber) {
    if (missionNumber < 1 || missionNumber > MISSION_SUPPLIERS.length) {
      throw new IllegalArgumentException("Mission number must be between 1 and " + MISSION_SUPPLIERS.length);
    }
    return MISSION_SUPPLIERS[missionNumber - 1].get();
  }
}