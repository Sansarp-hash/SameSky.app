#!/bin/bash
sed -i '/    object PollArchive/a \    object AILab : Screen("ai_lab", "AI Lab", Icons.Filled.Science)' app/src/main/java/com/example/MainActivity.kt

sed -i '/        HubItem("Stars Premium"/i \        HubItem("AI Lab (Gemini)", "Chatbots, Image Gen, Search Grounding", Icons.Filled.Science, ShimmeringGold, Screen.AILab.route),' app/src/main/java/com/example/MainActivity.kt

