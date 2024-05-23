import React, { useEffect } from "react";
import { FlatList, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import SearchInput from "../../components/SearchInput";
import VideoCard from "../../components/VideoCard";
import { useLikedVideos } from "../hooks/useLikedVideos";
import { getUserId } from "../utils/getUserId";
//TODO: implement bookmark scaffold UI
const bookmark = () => {
  const { loading, videos, getLikedVideos } = useLikedVideos();

  useEffect(() => {
    const init = async () => {
      const userId = getUserId();

      // TODO later: once auth is reimplemented push to login
      if (!userId) {
        console.error("User not logged in. No userID found.");
        // router.push("/login");
      }

      await getLikedVideos(1);
    };
    init();
  }, []);

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
