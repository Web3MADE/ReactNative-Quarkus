import { useState } from "react";

interface IRefresh {
  refetch: () => void;
}

export default function useRefresh() {
  const [refreshing, setRefreshing] = useState(false);

  const onRefresh = ({ refetch }: IRefresh) => {
    setRefreshing(true);
    refetch();
    setRefreshing(false);
  };

  return { refreshing, onRefresh };
}
