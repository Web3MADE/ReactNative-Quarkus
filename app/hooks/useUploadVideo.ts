import { QueryClient, useMutation } from "@tanstack/react-query";
import * as ImagePicker from "expo-image-picker";
import { GET_LIKED_VIDEOS_KEY } from "./useLikedVideos";

interface IUploadVideo {
  title: string;
  video: ImagePicker.ImagePickerAsset | null;
  thumbnail: ImagePicker.ImagePickerAsset | null;
  prompt: string;
}

const uploadVideo = async (form: IUploadVideo) => {
  const formData = new FormData();
  formData.append("title", form.title);
  formData.append("uploaderId", "1"); // TODO: get user id from auth context
  formData.append("video", form.video?.uri ?? "");
  formData.append("thumbnail", form.thumbnail?.uri ?? "");

  await fetch("http://localhost:8080/api/videos", {
    method: "POST",
    headers: {
      "Content-Type": "multipart/form-data",
    },
    body: formData,
  });
};

export function useUploadVideo() {
  const queryClient = new QueryClient();

  const {
    mutate: upload,
    isPending: isUploading,
    isError: isErrorUpload,
    isSuccess: isUploadSuccess,
    error: uploadError,
  } = useMutation({
    mutationFn: uploadVideo,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: GET_LIKED_VIDEOS_KEY });
    },
  });

  return { upload, isUploading, isErrorUpload, uploadError, isUploadSuccess };
}
