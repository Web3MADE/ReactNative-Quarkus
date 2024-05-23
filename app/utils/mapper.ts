// TODO refactor: update VideoDTO to match IVideo (same with UserDTO)
export const mapVideos = (videos: any) => {
  return videos.map((video: any) => ({
    id: video.id,
    title: video.title,
    video: video.url,
    thumbnail: video.thumbnailUrl,
    likes: video.likes,
  }));
};
