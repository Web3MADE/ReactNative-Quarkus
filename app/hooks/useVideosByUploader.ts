import { useQuery } from "@tanstack/react-query";

const GET_UPLOADER_VIDEOS_KEY = "GET_UPLOADER_VIDEOS";

const fetchVideosByUploader = async (uploaderId: number) => {
  const response = await fetch(
    `http://localhost:8080/api/videos/uploader/${uploaderId}`
  );
  if (!response.ok) {
    throw new Error("Failed to fetch videos");
  }
  return response.json();
};

export default function useVideosByUploader(uploaderId: number) {
  const { data, isError, isLoading } = useQuery({
    queryKey: [GET_UPLOADER_VIDEOS_KEY],
    queryFn: () => fetchVideosByUploader(uploaderId),
  });

  const videos = data ? data : [];

  return { isLoading, isError, videos };
}
