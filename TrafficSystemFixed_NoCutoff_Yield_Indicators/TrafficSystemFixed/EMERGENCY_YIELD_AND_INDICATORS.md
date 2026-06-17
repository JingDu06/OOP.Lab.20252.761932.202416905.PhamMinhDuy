Changes in this build:
- All normal vehicles yield to ambulance/firetruck without cutting in front.
- During emergency yield, vehicles move right with reduced forward speed; if emergency vehicle is too close and no safe gap exists, they stop and let it pass.
- Rightmost-lane vehicles stop/slow to open a corridor instead of blocking the emergency vehicle.
- Added blinking turn indicators for lane changes (left/right), including emergency-yield lane changes and regular lane changes.
- Indicator state is part of the model snapshot and remains separated from JavaFX drawing logic.
