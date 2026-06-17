# Emergency priority update

- Bus maximum speed increased from 4.2 to 5.4; acceleration increased from 0.16 to 0.22.
- Every normal vehicle, including Bus, reacts to an Ambulance or FireTruck with an active siren.
- Yielding vehicles change one lane to the right with an S-curve only when the target lane has safe front/rear gaps.
- Emergency-yield lane changes ignore the emergency vehicle behind when checking the target lane, fixing the case where a bus started yielding but could not move.
- A Bicycle pulls farther toward the right edge, stops, waits until the emergency vehicle has passed by 78 px, then smoothly returns to the inner lane and continues.
- Ambulance and FireTruck track how long they are blocked by a vehicle in front. At 100 simulation ticks (10 seconds at the app's 100 ms update interval), they may start a safe lane-change overtake.
- Emergency overtaking is never teleportation: the adjacent lane must have safe gaps both ahead and behind, and normal overlap checks remain active.
