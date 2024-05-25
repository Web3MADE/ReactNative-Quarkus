import { router, usePathname } from "expo-router";
import React, { useState } from "react";
import { Image, TextInput, TouchableOpacity, View } from "react-native";
import { icons } from "../constants";
interface ISearchInputProps {
  initialQuery?: string;
  placeholder?: string;
}
const SearchInput = ({ initialQuery, placeholder }: ISearchInputProps) => {
  const pathname = usePathname();
  const [query, setQuery] = useState(initialQuery || "");

  function handleQuery(e: string) {
    setQuery(e);
  }

  return (
    <View className="w-full h-16 px-4 bg-black-100 rounded-2xl border-2 border-black-200 focus:border-secondary flex flex-row items-center space-x-4">
      <TextInput
        className="flex-1 text-white font-pregular text-base"
        value={query}
        placeholder={placeholder}
        placeholderTextColor="#CDCDE0"
        onChangeText={handleQuery}
        style={{ alignSelf: "center" }}
      />
      <TouchableOpacity
        onPress={() => {
          if (pathname.startsWith("/search")) {
            router.setParams({ query });
          } else {
            router.push(`/search/${query}`);
          }
        }}
      >
        <Image source={icons.search} className="w-5 h-5" resizeMode="contain" />
      </TouchableOpacity>
    </View>
  );
};

export default SearchInput;
