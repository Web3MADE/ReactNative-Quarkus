import { Link, useRouter } from "expo-router";
import React, { useState } from "react";
import { Image, ScrollView, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import CustomButton from "../../components/CustomButton";
import FormField from "../../components/FormField";
import { images } from "../../constants";
import storage from "../config/Storage";
const SignIn = () => {
  const router = useRouter();

  const [isLoading, setisLoading] = useState(false);
  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const submit = async () => {
    setisLoading(true);
    console.log("form ", form);
    const response = await fetch(`http://localhost:8080/api/users/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(form),
    })
      .then((response) => response.json()) // Convert response to JSON
      .then((data) => {
        console.log("Success:", data);
        const token = data.token; // Access the token from the response
        const userId = data.userId;
        // Store the token using AsyncStorage or similar for later use
        storage.save({ key: "token", data: token, expires: 1000 * 3600 * 24 });
        storage.save({
          key: "userId",
          data: userId,
          expires: 1000 * 3600 * 24,
        });
        setisLoading(false);
        router.push("/home");
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  };

  return (
    <SafeAreaView className="bg-primary h-full">
      <ScrollView>
        <View className="w-full justify-center min-h-[85vh] px-4 my-6">
          <Image
            source={images.logo}
            className="w-[130px] h-[84px]"
            resizeMode="contain"
          />
          <Text className="text-2xl text-white text-semibold mt-10 font-psemibold">
            Log in
          </Text>
          <FormField
            title="Email"
            value={form.email}
            handleChangeText={(e) => setForm({ ...form, email: e })}
            otherStyles="mt-7"
            // for auto-filling email information
            keyboardType="email-address"
          />
          <FormField
            title="Password"
            value={form.password}
            handleChangeText={(e) => setForm({ ...form, password: e })}
            otherStyles="mt-7"
          />
          <CustomButton
            title="Sign In"
            handlePress={submit}
            containerStyles="mt-7"
            // isLoading={isLoading}
          />
          <View className="flex flex-row justify-center mt-5 gap-x-2">
            <Text className="text-lg text-gray-100 font-pregular">
              No account?
            </Text>
            <Link
              href="/sign-up"
              className="text-lg font-psemibold text-secondary"
            >
              Sign Up
            </Link>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
};
export default SignIn;
