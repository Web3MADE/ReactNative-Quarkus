import { Link } from "expo-router";
import React from "react";
import { StyleSheet, Text, View } from "react-native";

const profile = () => {
  return (
    <View>
      <Text>profile</Text>
      <Link href="/profile" className="text-lime-700">
        Go to Profile
      </Link>
    </View>
  );
};

export default profile;

const styles = StyleSheet.create({});
