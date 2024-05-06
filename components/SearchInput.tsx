import React from "react";
import { Image, TextInput, TouchableOpacity, View } from "react-native";
import { icons } from "../constants";
interface ISearchInputProps {
  value?: string;
  placeholder?: string;
  handleChangeText: (text: string) => void;
  otherStyles?: string;
  keyboardType?: "default" | "email-address" | "numeric" | "phone-pad";
}
const SearchInput = ({
  value,
  placeholder,
  handleChangeText,
  otherStyles,
  keyboardType,
  ...props
}: ISearchInputProps) => {
  return (
    <View className="w-full h-16 px-4 bg-black-100 rounded-2xl border-2 border-black-200 focus:border-secondary flex flex-row items-center space-x-4">
      <TextInput
        className="flex-1 text-white font-pregular text-base"
        value={value}
        placeholder="Search for a topic"
        placeholderTextColor="#7B7B8B"
        onChangeText={handleChangeText}
        {...props}
        style={{ alignSelf: "center" }}
      />
      <TouchableOpacity>
        <Image source={icons.search} className="w-5 h-5" resizeMode="contain" />
      </TouchableOpacity>
    </View>
  );
};

export default SearchInput;
