import { useFonts } from "expo-font";
import { SplashScreen, Stack } from "expo-router";
import React, { useEffect } from "react";
// prevent app from auto-hiding splash screen BEFORE asset loading is completed
SplashScreen.preventAutoHideAsync();

// TODO: Finish RN tutorial
// Fonts not importing correctly...
const RootLayout = () => {
  const [loaded, error] = useFonts({
    "Poppins-Black": require("../assets/fonts/Poppins-Black.ttf"),
    // "Poppins-Bold": require("../assets/fonts/Poppins-Bold.ttf"),
    // "Poppins-ExtraBold": require("../assets/fonts/Poppins-ExtraBold.ttf"),
    // "Poppins-ExtraLight": require("../assets/fonts/Poppins-ExtraLight.ttf"),
    // "Poppins-Light": require("../assets/fonts/Poppins-Light.ttf"),
    // "Poppins-Medium": require("../assets/fonts/Poppins-Medium.ttf"),
    // "Poppins-Regular": require("../assets/fonts/Poppins-Regular.ttf"),
    // "Poppins-SemiBold": require("../assets/fonts/Poppins-SemiBold.ttf"),
    // "Poppins-Thin": require("../assets/fonts/Poppins-Thin.ttf"),
  });

  useEffect(() => {
    // throw error if fonts haven't loaded

    if (error) throw error;
    // hideAsync = ensure app has content pre-loaded or else a blank screen will appear
    if (loaded) SplashScreen.hideAsync();
  }, [loaded, error]);

  return (
    <Stack>
      <Stack.Screen name="index" options={{ headerShown: false }} />
      <Stack.Screen name="(auth)" options={{ headerShown: false }} />
      <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
      <Stack.Screen name="search/[query]" options={{ headerShown: false }} />
    </Stack>
  );
};

export default RootLayout;
