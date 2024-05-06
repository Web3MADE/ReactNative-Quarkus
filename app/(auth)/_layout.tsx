import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import React from "react";

// Only Auth screen and onboarding do not have bottom tabs
// hence it is necessary to create a separate layout for them
const AuthLayout = () => {
  return (
    <>
      <Stack>
        <Stack.Screen
          name="sign-in"
          options={{
            headerShown: false,
          }}
        />
        <Stack.Screen
          name="sign-up"
          options={{
            headerShown: false,
          }}
        />
        <StatusBar backgroundColor="#161622" style="light" />
      </Stack>
    </>
  );
};

export default AuthLayout;
