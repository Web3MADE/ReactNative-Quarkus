import { ResizeMode, Video } from "expo-av";
import * as ImagePicker from "expo-image-picker";
import React, { useState } from "react";
import {
  Alert,
  Image,
  ScrollView,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import CustomButton from "../../components/CustomButton";
import FormField from "../../components/FormField";
import { icons } from "../../constants";
import { useUploadVideo } from "../hooks/useUploadVideo";

export interface IUploadVideo {
  title: string;
  video: ImagePicker.ImagePickerAsset | null;
  thumbnail: ImagePicker.ImagePickerAsset | null;
  prompt: string;
}

const Create = () => {
  const { upload, isUploading, isErrorUpload, uploadError, isUploadSuccess } =
    useUploadVideo();
  const [form, setform] = useState<IUploadVideo>({
    title: "",
    video: null,
    thumbnail: null,
    prompt: "",
  });

  const openPicker = async (selectType: "video" | "image") => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes:
        selectType === "video"
          ? ImagePicker.MediaTypeOptions.Videos
          : ImagePicker.MediaTypeOptions.Images,
      aspect: [4, 3],
      quality: 1,
    });

    if (!result.canceled) {
      if (selectType === "video") setform({ ...form, video: result.assets[0] });
      else setform({ ...form, thumbnail: result.assets[0] });
    }
  };
  const submit = async () => {
    if (!isFormValid(form)) {
      return Alert.alert("Error", "Please fill all fields");
    }
    upload(form);
    // router.push("/home");

    if (isErrorUpload) {
      Alert.alert("Error", "An error occurred while uploading the post ");
      throw new Error(`error occured during upload: ${uploadError})`);
    }

    if (isUploadSuccess) {
      Alert.alert("Success", "Post uploaded successfully");
      setform({
        title: "",
        video: null,
        thumbnail: null,
        prompt: "",
      });
    }
  };

  return (
    // SafeAreaView is only applicable to iOS devices and renders
    // a view that respects the device's top status bar and front camera notch
    <SafeAreaView className="bg-primary h-full">
      <ScrollView className="px-4 my-6">
        <Text className="text-2xl text-white font-bold">Upload video</Text>

        <FormField
          title="Video Title"
          value={form.title}
          placeholder="Give your video a catchy title"
          handleChangeText={(e) => setform({ ...form, title: e })}
          otherStyles="mt-10"
        />

        <View className="mt-7 space-y-2">
          <Text className="text-base text-gray-100 font-pmedium">
            Upload Video
          </Text>
          <TouchableOpacity onPress={() => openPicker("video")}>
            {form.video ? (
              <Video
                source={{ uri: form.video.uri }}
                className="w-full h-64 rounded-2xl"
                resizeMode={ResizeMode.COVER}
              />
            ) : (
              <View className="w-full h-40 px-4 bg-black-100 rounded-2xl justify-center items-center">
                <View className="w-14 h-14 border border-dashed border-secondary-100 justify-center items-center">
                  <Image
                    source={icons.upload}
                    resizeMode="contain"
                    className="w-1/2 h-1/2"
                  />
                </View>
              </View>
            )}
          </TouchableOpacity>
        </View>
        <View className="mt-7 space-y-2">
          <Text className="text-base text-gray-100 font-pmedium">
            Thumbnail Image
          </Text>

          <TouchableOpacity onPress={() => openPicker("image")}>
            {form.thumbnail ? (
              <Image
                source={{ uri: form.thumbnail.uri }}
                className="w-full h-64 rounded-2xl"
                resizeMode="cover"
              />
            ) : (
              <View className="w-full h-16 px-4 bg-black-100 rounded-2xl border-2 border-black-200 flex justify-center items-center flex-row space-x-2">
                <Image
                  source={icons.upload}
                  resizeMode="contain"
                  className="w-5 h-5"
                />
                <Text className="text-sm text-gray-100 font-pmedium">
                  Choose a file
                </Text>
              </View>
            )}
          </TouchableOpacity>
        </View>

        <FormField
          title="AI Prompt"
          value={form.prompt}
          placeholder="The AI prompt of your video..."
          handleChangeText={(e) => setform({ ...form, title: e })}
          otherStyles="mt-7"
        />

        <CustomButton
          title="Submit & Publish"
          handlePress={submit}
          containerStyles="mt-7"
          isLoading={isUploading}
        />
      </ScrollView>
    </SafeAreaView>
  );
};

function isFormValid(form: IUploadVideo) {
  return Object.values(form).every((value) => value !== null || value !== "");
}

export default Create;
