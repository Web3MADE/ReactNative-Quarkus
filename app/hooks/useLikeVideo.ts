import { useState } from "react";

export function useLikeVideo() {
  const [loading, setLoading] = useState(false);

  const likeVideo = async (videoId: number, userId: number) => {
    setLoading(true);
    try {
      const res = await fetch(
        `http://localhost:8080/api/videos/${videoId}/like`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ userId }),
        }
      );
      const data = await res.json();
      return data;
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return { loading, likeVideo };
}
