import React, { useEffect } from "react";
import { FlatList, Image, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import EmptyState from "../../components/EmptyState";
import SearchInput from "../../components/SearchInput";
import Trending from "../../components/Trending";
import VideoCard from "../../components/VideoCard";
import { images } from "../../constants";
import { useVideos } from "../hooks/useVideos";
const mockImage = "https://picsum.photos/200";
const mockImage2 = "https://picsum.photos/id/237/200/300";
const mockImage3 = "https://picsum.photos/id/238/200/300";
const mockVideo = "https://www.tiktok.com/@web3made/video/7363899884509908256";
export const mockPosts = [
  {
    id: "1",
    title: "The Matrix",
    thumbnail: mockImage,
    video: mockVideo,
    avatar: mockImage,
    creator: "Neo",
  },
  {
    id: "2",
    title: "The Matrix Reloaded",
    thumbnail: mockImage2,
    video: mockVideo,
    avatar: mockImage2,
    creator: "Trinity",
  },
  {
    id: "3",
    title: "The Matrix TriLoaded",
    thumbnail: mockImage3,
    video: mockVideo,
    avatar: mockImage3,
    creator: "Morpheus",
  },
];
const Home = () => {
  // TODO: refetch videos on scroll up feature
  const { loading, videos, getAllVideos } = useVideos();

  useEffect(() => {
    const init = async () => {
      await getAllVideos();
    };
    init();
  }, []);

  // TODO: loading state for fetching posts

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
            creator={item.creator}
          />
        )}
        ListHeaderComponent={() => (
          <View className="my-6 px-4 space-y-6">
            <View className="justify-between items-start flex-row mb-6">
              <View>
                <Text className="font-pmedium text-sm text-gray-100">
                  Welcome Back
                </Text>
              </View>
              <View className="mt-1.5">
                <Image
                  source={images.logoSmall}
                  className="w-9 h-10"
                  resizeMode="contain"
                />
              </View>
            </View>
            <SearchInput placeholder="Search for a topic" />

            <View className="w-full flex-1 pt-5 pb-8">
              <Text className="text-gray-100 text-lg font-pregular mb-3">
                Latest Videos
              </Text>
              <Trending posts={mockPosts} />
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

export default Home;
