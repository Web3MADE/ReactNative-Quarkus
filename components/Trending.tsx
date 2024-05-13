import { ResizeMode, Video } from "expo-av";
import React, { useState } from "react";
import {
  FlatList,
  Image,
  ImageBackground,
  TouchableOpacity,
  ViewToken,
} from "react-native";
import * as Animatable from "react-native-animatable";
import localVideo from "../assets/videos/mockShort.mp4";
import { icons } from "../constants";

interface ITrendingItemProps {
  activeItem: any;
  item: any;
}

interface ITrendingProps {
  posts: any;
}

const zoomIn = {
  0: {
    scale: 0.9,
  },
  1: {
    scale: 1.1,
  },
};

const zoomOut = {
  0: {
    scale: 1.1,
  },
  1: {
    scale: 0.9,
  },
};

const TrendingItem = ({ activeItem, item }: ITrendingItemProps) => {
  const [play, setPlay] = useState(false);
  console.log("Active item ", activeItem);
  console.log("Item ", item);
  // animate the active item to zoom in and out when active
  return (
    <Animatable.View
      className="mr-5"
      animation={activeItem === item.id ? zoomIn : zoomOut}
      duration={500}
    >
      {play ? (
        <Video
          source={localVideo}
          className="w-52 h-72 rounded-[35px] mt-3 bg-white/10"
          resizeMode={ResizeMode.CONTAIN}
          shouldPlay
          useNativeControls
          onPlaybackStatusUpdate={(status) => {}}
        />
      ) : (
        <TouchableOpacity
          className="relative justify-center items-center"
          activeOpacity={0.7}
          // disable play for now until video is implemented
          onPress={() => setPlay(true)}
        >
          {/* ImageBackground  */}
          <ImageBackground
            source={{ uri: item.thumbnail }}
            className="w-52 h-72 rounded-[35px] my-5 overflow-hidden shadow-lg shadow-black/40"
            resizeMode="cover"
          />
          <Image
            source={icons.play}
            className="w-12 h-12 absolute"
            resizeMode="contain"
          />
          {/* Text */}
        </TouchableOpacity>
      )}
    </Animatable.View>
  );
};
const Trending = ({ posts }: ITrendingProps) => {
  const [activeItem, setActiveItem] = useState(posts[1].id);
  // TODO: fix the active item not updating
  const viewableItemsChanged = (viewableItems: ViewToken[]) => {
    console.log("viewableItems called ", viewableItems);
    if (viewableItems.length > 0) {
      console.log("viewableItems length > 0 ", viewableItems);
      setActiveItem(viewableItems[0].key);
    }
  };

  return (
    <FlatList
      data={posts}
      keyExtractor={(item) => item.id}
      renderItem={({ item }) => (
        <TrendingItem activeItem={activeItem} item={item} />
      )}
      horizontal
      onViewableItemsChanged={({ viewableItems }) =>
        viewableItemsChanged(viewableItems)
      }
      viewabilityConfig={{
        itemVisiblePercentThreshold: 70,
      }}
      contentOffset={{ x: 170, y: 0 }}
    />
  );
};

export default Trending;
