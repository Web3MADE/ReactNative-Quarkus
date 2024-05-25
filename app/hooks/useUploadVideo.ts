import { QueryClient, useMutation } from "@tanstack/react-query";
import { IUploadVideo } from "../(tabs)/create";
import { GET_LIKED_VIDEOS_KEY } from "./useLikedVideos";

const uriToBlob = async (uri: string) => {
  try {
    const response = await fetch(uri);
    if (!response.ok) {
      throw new Error(`Failed to fetch file: ${response.statusText}`);
    }
    const blob = await response.blob();
    return blob;
  } catch (error) {
    console.error("Error fetching file:", error);
    throw error;
  }
};

const uriToFile = async (uri: string, name: string, type: string) => {
  try {
    const blob = await uriToBlob(uri);
    const file = new File([blob], name, { type });
    return file;
  } catch (error) {
    console.error("Error converting Blob to File:", error);
    throw error;
  }
};
const uploadVideo = async (form: IUploadVideo) => {
  const formData = new FormData();
  formData.append("title", form.title);
  formData.append("uploaderId", "1"); // TODO: get user id from auth context
  formData.append("thumbnail", form.thumbnail?.uri ?? "");
  formData.append("video", form.video?.uri ?? "");
  console.log("form.video?.uri ", form.video?.uri);
  console.log("form.thumbnail?.uri ", form.thumbnail?.uri);

  // const videoFile = await uriToFile(
  //   form.video?.uri ?? "",
  //   "video.mp4",
  //   "video/mp4"
  // );
  // const thumbnailFile = await uriToFile(
  //   form.thumbnail?.uri ?? "",
  //   "thumbnail.jpg",
  //   "image/jpeg"
  // );
  // console.log("videoFile ", videoFile);
  // console.log("thumbnailFile ", thumbnailFile);
  // TODO:
  // formData not working with files, they contain data but the file itself if empty
  // Moving forward, we need to ensure the API logic is tested with desired file types
  // Then, the client side logic can be tested with the correct file types
  // formData.append("video", videoFile);
  // formData.append("thumbnail", thumbnailFile);

  console.log("formData ", formData);

  await fetch("http://localhost:8080/api/videos", {
    method: "POST",
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
