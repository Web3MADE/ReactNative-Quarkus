// the page is dynamic based on user search query
import { router, useLocalSearchParams } from "expo-router";
import React from "react";
import { FlatList, Image, Text, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import EmptyState from "../../components/EmptyState";
import SearchInput from "../../components/SearchInput";
import VideoCard from "../../components/VideoCard";
import { icons } from "../../constants";
import { useLikeVideo } from "../hooks/useLikeVideo";
import useSearchVideos from "../hooks/useSearchVideos";

const Search = () => {
  const { query } = useLocalSearchParams();
  const { videos } = useSearchVideos({
    query: query ? (query as string) : "",
  });
  const { likeVideo } = useLikeVideo();

  return (
    <SafeAreaView className="bg-primary h-full">
      <FlatList
        data={videos}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <VideoCard
            title={item.title}
            thumbnail={item.thumbnail}
            video={item.video}
            avatar={item.avatar}
            onLike={() => likeVideo({ videoId: Number(item.id), userId: 1 })}
          />
        )}
        ListHeaderComponent={() => (
          <View className="my-6 px-4 gap-3">
            <View>
              <TouchableOpacity onPress={() => router.push("/home")}>
                <Image
                  source={icons.leftArrow}
                  className="w-5 h-5"
                  resizeMode="contain"
                />
              </TouchableOpacity>
            </View>
            <Text className="font-pmedium text-sm text-gray-100">
              Search Results
            </Text>
            <Text className="text-2xl font-psemibold text-white">{query}</Text>

            <View className="mt-6 mb-8">
              <SearchInput initialQuery={query ? (query as string) : ""} />
            </View>
          </View>
        )}
        // render if list is empty
        ListEmptyComponent={() => (
          <EmptyState
            title="No Videos Found"
            subtitle="Be the first one to upload a video!"
          />
        )}
      />
    </SafeAreaView>
  );
};

export default Search;
