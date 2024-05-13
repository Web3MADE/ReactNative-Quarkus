import { ResizeMode, Video } from "expo-av";
import React, { useState } from "react";
import { Image, Text, TouchableOpacity, View } from "react-native";
import localVideo from "../assets/videos/testMp4.mp4";
import { icons } from "../constants";
interface IVideoCardProps {
  title: string;
  creator?: string;
  avatar?: string; // image uri
  thumbnail: string; // image uri
  video?: string; // video uri
}

const VideoCard = ({
  title,
  creator,
  avatar,
  thumbnail,
  video,
}: IVideoCardProps) => {
  const [play, setPlay] = useState(false);
  console.log("uri thumb ", thumbnail);

  return (
    <View className="flex flex-col items-center px-4 mb-14">
      <View className="flex flex-row gap-3 items-start">
        <View className="flex justify-center items-center flex-row flex-1">
          <View className="w-[46px] h-[46px] rounded-lg border border-secondary flex justify-center items-center p-0.5">
            <Image
              source={{ uri: avatar }}
              className="w-full h-full rounded-lg"
              resizeMode="cover"
            />
          </View>

          <View className="flex justify-center flex-1 ml-3 gap-y-1">
            <Text
              className="font-psemibold text-sm text-white"
              numberOfLines={1}
            >
              {title}
            </Text>
            <Text className="text-xs text-gray-100 font-pregular">
              {creator}
            </Text>
          </View>
        </View>

        <View className="pt-2">
          <Image source={icons.menu} className="w-5 h-5" resizeMode="contain" />
        </View>
      </View>

      {play ? (
        <Video
          // TODO: replace with item.video once API is finalized
          source={localVideo}
          className="w-full h-60 rounded-xl mt-3"
          resizeMode={ResizeMode.CONTAIN}
          useNativeControls
          shouldPlay
        />
      ) : (
        <TouchableOpacity
          className="w-full h-60 rounded-xl mt-3 relative flex justify-center items-center"
          activeOpacity={0.7}
          onPress={() => setPlay(true)}
        >
          <Image
            source={{ uri: thumbnail }}
            className="w-ful h-full rounded-xl mt-3"
            resizeMode="cover"
          />
          <Image
            source={icons.play}
            className="w-12 h-12 absolute"
            resizeMode="contain"
          />
        </TouchableOpacity>
      )}
    </View>
  );
};

export default VideoCard;
