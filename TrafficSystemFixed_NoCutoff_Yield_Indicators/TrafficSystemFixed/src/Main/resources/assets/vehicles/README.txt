Vehicle sprites used by JavaFxVehicleView.
All PNGs face EAST. The renderer rotates them using the immutable heading copied
from VehicleRenderState.

Run with: mvn clean javafx:run
Choose "Sprite xe" in the Hiển thị box. The status bar also shows the active mode.
If a sprite cannot be loaded, a magenta X appears instead of silently falling
back to the old colored rectangle.
