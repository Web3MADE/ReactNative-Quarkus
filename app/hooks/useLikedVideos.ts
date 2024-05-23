import { useQuery } from "@tanstack/react-query";

export const GET_LIKED_VIDEOS_KEY = ["GET_LIKED_VIDEOS"];

const fetchLikedVideos = async (userId: number) => {
  const response = await fetch(
    `http://localhost:8080/api/videos/liked/${userId}`
  );
  if (!response.ok) {
    throw new Error("Failed to fetch liked videos");
  }
  return response.json();
};

export function useLikedVideos(userId: number) {
  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: GET_LIKED_VIDEOS_KEY,
    queryFn: () => fetchLikedVideos(userId),
  });

  const videos = data ? data : [];

  return { isLoading, isError, videos, refetch };
}
