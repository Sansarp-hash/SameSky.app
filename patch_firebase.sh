#!/bin/bash
sed -i '/implementation(libs.firebase.ai)/a \  implementation("com.google.firebase:firebase-auth-ktx")\n  implementation("com.google.firebase:firebase-firestore-ktx")\n  implementation("com.google.android.gms:play-services-auth:20.7.0")' app/build.gradle.kts
