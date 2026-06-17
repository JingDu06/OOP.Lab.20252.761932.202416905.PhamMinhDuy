# Custom emergency audio

The ambulance and fire-truck recordings provided by the user are included in:

- `src/main/resources/assets/sounds/original/ambulance-source.mp3`
- `src/main/resources/assets/sounds/original/firetruck-source.mp3`

For reliable playback through `javax.sound.sampled`, runtime copies are stored
as normalized PCM WAV files:

- `ambulance-siren.wav`
- `firetruck-siren.wav`

`VehicleController` reports whether each siren type is currently active.
`SoundManager` owns all playback state, loops the matching recording, and stops
it immediately when no matching emergency vehicle has its siren enabled.
