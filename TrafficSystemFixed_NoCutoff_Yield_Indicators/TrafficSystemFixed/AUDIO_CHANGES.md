# Emergency yield and audio

- Bus, car, motorbike and other non-emergency vehicles detect an ambulance or fire truck with an active siren on the same road.
- They move gradually to the right only when the target lane has safe front and rear gaps.
- A bus uses a longer lane-change path because of its length.
- If moving right is not safe, the yielding vehicle slows down instead of colliding or teleporting.
- Ambulances and fire trucks still obey collision spacing and cannot pass through other vehicles.

Audio files are under `src/main/resources/assets/sounds/`:

- `car-horn.wav`
- `ambulance-siren.wav`
- `firetruck-siren.wav`
- `turn-signal.wav`
- `traffic-light-change.wav`

`util.SoundManager` is responsible only for audio effects. It does not modify coordinates or vehicle behavior.
Use the **Âm thanh: Bật/Tắt** button in the toolbar to mute or enable all effects.
