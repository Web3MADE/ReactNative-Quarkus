import { useState } from "react";
import { IVideo } from "./useVideos";

export function useVideosByUploader() {
  const [loading, setLoading] = useState(false);
  const [videos, setVideos] = useState<IVideo[]>([]);

  const getVideosByUploader = async (uploaderId: number) => {
    setLoading(true);
    try {
      const res = await fetch(
        `http://localhost:8080/api/videos/uploader/${uploaderId}`
      );
      const data = await res.json();

      const mappedData = data.map((video: any) => ({
        id: video.id,
        title: video.title,
        video: video.url,
        thumbnail: video.thumbnailUrl,
        likes: video.likes,
      }));

      setVideos(mappedData);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return { loading, videos, getVideosByUploader };
}
