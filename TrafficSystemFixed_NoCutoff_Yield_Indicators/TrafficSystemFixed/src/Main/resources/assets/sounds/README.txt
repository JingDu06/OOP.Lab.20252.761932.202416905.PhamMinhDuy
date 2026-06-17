Runtime sound files used by util.SoundManager.

Emergency recordings supplied by the user:
- ambulance-siren.wav: converted from original/ambulance-source.mp3
- firetruck-siren.wav: converted from original/firetruck-source.mp3

The MP3 recordings are converted to mono PCM WAV at 22050 Hz because the
JDK javax.sound.sampled backend can play PCM WAV reliably without JavaFX Media.
The full recordings are retained and normalized for clearer playback.

Emergency sirens loop only while a matching vehicle has its siren enabled and
stop as soon as no matching emergency vehicle remains in the simulation.
Use the "Test âm thanh" button to hear the car horn, ambulance and fire truck.
