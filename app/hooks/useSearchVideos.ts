import { useQuery } from "@tanstack/react-query";

interface ISearchVideo {
  query: string;
}

const SEARCH_VIDEOS_KEY = ["SEARCH_VIDEOS"];
const searchVideos = async ({ query }: ISearchVideo) => {
  const response = await fetch(
    `http://localhost:8080/api/videos/search?query=${query}`
  );
  if (!response.ok) {
    throw new Error("Failed to fetch videos");
  }
  return response.json();
};

export default function useSearchVideos({ query }: ISearchVideo) {
  const {
    data,
    isError,
    isLoading,
    refetch: refetchQuery,
  } = useQuery({
    // any change to query key will trigger a new fetch
    // with the new query value (remember C&R, dynamic ids in queryKey)
    queryKey: [SEARCH_VIDEOS_KEY, query],
    queryFn: () => searchVideos({ query }),
  });

  const videos = data ? data : [];

  return { isLoading, isError, videos, refetchQuery };
}
