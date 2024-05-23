import React from "react";
import { FlatList, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import SearchInput from "../../components/SearchInput";
import VideoCard from "../../components/VideoCard";
import { useLikedVideos } from "../hooks/useLikedVideos";
const bookmark = () => {
  // TODO: auth context for user id
  const { videos } = useLikedVideos(1);

  return (
    <SafeAreaView className="bg-primary h-full">
      <FlatList
        data={videos}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <VideoCard
            title={item.title}
            thumbnail={item.thumbnail}
            video={item.video}
            avatar={item.avatar}
            onLike={() => {}}
          />
        )}
        ListHeaderComponent={() => (
          <View className="my-6 px-4 space-y-6">
            <View className="justify-between items-start flex-row mb-6">
              <View>
                <Text className="text-2xl text-white font-bold">
                  Saved Videos
                </Text>
              </View>
            </View>
            <SearchInput placeholder="Search your saved videos" />
          </View>
        )}
      />
    </SafeAreaView>
  );
};

export default bookmark;
