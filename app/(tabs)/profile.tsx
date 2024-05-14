// the page is dynamic based on user search query
import { useLocalSearchParams } from "expo-router";
import React from "react";
import { FlatList, Image, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { mockPosts } from "../(tabs)/home";
import EmptyState from "../../components/EmptyState";
import InfoBox from "../../components/InfoBox";
import VideoCard from "../../components/VideoCard";
import { icons } from "../../constants";

const mockAvatar = "https://picsum.photos/200";
// This component renders a user's profile page with their uploaded videos
const Profile = () => {
  // TODO: implement search API functionality
  const { query } = useLocalSearchParams();

  const logout = () => {};

  return (
    <SafeAreaView className="bg-primary h-full">
      <FlatList
        data={mockPosts}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <VideoCard
            title={item.title}
            thumbnail={item.thumbnail}
            video={item.video}
            avatar={item.avatar}
          />
        )}
        ListHeaderComponent={() => (
          <View className="w-full justify-center items-center mt-6 mb-12 px-4">
            <TouchableOpacity
              className="flex w-full items-end mb-10"
              onPress={logout}
            >
              <Image
                source={icons.logout}
                resizeMode="contain"
                className="w-6 h-6"
              />
            </TouchableOpacity>

            <View className="w-16 h-16 border border-secondary rounded-lg justify-center items-center">
              <Image
                source={{ uri: mockAvatar }}
                className="w-[90%] h-[90%] rounded-lg"
                resizeMode="cover"
              />
            </View>

            <InfoBox
              title={"John Doe"}
              containerStyles="mt-5"
              titleStyles="text-lg"
            />

            <View className="mt-5 flex-row">
              <InfoBox
                title={"John Doe"}
                containerStyles="mr-4"
                titleStyles="text-lg"
                subtitle="Posts"
              />
              <InfoBox
                title={"John Doe"}
                containerStyles="mr-4"
                titleStyles="text-lg"
                subtitle="Followers"
              />
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

export default Profile;
