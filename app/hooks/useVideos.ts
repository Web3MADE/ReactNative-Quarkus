import { useQuery } from "@tanstack/react-query";
import { mapVideos } from "../utils/mapper";

export interface IVideo {
  id: number;
  title: string;
  thumbnail: string;
  video: string;
  avatar?: string;
  creator?: string;
  likes?: number;
  isLiked?: boolean;
}

const GET_ALL_VIDEOS_KEY = ["GET_ALL_VIDEOS"];

const fetchAllVideos = async () => {
  const response = await fetch("http://localhost:8080/api/videos");
  if (!response.ok) {
    throw new Error("Failed to fetch videos");
  }
  return response.json();
};

export default function useVideos() {
  const { data, refetch, isError, isLoading } = useQuery({
    queryKey: GET_ALL_VIDEOS_KEY,
    queryFn: fetchAllVideos,
    refetchOnWindowFocus: true,
  });

  const videos = data ? mapVideos(data) : [];
  console.log("videos in useVideos", videos);

  return { isLoading, isError, videos, refetch };
}
