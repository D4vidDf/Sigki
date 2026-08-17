# SigKi

**SigKi** is a lightning-fast utility shortcut for Android that reimagines the standard digital assistant invocation. Instead of triggering generic AI chatbots, SigKi provides instant access to the tools, shortcuts, and actions you actually use every day.

The name is a combination of **"Sig"** (Signal) and **"Ki"** (the sharp cry of the Peregrine Falcon, the fastest animal on earth). It symbolizes the precision and speed of executing any action with a single signal from your phone.

---

## ✨ Features

- **Smart Multimedia Control**: Toggle Play/Pause or Stop. Set a *Preferred Media App* (like Spotify or YouTube Music) and SigKi will automatically launch it if no music is active.
- **Rich Screenshots**: Capture your screen and immediately share or edit using a modern, icon-only action bar with rich system previews.
- **Instant Contacts**: Speed-dial, message, or view the profile of your most important contacts with one gesture.
- **Camera & Flashlight**: Quick-access tools, including optimized camera launching from the lock screen.
- **App & URL Launcher**: Jump straight into your favorite application or any specific web destination.
- **System Toggles**: Effortlessly switch Do Not Disturb or TalkBack accessibility settings.

## 🎨 Design

SigKi is built with a **Material 3 Expressive** aesthetic, featuring:
- **Fluid Carousel**: A centered selector with smooth, proximity-based border animations.
- **Animated Option Panels**: Subtle fade transitions when navigating between utility settings.
- **Modern UI**: Clean, icon-centric interfaces designed for "one-hand" accessibility.
- **Multilingual**: Full support for **English** and **Spanish**.

## 🚀 How It Works

SigKi functions by implementing the Android `VoiceInteractionService` API. To get started:
1. Open your device **Settings**.
2. Search for **"Digital assistant app"**.
3. Select **SigKi** as your default assistant.
4. Open the **SigKi app** and scroll through the carousel to select your preferred action. 
5. Tap **"Options"** to configure your choice (e.g., setting a preferred media app or a target contact).
6. Trigger it instantly using your standard gesture (e.g., long-press home or swipe from bottom corners).

## 🛠️ Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: Modern Android Best Practices
- **APIs**: Assistant API, Media Session, Contacts Provider, FileProvider.

## ❓ FAQ

**Q: My device gestures stop working when selecting an assistant other than Gemini/Google.**
> **A:** Some OEMs (like Xiaomi) disable "swipe from corners" gestures for third-party assistant apps. If this happens, SigKi can still be triggered using alternate system shortcuts provided by your manufacturer, such as **long-pressing the power button** or using the **back-tap** gesture.

**Q: How can I contribute to the project?**
> **A:** You can help by **creating issues** for bugs or feature requests, or by submitting **pull requests** with improvements. Check the repository for the latest project needs.

## 🛡️ Privacy

SigKi is built with a **Privacy-First** philosophy. 
- **No Data Collection**: We do not track your usage or collect personal information.
- **Local Execution**: All actions are processed locally on your device.
- **Open Source**: The code is transparent and focused solely on utility.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*Speed up your workflow. Experience the power of the falcon.* 🦅
