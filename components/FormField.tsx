import React, { useState } from "react";
import { Text, TextInput, View } from "react-native";
interface IFormFieldProps {
  title: string;
  value?: string;
  placeholder?: string;
  handleChangeText: (text: string) => void;
  otherStyles?: string;
  keyboardType?: "default" | "email-address" | "numeric" | "phone-pad";
}
const FormField = ({
  title,
  value,
  placeholder,
  handleChangeText,
  otherStyles,
  keyboardType,
  ...props
}: IFormFieldProps) => {
  const [showPassword, setShowPassword] = useState(false);
  return (
    <View className="space-y-2">
      <Text className="text-base text-gray-100 font-pmedium">{title}</Text>
      <View className="w-full h-16 px-4 bg-black-100 rounded-2xl border-2 border-black-200 focus:border-secondary flex flex-row items-center">
        <TextInput
          className="flex-1 text-white font-psemibold text-base"
          value={value}
          placeholder={placeholder}
          placeholderTextColor="#7B7B8B"
          onChangeText={handleChangeText}
          secureTextEntry={title === "Password" && !showPassword}
          {...props}
        />
      </View>
    </View>
  );
};

export default FormField;
