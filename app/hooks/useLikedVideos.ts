import { useState } from "react";
import { IVideo } from "./useVideos";

export function useLikedVideos() {
  const [loading, setLoading] = useState(false);
  const [videos, setVideos] = useState<IVideo[]>([]);

  const getLikedVideos = async (userId: number) => {
    try {
      setLoading(true);
      const res = await fetch(
        `http://localhost:8080/api/videos/liked/${userId}`
      );
      const data = await res.json();
      const mappedData = data.map((video: any) => ({
        id: video.id,
        title: video.title,
        video: video.url,
        thumbnail: video.thumbnailUrl,
        likes: video.likes,
        isLiked: true,
      }));

      setVideos(mappedData);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  return { loading, videos, getLikedVideos };
}
