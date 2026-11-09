This bridge is compiled separately and dexed into `assets/colormotion-native.dex`. `NativeLayers` loads it with SystemUI's class loader as its parent, so Compose state, graphics-layer scopes and notification views resolve to Samsung's real types. It talks to the module only through bootstrap types (`Object`, `Runnable`, primitive suppliers and arrays). If its startup probe fails, the module falls back to its reflective binding.

`nativeApi` holds compile-only stubs for the members it uses; they are never packaged.
