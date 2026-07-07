#!/bin/bash
sed -i '/val sysInst = JSONObject()/i \        if (useThinking) {\n            val genConfig = JSONObject()\n            genConfig.put("thinkingLevel", "HIGH")\n            // Do not set maxOutputTokens for thinking\n            root.put("generationConfig", genConfig)\n        }\n' app/src/main/java/com/example/ui/AILabTabs.kt
