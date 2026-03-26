# Morse

An Android Morse code translator and messenger that uses **Clipt** as its real-time communication backbone. Type text, it converts to Morse and beeps it out — and sends it over the network to anyone else tuned to the same channel.

## What It Does

- Translates text to Morse code (dots and dashes) and vice versa
- Plays the Morse code as audible beeps via `Beeper`
- Sends encoded messages over a Socket.IO connection to a Clipt server
- Receives incoming Morse messages from the channel and auto-translates them back to text on tap
- Channel selection and server URL configurable via a settings dialog

The app connects to `clipt.azurewebsites.net` by default — Clipt was a personal Socket.IO-based messaging platform. Messages are scoped to a named channel, so two devices on the same channel exchange Morse in real time.

## Architecture

| File | Purpose |
|------|---------|
| `MainActivity.java` | UI — input field, convert button, message list, socket lifecycle |
| `Converter.java` | Bidirectional Morse↔text translation using lookup tables |
| `Beeper.java` | Plays dot/dash/space audio sequences |
| `MorseOptions.java` | Persisted settings (server URL, channel name) |
| `SettingsDialog.java` | Settings dialog fragment |
| `ActiveElement.java` | Tracks which list item is currently expanded (showing alpha vs. Morse) |
| `Helper.java` | Shared utilities — preferences, message persistence |

## How to Run

Open in Android Studio. Requires an active Clipt/Socket.IO server. Update the server URL in settings if you're hosting your own. Build and run on Android 5.0+.

## Notes

Built in 2016 as part of the Clipt ecosystem. The Converter handles the full standard Morse alphabet including punctuation. Tap any message in the list to toggle between Morse display and translated text.
