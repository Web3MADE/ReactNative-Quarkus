import { useState } from "react";

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
// TODO refactor: update VideoDTO to match IVideo (same with UserDTO)
export function useVideos() {
  const [loading, setLoading] = useState(false);
  const [videos, setVideos] = useState<IVideo[]>([]);

  const getAllVideos = async () => {
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8080/api/videos");
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

  return { loading, videos, getAllVideos };
}
