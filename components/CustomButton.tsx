import React from "react";
import { Text, TouchableOpacity } from "react-native";

interface ICustomButtonProps {
  title: string;
  handlePress: () => void;
  containerStyles?: string;
  textStyles?: string;
  isLoading?: boolean;
}
// Touchable opacity, not techincally a button
const CustomButton = ({
  title,
  handlePress,
  containerStyles,
  textStyles,
  isLoading,
}: ICustomButtonProps) => {
  return (
    <TouchableOpacity
      className={`${containerStyles}${
        isLoading ? "opacity-50" : ""
      } bg-secondary rounded-xl min-h-[62px] justify-center items-center`}
      onPress={handlePress}
      disabled={isLoading}
    >
      <Text className={`${textStyles} text-primary font-psemibold text-lg`}>
        {title}
      </Text>
    </TouchableOpacity>
  );
};

export default CustomButton;
