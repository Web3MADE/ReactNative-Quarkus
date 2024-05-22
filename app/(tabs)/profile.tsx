// the page is dynamic based on user search query
import React, { useEffect } from "react";
import { FlatList, Image, TouchableOpacity, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import EmptyState from "../../components/EmptyState";
import InfoBox from "../../components/InfoBox";
import VideoCard from "../../components/VideoCard";
import { icons } from "../../constants";
import { useVideosByUploader } from "../hooks/useVideosByUploader";
import { getUserId } from "../utils/getUserId";

const mockAvatar = "https://picsum.photos/200";
const Profile = () => {
  const { loading, videos, getVideosByUploader } = useVideosByUploader();

  useEffect(() => {
    const init = async () => {
      const userId = getUserId();

      // TODO later: once auth is reimplemented push to login
      if (!userId) {
        console.error("User not logged in. No userID found.");
        // router.push("/login");
      }

      await getVideosByUploader(1);
    };
    init();
  }, []);

  const logout = () => {};

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
            avatar={item.thumbnail}
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
