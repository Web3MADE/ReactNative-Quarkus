import { QueryClient, useMutation } from "@tanstack/react-query";
import { GET_LIKED_VIDEOS_KEY } from "./useLikedVideos";

interface ILikeVideo {
  videoId: number;
  userId: number;
}

const postLikeVideo = async ({ videoId, userId }: ILikeVideo) => {
  await fetch(`http://localhost:8080/api/videos/${videoId}/like`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ userId }),
  });
};

export function useLikeVideo() {
  const queryClient = new QueryClient();
  const { mutate: likeVideo, isError: isErrorLikeVideo } = useMutation({
    mutationFn: postLikeVideo,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: GET_LIKED_VIDEOS_KEY });
    },
  });

  return { likeVideo, isErrorLikeVideo };
}
